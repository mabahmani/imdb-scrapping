package ir.mab.imdbscrapping.controller;

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

@RestController
@RequestMapping(path = AppConstants.Api.VIDEOS)
public class ImdbVideoController {

    @GetMapping("/{videoId}")
    ApiResponse<Video> fetchVideo(@PathVariable("videoId") String videoId) {
        Video video = new Video();
        try {
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_VIDEO + "%s", videoId)).get();
            Element iMDbVideoExperienceJSElement = extractIMDbVideoExperienceJS(doc);
            extractVideoUrls(iMDbVideoExperienceJSElement, video);
            extractVideoInfo(iMDbVideoExperienceJSElement, video);
            extractRelatedVideos(iMDbVideoExperienceJSElement, video);

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(video, null, true);
    }

    @GetMapping("/name/{nameId}/videogallery")
    ApiResponse<VideoGallery> fetchNameVideos(
            @PathVariable("nameId") String nameId,
            @RequestParam(value = "sort", required = false, defaultValue = "date") String sort,
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
                videoGallery.setAvatar(generateCover(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("img").attr("src"),0,0));
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
                        video.setCover(generateCover(element.getElementsByTag("a").get(0).getElementsByTag("img").attr("loadLate"),400,300));
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
    ApiResponse<VideoGallery> fetchTitleVideos(
            @PathVariable("titleID") String titleID,
            @RequestParam(value = "sort", required = false, defaultValue = "date") String sort,
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
                videoGallery.setAvatar(generateCover(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("img").attr("src"),0,0));
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
                        video.setCover(generateCover(element.getElementsByTag("a").get(0).getElementsByTag("img").attr("loadLate"),400,300));
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

    private void extractVideoUrls(Element element, Video video) {
        List<String> urls = new ArrayList<>();
        try {
            String[] args = element.toString().split("args.push");
            int sIndex = args[0].indexOf("\"playbackData\"");
            int eIndex = args[0].indexOf(",\"videoInfoKey\"");
            if (sIndex > -1) {
                String[] els = args[0].substring(sIndex, eIndex).split("\"");
                for (String s : els) {
                    if (s.startsWith("https://imdb-video.media-imdb.com") && s.contains(".mp4")) {
                        urls.add(s.substring(0, s.length() - 1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            video.setHighQ(urls.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            video.setLowQ(urls.get(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractRelatedVideos(Element element, Video video) {
        List<Video.RelatedVideo> relatedVideos = new ArrayList<>();

        try {
            String[] args = element.toString().split("args.push");
            String trimArg = args[1].trim();
            String result = trimArg.substring(1, trimArg.length() - 2);


            try {
                JSONObject response = new JSONObject(result);
                JSONObject videoList = (JSONObject) response.get("VIDEO_LIST");
                Iterator<String> keys = videoList.keys();
                JSONObject videoListObject = videoList.getJSONObject(keys.next());
                JSONArray videoListArray = videoListObject.getJSONArray("videoList");

                for (Object o : videoListArray) {
                    JSONObject videoListItem = (JSONObject) o;
                    Video.RelatedVideo relatedVideo = new Video.RelatedVideo();
                    try {
                        relatedVideo.setVideoId(videoListItem.getJSONObject("videoId").getString("value"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        relatedVideo.setTitle(videoListItem.getString("videoTitle"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        relatedVideo.setDuration(videoListItem.getString("videoRuntime"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        relatedVideo.setCover(videoListItem.getJSONObject("videoSlate").getString("source"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        relatedVideo.setSubtitle(videoListItem.getString("relationText"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        relatedVideo.setTitleId(videoListItem.getString("relationId"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    relatedVideos.add(relatedVideo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        video.setRelatedVideos(relatedVideos);
    }

    private void extractVideoInfo(Element element, Video video) {

        try {
            String[] args = element.toString().split("args.push");
            String trimArg = args[1].trim();
            String result = trimArg.substring(1, trimArg.length() - 2);


            try {
                JSONObject response = new JSONObject(result);
                JSONObject videoInfo = (JSONObject) response.get("VIDEO_INFO");
                Iterator<String> keys = videoInfo.keys();
                JSONObject videoInfoObject = videoInfo.getJSONArray(keys.next()).getJSONObject(0);

                try {
                    video.setVideoCover(videoInfoObject.getJSONObject("videoSlate").getString("source"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    video.setVideoDescription(videoInfoObject.getString("videoDescription"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    video.setVideoRuntime(videoInfoObject.getString("videoRuntime"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    video.setVideoSubTitle(videoInfoObject.getString("videoSubTitle"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    video.setVideoTitle(videoInfoObject.getString("videoTitle"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject relatedTitleInfoModelObject = videoInfoObject.getJSONObject("relatedTitleInfoModel");
                    try {
                        List<String> genres = new ArrayList<>();
                        for (Object genre : relatedTitleInfoModelObject.getJSONArray("relationGenres")) {
                            genres.add(genre.toString());
                        }
                        video.setRelationGenres(genres);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        video.setTitleId(relatedTitleInfoModelObject.getJSONObject("relationTitleId").getString("value"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        video.setRelationPoster(relatedTitleInfoModelObject.getJSONObject("relationPoster").getString("source"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        video.setRelationIMDbRating(relatedTitleInfoModelObject.getDouble("relationIMDbRating"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        video.setRelationIsIMDbTVTitle(relatedTitleInfoModelObject.getBoolean("relationIsIMDbTVTitle"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        video.setRelationIsReleased(relatedTitleInfoModelObject.getBoolean("relationIsReleased"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        video.setRelationCertificateRatingsBody(relatedTitleInfoModelObject.getString("relationCertificateRatingsBody"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        video.setRelationCertificateRating(relatedTitleInfoModelObject.getString("relationCertificateRating"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        video.setRelationRuntime(relatedTitleInfoModelObject.getString("relationRuntime"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        video.setRelationReleaseYear(relatedTitleInfoModelObject.getString("relationReleaseYear"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        video.setRelationReleaseDate(relatedTitleInfoModelObject.getString("relationReleaseDate"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        video.setRelationTitle(relatedTitleInfoModelObject.getString("relationTitle"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Element extractIMDbVideoExperienceJS(Document doc) {
        Element result = null;
        try {
            Elements elements = doc.select("[type=text/javascript]");
            for (Element element : elements) {
                if (element.toString().contains("IMDbVideoExperienceJS")) {
                    result = element;
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String generateCover(String url, int width, int height) {

        if (url.isEmpty())
            return null;

        if (width == 0 || height == 0) {
            String[] coverUrlSplits = url.split("._V1_");
            return coverUrlSplits[0] + "._V1_.jpg";
        }

        String[] coverUrlSplits = url.split("._V1_");
        String baseUrl = coverUrlSplits[0] + "._V1_";
        String options = String.format("UY%s_CR%s,0,%s,%s_AL_.jpg", height, 0, 0, 0);
        return baseUrl + options;
    }


}
