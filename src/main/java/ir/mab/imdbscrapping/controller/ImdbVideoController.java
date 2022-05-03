package ir.mab.imdbscrapping.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.Video;
import ir.mab.imdbscrapping.model.VideoGallery;
import ir.mab.imdbscrapping.util.AppConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static ir.mab.imdbscrapping.util.Utils.extractVideoId;
import static ir.mab.imdbscrapping.util.Utils.generateImage;

@RestController
@RequestMapping(path = AppConstants.Api.VIDEOS)
public class ImdbVideoController {

    @GetMapping("/{videoId}")
    @ApiOperation("Details of a Video (urls, runtime, ...)")
    ApiResponse<Video> fetchVideo(@ApiParam("Ex. vi2478686233") @PathVariable("videoId") String videoId) {
        Video video = new Video();
        try {
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_VIDEO + "%s", videoId)).get();
            JSONObject json = getJsonResponse(doc);
            extractVideoUrls(json, video);
            extractVideoInfo(json, video);
            extractRelatedVideos(doc, video);

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(video, null, true);
    }

    @GetMapping("/name/{nameId}/videogallery")
    @ApiOperation("Videos of a Name (Celebrities, Actors/Actress, Directors, ...)")
    ApiResponse<VideoGallery> fetchVideosOfName(
            @ApiParam("Ex. nm0000190")
            @PathVariable("nameId") String nameId,
            @ApiParam("Ex. date, duration, expiration")
            @RequestParam(value = "sort", required = false, defaultValue = "date") String sort,
            @ApiParam("Ex. desc, asc")
            @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir,
            @RequestParam(value = "page", required = false) Integer page
    ) {
        VideoGallery videoGallery = new VideoGallery();
        try {
            Document doc;
            if (page != null){
                doc = Jsoup.connect(String.format(AppConstants.IMDB_NAME + "%s/videogallery?sort=%s&sortDir=%s&page=%s", nameId,sort,sortDir,page)).get();
            }
            else {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_NAME + "%s/videogallery", nameId)).get();
            }

            try {
                videoGallery.setTitle(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("h3").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                videoGallery.setAvatar(generateImage(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("img").attr("src"),0,0));
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                List<VideoGallery.Video> videos = new ArrayList<>();
                for (Element element: doc.getElementsByClass("search-results").get(0).getElementsByTag("li")){
                    VideoGallery.Video video = new VideoGallery.Video();
                    try {
                        video.setId(element.getElementsByTag("a").attr("data-video"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        video.setCover(generateImage(element.getElementsByTag("a").get(0).getElementsByTag("img").attr("loadLate"),400,300));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        video.setTitle(element.getElementsByTag("h2").text());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    videos.add(video);
                }
                videoGallery.setVideos(videos);
            }catch (Exception e){
                e.printStackTrace();
            }

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(videoGallery, null, true);
    }

    @GetMapping("/title/{titleID}/videogallery")
    @ApiOperation("Videos of a Title (Movies, Tv Shows, ...)")
    ApiResponse<VideoGallery> fetchVideosOfTitle(
            @ApiParam("Ex. tt0111161")
            @PathVariable("titleID") String titleID,
            @ApiParam("Ex. date, duration, expiration")
            @RequestParam(value = "sort", required = false, defaultValue = "date") String sort,
            @ApiParam("Ex. desc, asc")
            @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir,
            @RequestParam(value = "page", required = false) Integer page
    ) {
        VideoGallery videoGallery = new VideoGallery();
        try {
            Document doc;
            if (page != null){
                doc = Jsoup.connect(String.format(AppConstants.IMDB_TITLE + "%s/videogallery?sort=%s&sortDir=%s&page=%s", titleID,sort,sortDir,page)).get();
            }
            else {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_TITLE + "%s/videogallery", titleID)).get();
            }

            try {
                videoGallery.setTitle(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("h3").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                videoGallery.setAvatar(generateImage(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("img").attr("src"),0,0));
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                List<VideoGallery.Video> videos = new ArrayList<>();
                for (Element element: doc.getElementsByClass("search-results").get(0).getElementsByTag("li")){
                    VideoGallery.Video video = new VideoGallery.Video();
                    try {
                        video.setId(element.getElementsByTag("a").attr("data-video"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        video.setCover(generateImage(element.getElementsByTag("a").get(0).getElementsByTag("img").attr("loadLate"),400,300));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        video.setTitle(element.getElementsByTag("h2").text());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    videos.add(video);
                }
                videoGallery.setVideos(videos);
            }catch (Exception e){
                e.printStackTrace();
            }

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(videoGallery, null, true);
    }

    private void extractVideoUrls(JSONObject json, Video video) {
        List<String> urls = new ArrayList<>();
        try {
            JSONArray playbackUrls = json.getJSONArray("playbackURLs");

            for (int i=0; i<playbackUrls.length(); i++ ){
                if (playbackUrls.getJSONObject(i).getString("mimeType").equals("video/mp4") || playbackUrls.getJSONObject(i).getString("url").contains("mp4")){
                    urls.add(playbackUrls.getJSONObject(i).getString("url"));
                }
            }

            video.setPlaybackUrls(urls);
        } catch (Exception e) {
            e.printStackTrace();
            video.setPlaybackUrls(urls);
        }

        try {
            video.setHighQ(urls.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            video.setLowQ(urls.get(urls.size() - 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractRelatedVideos(Element element, Video video) {
        List<Video.RelatedVideo> relatedVideos = new ArrayList<>();

        try {

            Element relatedVideoElement = element.getElementsByAttributeValue("data-testid","related-videos").first();

            for (Element item : Objects.requireNonNull(relatedVideoElement).getElementsByClass("ipc-slate-card")){
                Video.RelatedVideo relatedVideo = new Video.RelatedVideo();

                try {
                    relatedVideo.setVideoId(extractVideoId(item.getElementsByClass("ipc-slate-card__title").attr("href")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    relatedVideo.setTitle(item.getElementsByClass("ipc-slate-card__title-text").text());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    relatedVideo.setDuration(item.getElementsByClass("ipc-lockup-overlay__text").text());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    relatedVideo.setCover(item.getElementsByClass("ipc-media").first().getElementsByTag("img").attr("srcset"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    relatedVideo.setSubtitle(item.getElementsByClass("ipc-slate-card__subtitle2").text());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    //relatedVideo.setTitleId(videoListItem.getString("relationId"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                relatedVideos.add(relatedVideo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        video.setRelatedVideos(relatedVideos);
    }

    private void extractVideoInfo(JSONObject json, Video video) {

        try {
            video.setVideoCover(json.getJSONObject("thumbnail").getString("url"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            video.setVideoDescription(json.getJSONObject("description").getString("value"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            int seconds = json.getJSONObject("runtime").getInt("value");
            video.setVideoRuntime(String.format("%d:%02d", seconds/60, seconds%60));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            video.setVideoSubTitle(json.getJSONObject("name").getString("value"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            video.setVideoTitle(json.getJSONObject("name").getString("value"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject primaryTitle = json.getJSONObject("primaryTitle");
            try {
                List<String> genres = new ArrayList<>();
                for (Object genre : primaryTitle.getJSONObject("genres").getJSONArray("genres")) {
                    genres.add(((JSONObject) genre).getString("text"));
                }
                video.setRelationGenres(genres);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                video.setTitleId(primaryTitle.getString("id"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                video.setRelationPoster(primaryTitle.getJSONObject("primaryImage").getString("url"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                video.setRelationIMDbRating(primaryTitle.getJSONObject("ratingsSummary").getDouble("aggregateRating"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                video.setRelationIsIMDbTVTitle(primaryTitle.getJSONObject("titleType").getString("id").equals("tvSeries"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                video.setRelationIsReleased(primaryTitle.getJSONObject("canRate").getBoolean("isRatable"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                video.setRelationCertificateRatingsBody(primaryTitle.getJSONObject("certificate").getString("rating"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                video.setRelationCertificateRating(primaryTitle.getJSONObject("certificate").getString("rating"));

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                int seconds = primaryTitle.getJSONObject("runtime").getInt("seconds");
                video.setRelationRuntime(String.format("%d:%02d", seconds/60, seconds%60));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                video.setRelationReleaseYear(String.valueOf(primaryTitle.getJSONObject("releaseYear").getInt("year")));

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                video.setRelationReleaseDate(String.valueOf(primaryTitle.getJSONObject("releaseYear").getInt("year")));

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                video.setRelationTitle(primaryTitle.getJSONObject("originalTitleText").getString("text"));

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJsonResponse(Document doc) {
        String json = doc.getElementsByAttributeValue("id", "__NEXT_DATA__").get(0).data();
        return new JSONObject(json).getJSONObject("props").getJSONObject("pageProps").getJSONObject("videoPlaybackData").getJSONObject("video");
    }

}
