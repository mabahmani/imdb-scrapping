package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.Home;
import ir.mab.imdbscrapping.model.HomeGraphQl;
import ir.mab.imdbscrapping.util.AppConstants;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping(path = AppConstants.Api.HOME)
public class ImdbHomeController {

    @GetMapping("/")
    ApiResponse<Home> fetchHome() {
        Home home = new Home();
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_URL).get();
            try {
                JSONObject response = getJsonResponse(doc);
                home.setTrailers(getTrailers(response));
                home.setFeaturedToday(extractFeaturedItems(response, "featured-today"));
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

    @GetMapping("/extra")
    ApiResponse<HomeGraphQl> fetchHomeExtra() {
        HomeGraphQl home = new HomeGraphQl();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(AppConstants.IMDB_URL_GRAPH_QL);
        StringEntity params;
        try {
            LocalDate currentDate = LocalDate.now();
            String today = String.format("--%02d-%02d", currentDate.getMonthValue(), currentDate.getDayOfMonth());
            String movieReleasingOnOrAfter = String.format("%d-%02d-%02d", currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth());

            JSONObject reqObject = new JSONObject();
            reqObject.put("query", "fragment TitleWatchOption on Title {\n  primaryWatchOption {\n    additionalWatchOptionsCount\n    __typename\n  }\n}\n\nfragment TitleCardTrailer on Title {\n  latestTrailer {\n    id\n    __typename\n  }\n}\n\nfragment BaseTitleCard on Title {\n  id\n  titleText {\n    text\n    __typename\n  }\n  titleType {\n    id\n    __typename\n  }\n  originalTitleText {\n    text\n    __typename\n  }\n  primaryImage {\n    id\n    width\n    height\n    url\n    __typename\n  }\n  releaseYear {\n    year\n    endYear\n    __typename\n  }\n  ratingsSummary {\n    aggregateRating\n    voteCount\n    __typename\n  }\n  runtime {\n    seconds\n    __typename\n  }\n  certificate {\n    rating\n    __typename\n  }\n  canRate {\n    isRatable\n    __typename\n  }\n  canHaveEpisodes\n}\n\nquery BatchPage_HomeMain($topPicksFirst: Int!, $topPicksAfter: String, $fanPicksFirst: Int!, $fanPicksAfter: ID, $inTheatersLocation: ShowtimesLocation!, $movieReleasingOnOrAfter: Date!, $movieViewerLocation: ShowtimesLocation!, $bornToday: MonthDay!, $bornTodayFirst: Int!) {\n  titleRecommendations(first: $topPicksFirst, after: $topPicksAfter) {\n    edges {\n      node {\n        refTag\n        title {\n          ...BaseTitleCard\n          ...TitleCardTrailer\n          ...TitleWatchOption\n          __typename\n        }\n        explanations {\n          title {\n            id\n            titleText {\n              text\n              __typename\n            }\n            originalTitleText {\n              text\n              __typename\n            }\n            __typename\n          }\n          __typename\n        }\n        __typename\n      }\n      __typename\n    }\n    __typename\n  }\n  fanPicksTitles(first: $fanPicksFirst, after: $fanPicksAfter) {\n    edges {\n      node {\n        ...BaseTitleCard\n        ...TitleCardTrailer\n        ...TitleWatchOption\n        __typename\n      }\n      __typename\n    }\n    refTag {\n      ep13nReftag\n      __typename\n    }\n    __typename\n  }\n  streamingTitles {\n    provider {\n      id\n      name {\n        value\n        __typename\n      }\n      description {\n        value\n        __typename\n      }\n      refTagFragment\n      __typename\n    }\n    titles(first: 25) {\n      edges {\n        node {\n          title {\n            ...BaseTitleCard\n            ...TitleCardTrailer\n            __typename\n          }\n          __typename\n        }\n        __typename\n      }\n      __typename\n    }\n    __typename\n  }\n  showtimesTitles(first: 30, location: $inTheatersLocation, queryMetadata: {sortField: SHOWTIMES_COUNT, sortOrder: DESC}) {\n    edges {\n      node {\n        ...BaseTitleCard\n        ...TitleCardTrailer\n        __typename\n      }\n      __typename\n    }\n    __typename\n  }\n  comingSoonMovie: comingSoon(first: 50, comingSoonType: MOVIE, releasingOnOrAfter: $movieReleasingOnOrAfter) {\n    edges {\n      node {\n        ...BaseTitleCard\n        ...TitleCardTrailer\n        releaseDate {\n          day\n          month\n          year\n          __typename\n        }\n        latestTrailer {\n          name {\n            value\n            __typename\n          }\n          runtime {\n            value\n            __typename\n          }\n          thumbnail {\n            height\n            width\n            url\n            __typename\n          }\n          __typename\n        }\n        cinemas(first: 0, request: {location: $movieViewerLocation}) {\n          total\n          __typename\n        }\n        meterRanking {\n          currentRank\n          __typename\n        }\n        __typename\n      }\n      __typename\n    }\n    __typename\n  }\n  bornToday(today: $bornToday, first: $bornTodayFirst) {\n    edges {\n      node {\n        id\n        nameText {\n          text\n          __typename\n        }\n        birth {\n          date\n          __typename\n        }\n        death {\n          date\n          __typename\n        }\n        primaryImage {\n          caption {\n            plainText\n            __typename\n          }\n          url\n          height\n          width\n          __typename\n        }\n        __typename\n      }\n      __typename\n    }\n    __typename\n  }\n}\n");
            JSONObject varObject = new JSONObject();
            JSONObject locationObject = new JSONObject();
            JSONObject latLongObject = new JSONObject();

            latLongObject.put("lat","37.77");
            latLongObject.put("long","-122.41");
            locationObject.put("latLong",latLongObject);
            locationObject.put("radiusInMeters",80467);

            varObject.put("bornToday",today);
            varObject.put("bornTodayFirst",30);
            varObject.put("fanPicksFirst",30);
            varObject.put("inTheatersLocation",locationObject);
            varObject.put("movieReleasingOnOrAfter",movieReleasingOnOrAfter);
            varObject.put("movieViewerLocation",locationObject);
            varObject.put("topPicksFirst",30);

            reqObject.put("variables",varObject);

            params = new StringEntity(reqObject.toString());
            httppost.addHeader("content-type", "application/json");
            httppost.addHeader("x-amzn-sessionid", "0");
            httppost.setEntity(params);
            HttpResponse response = httpClient.execute(httppost);
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            home.setFanPicksTitles(getFanPicksTitles(responseJson));
            home.setStreamingTitles(getStreamingTitles(responseJson));
            home.setComingSoonMovies(getComingSoonMovies(responseJson));
            home.setShowTimesTitles(getShowTimesTitles(responseJson));
            home.setBornTodayList(getBornTodayList(responseJson));
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(home, null, true);
    }

    private List<HomeGraphQl.BornToday> getBornTodayList(JSONObject responseJson) {
        try {
            JSONArray edges = (JSONArray) ((JSONObject) ((JSONObject) responseJson.get("data")).get("bornToday")).get("edges");
            return extractBornTodayNodes(edges);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<HomeGraphQl.MovieCard> getShowTimesTitles(JSONObject responseJson) {
        try {
            JSONArray edges = (JSONArray) ((JSONObject) ((JSONObject) responseJson.get("data")).get("showtimesTitles")).get("edges");
            return extractMovieCardNodes(edges);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<HomeGraphQl.MovieCard> getComingSoonMovies(JSONObject responseJson) {
        try {
            JSONArray edges = (JSONArray) ((JSONObject) ((JSONObject) responseJson.get("data")).get("comingSoonMovie")).get("edges");
            return extractMovieCardNodes(edges);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<HomeGraphQl.StreamProvider> getStreamingTitles(JSONObject responseJson) {
        List<HomeGraphQl.StreamProvider> list = new ArrayList<>();
        try {
            JSONArray streamingTitles = (JSONArray) ((JSONObject) responseJson.get("data")).get("streamingTitles");
            for (Object o : streamingTitles) {
                try {
                    JSONObject streamingTitle = (JSONObject) o;
                    HomeGraphQl.StreamProvider streamProvider = new HomeGraphQl.StreamProvider();
                    try {
                        streamProvider.setName(((JSONObject) ((JSONObject) streamingTitle.get("provider")).get("name")).get("value").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        streamProvider.setTitles(extractMovieCardNodes((JSONArray) ((JSONObject) streamingTitle.get("titles")).get("edges")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    list.add(streamProvider);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<HomeGraphQl.MovieCard> getFanPicksTitles(JSONObject responseJson) {
        try {
            JSONArray edges = (JSONArray) ((JSONObject) ((JSONObject) responseJson.get("data")).get("fanPicksTitles")).get("edges");
            return extractMovieCardNodes(edges);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<HomeGraphQl.MovieCard> extractMovieCardNodes(JSONArray edges) {
        List<HomeGraphQl.MovieCard> list = new ArrayList<>();

        for (Object o : edges) {
            try {
                HomeGraphQl.MovieCard movieCard = new HomeGraphQl.MovieCard();

                JSONObject node = (JSONObject) ((JSONObject) o).get("node");

                if (!node.isNull("title"))
                    node = (JSONObject) node.get("title");

                try {
                    movieCard.setTitleId(node.get("id").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    movieCard.setTitle(((JSONObject) node.get("titleText")).get("text").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    movieCard.setCover(((JSONObject) node.get("primaryImage")).get("url").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    movieCard.setReleaseYear(((JSONObject) node.get("releaseYear")).get("year").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    movieCard.setRate(Float.valueOf(((JSONObject) node.get("ratingsSummary")).get("aggregateRating").toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    movieCard.setVoteCount(Integer.valueOf(((JSONObject) node.get("ratingsSummary")).get("voteCount").toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    movieCard.setRuntime(Integer.valueOf(((JSONObject) node.get("runtime")).get("seconds").toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    movieCard.setCertificate(((JSONObject) node.get("certificate")).get("rating").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    movieCard.setVideoId(((JSONObject) node.get("latestTrailer")).get("id").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (!node.isNull("releaseDate")) {
                        movieCard.setReleaseDay(((JSONObject) node.get("releaseDate")).get("day").toString());
                        movieCard.setReleaseMonth(((JSONObject) node.get("releaseDate")).get("month").toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                list.add(movieCard);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    private List<HomeGraphQl.BornToday> extractBornTodayNodes(JSONArray edges) {
        List<HomeGraphQl.BornToday> list = new ArrayList<>();

        for (Object o : edges) {
            try {
                HomeGraphQl.BornToday bornToday = new HomeGraphQl.BornToday();

                JSONObject node = (JSONObject) ((JSONObject) o).get("node");

                try {
                    String death = ((JSONObject) node.get("death")).get("date").toString();
                    bornToday.setDeath(death.equals("null") ? null : death);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    bornToday.setBirth(((JSONObject) node.get("birth")).get("date").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    bornToday.setImage(((JSONObject) node.get("primaryImage")).get("url").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    bornToday.setNameId(node.get("id").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    bornToday.setTitle(((JSONObject) node.get("nameText")).get("text").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                list.add(bornToday);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
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
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                JSONArray boxOfficeWeekendChartEntries = (JSONArray) boxOfficeWeekendChart.get("entries");

                List<Home.BoxOffice.Data> dataList = new ArrayList<>();

                for (Object object : boxOfficeWeekendChartEntries) {
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                data.setCurrency(total.get("currency").toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                data.setCinemas(Integer.valueOf(cinemas.get("total").toString()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                data.setTitle(titleText.get("text").toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                data.setTitleId(title.get("id").toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dataList.add(data);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                boxOffice.setData(dataList);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
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

                for (Object object : newsObjectEdges) {
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                newsModel.setId(node.get("id").toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                newsModel.setImage(image.get("url").toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                newsModel.setTitle(articleTitle.get("plainText").toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                newsModel.setSource(((JSONObject) source.get("homepage")).get("label").toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            news.add(newsModel);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
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

                            while (queryTypeFlagsKeys.hasNext()) {
                                String type = queryTypeFlagsKeys.next();
                                if (type.equals("image"))
                                    featured.setImage(true);
                                else if (type.equals("video")) {
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

}
