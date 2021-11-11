package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.Home;
import ir.mab.imdbscrapping.model.HomeGraphQl;
import ir.mab.imdbscrapping.model.Trailer;
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
@RequestMapping(path = AppConstants.Api.TRAILER)
public class ImdbTrailerController {

    @GetMapping("/trending")
    ApiResponse<List<Trailer>> fetchTrendingTrailers() {
        List<Trailer> trailers = new ArrayList<>();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(AppConstants.IMDB_URL_GRAPH_QL);
        StringEntity params = null;
        try {

            JSONObject reqObject = new JSONObject();
            reqObject.put("query", "query Trl_TrendingTitles($limit: Int!, $paginationToken: String) {\n  trendingTitles(limit: $limit, paginationToken: $paginationToken) {\n    titles {\n      latestTrailer {\n        ...TrailerVideoMeta\n        __typename\n      }\n      ...TrailerTitleMeta\n      __typename\n    }\n    paginationToken\n    __typename\n  }\n}\n\nfragment TrailerTitleMeta on Title {\n  id\n  titleText {\n    text\n    __typename\n  }\n  primaryImage {\n    id\n    width\n    height\n    url\n    __typename\n  }\n  releaseDate {\n    day\n    month\n    year\n    __typename\n  }\n}\n\nfragment TrailerVideoMeta on Video {\n  id\n  name {\n    value\n    __typename\n  }\n  runtime {\n    value\n    __typename\n  }\n  description {\n    value\n    __typename\n  }\n  thumbnail {\n    url\n    width\n    height\n    __typename\n  }\n}\n");
            JSONObject varObject = new JSONObject();
            varObject.put("limit",100);
            reqObject.put("variables",varObject);

            params = new StringEntity(reqObject.toString());
            httppost.addHeader("content-type", "application/json");
            httppost.setEntity(params);
            HttpResponse response = httpClient.execute(httppost);
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            trailers = extractTrailers(responseJson.getJSONObject("data").getJSONObject("trendingTitles").getJSONArray("titles"));
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(trailers, null, true);
    }

    @GetMapping("/anticipated")
    ApiResponse<List<Trailer>> fetchMostAnticipatedTrailers() {
        List<Trailer> trailers = new ArrayList<>();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(AppConstants.IMDB_URL_GRAPH_QL);
        StringEntity params = null;
        try {
            LocalDate currentDate = LocalDate.now();
            String start = String.format("%d-%02d-%02d", currentDate.getYear() ,currentDate.getMonthValue(), currentDate.getDayOfMonth());
            String end = String.format("%d-%02d-%02d", currentDate.getYear() + 1 ,currentDate.getMonthValue(), currentDate.getDayOfMonth());

            JSONObject reqObject = new JSONObject();
            reqObject.put("query", "query Trl_PopularTitlesTrailers($limit: Int!, $paginationToken: String, $queryFilter: PopularTitlesQueryFilter!) {\n  popularTitles(limit: $limit, paginationToken: $paginationToken, queryFilter: $queryFilter) {\n    titles {\n      latestTrailer {\n        ...TrailerVideoMeta\n        __typename\n      }\n      ...TrailerTitleMeta\n      __typename\n    }\n    paginationToken\n    __typename\n  }\n}\n\nfragment TrailerTitleMeta on Title {\n  id\n  titleText {\n    text\n    __typename\n  }\n  primaryImage {\n    id\n    width\n    height\n    url\n    __typename\n  }\n  releaseDate {\n    day\n    month\n    year\n    __typename\n  }\n}\n\nfragment TrailerVideoMeta on Video {\n  id\n  name {\n    value\n    __typename\n  }\n  runtime {\n    value\n    __typename\n  }\n  description {\n    value\n    __typename\n  }\n  thumbnail {\n    url\n    width\n    height\n    __typename\n  }\n}\n");
            JSONObject varObject = new JSONObject();
            JSONObject releaseDateRangeObject = new JSONObject();
            JSONObject queryFilterObject = new JSONObject();
            releaseDateRangeObject.put("start",start);
            releaseDateRangeObject.put("end",end);
            queryFilterObject.put("releaseDateRange",releaseDateRangeObject);
            varObject.put("limit",100);
            varObject.put("queryFilter",queryFilterObject);

            reqObject.put("variables",varObject);

            params = new StringEntity(reqObject.toString());
            httppost.addHeader("content-type", "application/json");
            httppost.setEntity(params);
            HttpResponse response = httpClient.execute(httppost);
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            trailers = extractTrailers(responseJson.getJSONObject("data").getJSONObject("popularTitles").getJSONArray("titles"));
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(trailers, null, true);
    }

    @GetMapping("/popular")
    ApiResponse<List<Trailer>> fetchMostPopularTrailers() {
        List<Trailer> trailers = new ArrayList<>();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(AppConstants.IMDB_URL_GRAPH_QL);
        StringEntity params = null;
        try {
            LocalDate currentDate = LocalDate.now();
            String end = String.format("%d-%02d-%02d", currentDate.getYear() ,currentDate.getMonthValue(), currentDate.getDayOfMonth());

            JSONObject reqObject = new JSONObject();
            reqObject.put("query", "query Trl_PopularTitlesTrailers($limit: Int!, $paginationToken: String, $queryFilter: PopularTitlesQueryFilter!) {\n  popularTitles(limit: $limit, paginationToken: $paginationToken, queryFilter: $queryFilter) {\n    titles {\n      latestTrailer {\n        ...TrailerVideoMeta\n        __typename\n      }\n      ...TrailerTitleMeta\n      __typename\n    }\n    paginationToken\n    __typename\n  }\n}\n\nfragment TrailerTitleMeta on Title {\n  id\n  titleText {\n    text\n    __typename\n  }\n  primaryImage {\n    id\n    width\n    height\n    url\n    __typename\n  }\n  releaseDate {\n    day\n    month\n    year\n    __typename\n  }\n}\n\nfragment TrailerVideoMeta on Video {\n  id\n  name {\n    value\n    __typename\n  }\n  runtime {\n    value\n    __typename\n  }\n  description {\n    value\n    __typename\n  }\n  thumbnail {\n    url\n    width\n    height\n    __typename\n  }\n}\n");
            JSONObject varObject = new JSONObject();
            JSONObject releaseDateRangeObject = new JSONObject();
            JSONObject queryFilterObject = new JSONObject();
            releaseDateRangeObject.put("end",end);
            queryFilterObject.put("releaseDateRange",releaseDateRangeObject);
            varObject.put("limit",100);
            varObject.put("queryFilter",queryFilterObject);

            reqObject.put("variables",varObject);

            params = new StringEntity(reqObject.toString());
            httppost.addHeader("content-type", "application/json");
            httppost.setEntity(params);
            HttpResponse response = httpClient.execute(httppost);
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            trailers = extractTrailers(responseJson.getJSONObject("data").getJSONObject("popularTitles").getJSONArray("titles"));
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(trailers, null, true);
    }

    @GetMapping("/recent")
    ApiResponse<List<Trailer>> fetchMostRecentTrailers() {
        List<Trailer> trailers = new ArrayList<>();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(AppConstants.IMDB_URL_GRAPH_QL);
        StringEntity params = null;
        try {

            JSONObject reqObject = new JSONObject();
            reqObject.put("query", "query Trl_RecentVideos($limit: Int!, $paginationToken: String, $queryFilter: RecentVideosQueryFilter!) {\n  recentVideos(limit: $limit, paginationToken: $paginationToken, queryFilter: $queryFilter) {\n    videos {\n      ...TrailerVideoMeta\n      primaryTitle {\n        ...TrailerTitleMeta\n        __typename\n      }\n      __typename\n    }\n    paginationToken\n    __typename\n  }\n}\n\nfragment TrailerTitleMeta on Title {\n  id\n  titleText {\n    text\n    __typename\n  }\n  primaryImage {\n    id\n    width\n    height\n    url\n    __typename\n  }\n  releaseDate {\n    day\n    month\n    year\n    __typename\n  }\n}\n\nfragment TrailerVideoMeta on Video {\n  id\n  name {\n    value\n    __typename\n  }\n  runtime {\n    value\n    __typename\n  }\n  description {\n    value\n    __typename\n  }\n  thumbnail {\n    url\n    width\n    height\n    __typename\n  }\n}\n");
            JSONObject varObject = new JSONObject();
            JSONObject contentTypesObject = new JSONObject();
            contentTypesObject.put("contentTypes",new JSONArray().put("TRAILER"));
            varObject.put("limit",100);
            varObject.put("queryFilter",contentTypesObject);

            reqObject.put("variables",varObject);

            params = new StringEntity(reqObject.toString());
            httppost.addHeader("content-type", "application/json");
            httppost.setEntity(params);
            HttpResponse response = httpClient.execute(httppost);
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            trailers = extractRecentTrailers(responseJson.getJSONObject("data").getJSONObject("recentVideos").getJSONArray("videos"));
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(trailers, null, true);
    }

    private List<Trailer> extractTrailers(JSONArray responseJsonArray) {
        List<Trailer> trailers = new ArrayList<>();
        for (Object o : responseJsonArray){
            Trailer trailer = new Trailer();
            JSONObject item = (JSONObject) o;
            try{
                JSONObject latestTrailer = item.getJSONObject("latestTrailer");
                try {
                    trailer.setVideoId(latestTrailer.getString("id"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    trailer.setVideoName(latestTrailer.getJSONObject("name").getString("value"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    trailer.setVideoRuntime(latestTrailer.getJSONObject("runtime").getInt("value"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    trailer.setVideoDescription(latestTrailer.getJSONObject("description").getString("value"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    trailer.setVideoThumbnail(latestTrailer.getJSONObject("thumbnail").getString("url"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                trailer.setTitleId(item.getString("id"));
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                JSONObject titleText = item.getJSONObject("titleText");
                trailer.setTitle(titleText.getString("text"));
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                JSONObject primaryImage = item.getJSONObject("primaryImage");
                trailer.setTitleCover(primaryImage.getString("url"));
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                JSONObject releaseDate = item.getJSONObject("releaseDate");
                try{
                    trailer.setTitleReleaseYear(releaseDate.getInt("year"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    trailer.setTitleReleaseMonth(releaseDate.getInt("month"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    trailer.setTitleReleaseDay(releaseDate.getInt("day"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            trailers.add(trailer);
        }
        return trailers;
    }

    private List<Trailer> extractRecentTrailers(JSONArray responseJsonArray) {
        List<Trailer> trailers = new ArrayList<>();
        for (Object o : responseJsonArray){
            Trailer trailer = new Trailer();
            JSONObject item = (JSONObject) o;
            try {
                trailer.setVideoId(item.getString("id"));
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                trailer.setVideoName(item.getJSONObject("name").getString("value"));
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                trailer.setVideoRuntime(item.getJSONObject("runtime").getInt("value"));
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                trailer.setVideoDescription(item.getJSONObject("description").getString("value"));
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                trailer.setVideoThumbnail(item.getJSONObject("thumbnail").getString("url"));
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                JSONObject primaryTitle = item.getJSONObject("primaryTitle");
                try {
                    trailer.setTitleId(primaryTitle.getString("id"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    JSONObject titleText = primaryTitle.getJSONObject("titleText");
                    trailer.setTitle(titleText.getString("text"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    JSONObject primaryImage = primaryTitle.getJSONObject("primaryImage");
                    trailer.setTitleCover(primaryImage.getString("url"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    JSONObject releaseDate = primaryTitle.getJSONObject("releaseDate");
                    try{
                        trailer.setTitleReleaseYear(releaseDate.getInt("year"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try{
                        trailer.setTitleReleaseMonth(releaseDate.getInt("month"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try{
                        trailer.setTitleReleaseDay(releaseDate.getInt("day"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }


            trailers.add(trailer);
        }
        return trailers;
    }
}
