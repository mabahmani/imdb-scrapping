package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.Home;
import ir.mab.imdbscrapping.util.AppConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = AppConstants.Api.BASE_URL)
public class ImdbController {
    private final Pattern namePattern = Pattern.compile("nm+[0-9]+");
    private final Pattern videoPattern = Pattern.compile("vi+[0-9]+");
    private final Pattern titlePattern = Pattern.compile("tt+[0-9]+");

    @GetMapping("/home")
    ApiResponse<Home> fetchHome() {
        Home home = new Home();
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_URL).get();
            try {
                JSONObject response = getJsonResponse(doc);
                home.setTrailers(getTrailers(response));
                home.setFeaturedToday(extractFeaturedItems(response,"featured-today"));
                home.setImdbOriginals(extractFeaturedItems(response, "imdb-originals"));
                home.setEditorPicks(extractFeaturedItems(response, "editors-picks"));
                home.setBoxOffice(getBoxOffice(response));
                home.setNews(getNews(response));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);

        }

        return new ApiResponse<>(home, null, true);
    }

    private Home.BoxOffice getBoxOffice(JSONObject response) {
        Home.BoxOffice boxOffice = new Home.BoxOffice();

        try {
            JSONObject props = (JSONObject) response.get("props");
            JSONObject urqlState = (JSONObject) props.get("urqlState");
            JSONObject urqlStateNextKey = (JSONObject) urqlState.get(urqlState.keys().next());
            JSONObject urqlStateNextKeyData = (JSONObject) urqlStateNextKey.get("data");
            JSONObject boxOfficeWeekendChart = (JSONObject) urqlStateNextKeyData.get("boxOfficeWeekendChart");

            try {
                boxOffice.setWeekendStartDate(boxOfficeWeekendChart.get("weekendStartDate").toString());
                boxOffice.setWeekendEndDate(boxOfficeWeekendChart.get("weekendEndDate").toString());
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                JSONArray boxOfficeWeekendChartEntries = (JSONArray) boxOfficeWeekendChart.get("entries");

                List<Home.BoxOffice.Data> dataList = new ArrayList<>();

                for (Object object : boxOfficeWeekendChartEntries){
                    try {
                        JSONObject jsonObject = (JSONObject) object;
                        JSONObject weekendGross = (JSONObject) jsonObject.get("weekendGross");
                        JSONObject title = (JSONObject) jsonObject.get("title");
                        try {
                            JSONObject total = (JSONObject) weekendGross.get("total");
                            JSONObject cinemas = (JSONObject) title.get("cinemas");
                            JSONObject titleText = (JSONObject) title.get("titleText");
                            Home.BoxOffice.Data data = new Home.BoxOffice.Data();
                            try {
                                data.setWeekendGross(Integer.valueOf(total.get("amount").toString()));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            try {
                                data.setCurrency(total.get("currency").toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            try {
                                data.setCinemas(Integer.valueOf(cinemas.get("total").toString()));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            try {
                                data.setTitle(titleText.get("text").toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            try {
                                data.setTitleId(title.get("id").toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            dataList.add(data);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                boxOffice.setData(dataList);

            }catch (Exception e){
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }


        return boxOffice;
    }

    private List<Home.News> getNews(JSONObject response) {
        List<Home.News> news = new ArrayList<>();

        try {
            JSONObject props = (JSONObject) response.get("props");
            JSONObject urqlState = (JSONObject) props.get("urqlState");
            JSONObject urqlStateNextKey = (JSONObject) urqlState.get(urqlState.keys().next());
            JSONObject urqlStateNextKeyData = (JSONObject) urqlStateNextKey.get("data");
            JSONObject newsObject = (JSONObject) urqlStateNextKeyData.get("news");

            try {
                JSONArray newsObjectEdges = (JSONArray) newsObject.get("edges");

                for (Object object : newsObjectEdges){
                    try {
                        JSONObject jsonObject = (JSONObject) object;
                        JSONObject node = (JSONObject) jsonObject.get("node");
                        try {
                            JSONObject image = (JSONObject) node.get("image");
                            JSONObject articleTitle = (JSONObject) node.get("articleTitle");
                            JSONObject source = (JSONObject) node.get("source");

                            Home.News newsModel = new Home.News();

                            try {
                                newsModel.setDate(node.get("date").toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            try {
                                newsModel.setId(node.get("id").toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            try {
                                newsModel.setImage(image.get("url").toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            try {
                                newsModel.setTitle(articleTitle.get("plainText").toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            try {
                                newsModel.setSource(((JSONObject)source.get("homepage")).get("label").toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            news.add(newsModel);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }


        return news;
    }

    private List<Home.Featured> extractFeaturedItems(JSONObject response, String titleToSearch) {
        List<Home.Featured> featuredList = new ArrayList<>();
        try {
            JSONObject transformedPlacements = getTransformedPlacements(response);
            Iterator<String> keys = transformedPlacements.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    if (key.contains(titleToSearch)) {
                        Home.Featured featured = new Home.Featured();

                        JSONObject featuredToday = (JSONObject) transformedPlacements.get(key);
                        JSONObject transformedArguments = (JSONObject) featuredToday.get("transformedArguments");
                        JSONObject queryTypeFlags = (JSONObject) featuredToday.get("queryTypeFlags");
                        JSONArray linkedImages = (JSONArray) transformedArguments.get("linkedImages");
                        JSONObject firstImage = (JSONObject) linkedImages.get(0);

                        try {
                            featured.setCover(((JSONObject) firstImage.get("imageModel")).get("url").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Iterator<String> queryTypeFlagsKeys = queryTypeFlags.keys();

                            while (queryTypeFlagsKeys.hasNext()){
                                String type = queryTypeFlagsKeys.next();
                                if (type.equals("image"))
                                    featured.setImage(true);
                                else if (type.equals("video")){
                                    featured.setVideo(true);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            featured.setLink(AppConstants.IMDB_URL + firstImage.get("link").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            featured.setTitle(transformedArguments.get("displayTitle").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            featured.setId(transformedArguments.get("constId").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            featured.setRmId(transformedArguments.get("rmConstForSlateImage").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            featured.setCaption(transformedArguments.get("overlayCaption").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        featuredList.add((featured));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return featuredList;
    }

    @GetMapping("/home1")
    String fetchHome1() {
        JSONObject response = null;
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_URL).get();
            try {
                response = getJsonResponse(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            return null;
        }

        return response.toString();
    }

    private List<Home.Trailer> getTrailers(JSONObject response) {
        List<Home.Trailer> trailers = new ArrayList<>();

        try {
            JSONObject transformedPlacements = getTransformedPlacements(response);

            Iterator<String> keys = transformedPlacements.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    if (key.contains("hero-video")) {
                        Home.Trailer trailer = new Home.Trailer();
                        JSONObject heroVideo = (JSONObject) transformedPlacements.get(key);
                        JSONObject transformedArguments = (JSONObject) heroVideo.get("transformedArguments");
                        JSONObject posterImage = (JSONObject) transformedArguments.get("posterImage");
                        JSONObject videoSlateImage = (JSONObject) transformedArguments.get("videoSlateImage");

                        try {
                            trailer.setTitleId(transformedArguments.get("titleId").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            trailer.setVideoId(transformedArguments.get("videoId").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            trailer.setDuration(transformedArguments.get("runtime").toString());
                        } catch (Exception e) {
                            trailer.setVideoId(transformedArguments.get("videoId").toString());
                        }
                        try {
                            trailer.setCover(posterImage.get("url").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            trailer.setPreview(videoSlateImage.get("url").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            trailer.setTitle(transformedArguments.get("headline").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            trailer.setSubTitle(transformedArguments.get("subHeadline").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        trailers.add(trailer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return trailers;
    }

    private JSONObject getJsonResponse(Document doc) {
        String json = doc.getElementsByAttributeValue("id", "__NEXT_DATA__").get(0).data();
        return new JSONObject(json);
    }

    private JSONObject getTransformedPlacements(JSONObject response) {
        JSONObject props = (JSONObject) response.get("props");
        JSONObject cmsContext = (JSONObject) props.get("cmsContext");
        return (JSONObject) cmsContext.get("transformedPlacements");
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

    private String extractNameId(String text) {
        Matcher m = namePattern.matcher(text);
        if (m.find())
            return m.group();

        return null;
    }

    private String extractTitleId(String text) {
        Matcher m = titlePattern.matcher(text);
        if (m.find())
            return m.group();

        return null;
    }

    private String extractVideoId(String text) {
        Matcher m = videoPattern.matcher(text);
        if (m.find())
            return m.group();

        return null;
    }
}
