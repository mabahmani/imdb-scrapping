package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.ImageGallery;
import ir.mab.imdbscrapping.model.ImageList;
import ir.mab.imdbscrapping.model.MovieDetails;
import ir.mab.imdbscrapping.util.AppConstants;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = AppConstants.Api.IMAGES)
public class ImdbImageGalleryController {
    private final Pattern imagePattern = Pattern.compile("rm+[0-9]+");

    @GetMapping("/{listId}")
    ApiResponse<ImageList> fetchImages(@PathVariable("listId") String listId) {
        ImageList imageGallery = new ImageList();
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_URL + String.format("/list/%s", listId)).get();

            try {
                imageGallery.setTitle(doc.getElementsByClass("header list-name").text());
                imageGallery.setSubtitle(doc.getElementsByClass("list-description").text());
                try {
                    List<ImageList.Image> images = new ArrayList<>();
                    for (Element element: doc.getElementsByClass("media_index_thumb_list").get(0).getElementsByTag("a")){
                        try {
                            ImageList.Image image = new ImageList.Image();
                            image.setId(extractImageId(element.attr("href")));
                            image.setUrl(generateCover(element.getElementsByTag("img").get(0).attr("src"),512,512));
                            images.add(image);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    imageGallery.setImages(images);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(imageGallery, null, true);
    }

    @GetMapping("/{listId}/gallery/{size}")
    ApiResponse<ImageGallery> fetchImagesGraphQl(@PathVariable("listId") String listId, @PathVariable("size") Integer size) {
        ImageGallery imageGallery = new ImageGallery();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(AppConstants.IMDB_URL_GRAPH_QL);
        StringEntity params = null;
        try {
            String reqBody = String.format(
                    "{\n" +
                            "  \"query\":\"query ListImages($id: ID!, $before: ID, $after: ID, $jumpTo: ID, $first: Int, $last: Int, $lastYes: Boolean!, $firstYes: Boolean!) {\\n  list(id: $id) {\\n    name {\\n      originalText\\n      __typename\\n    }\\n    items(first: $first, after: $after, jumpTo: $jumpTo) @include(if: $firstYes) {\\n      total\\n      ...MediaViewerListMeta\\n      __typename\\n    }\\n    wrapFront: items(last: $last, before: $before) @include(if: $lastYes) {\\n      total\\n      ...MediaViewerListMeta\\n      __typename\\n    }\\n    wrapBack: items(first: $first) @include(if: $firstYes) {\\n      total\\n      ...MediaViewerListMeta\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment MediaViewerListMeta on ListConnection {\\n  pageInfo {\\n    endCursor\\n    hasNextPage\\n    hasPreviousPage\\n    startCursor\\n    __typename\\n  }\\n  edges {\\n    position\\n    cursor\\n    node {\\n      item {\\n        ...MediaViewerImageMeta\\n        ...MediaSheetImageMeta\\n        __typename\\n      }\\n      ...MediaSheetListItemMeta\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment MediaViewerImageMeta on Image {\\n  id\\n  url\\n  height\\n  width\\n  caption {\\n    plainText\\n    __typename\\n  }\\n}\\n\\nfragment MediaSheetImageMeta on Image {\\n  copyright\\n  createdBy\\n  caption {\\n    plaidHtml\\n    __typename\\n  }\\n  titles {\\n    id\\n    titleText {\\n      text\\n      __typename\\n    }\\n    __typename\\n  }\\n  source {\\n    attributionUrl\\n    text\\n    banner {\\n      url\\n      attributionUrl\\n      __typename\\n    }\\n    __typename\\n  }\\n  names {\\n    id\\n    nameText {\\n      text\\n      __typename\\n    }\\n    __typename\\n  }\\n  countries {\\n    text\\n    __typename\\n  }\\n  languages {\\n    text\\n    __typename\\n  }\\n  correctionLink(relatedId: $id, contributionContext: {isInIframe: true, returnUrl: \\\"https://www.imdb.com/close_me\\\", business: \\\"consumer\\\"}) {\\n    url\\n    __typename\\n  }\\n  reportingLink(relatedId: $id, contributionContext: {isInIframe: true, returnUrl: \\\"https://www.imdb.com/close_me\\\", business: \\\"consumer\\\"}) {\\n    url\\n    __typename\\n  }\\n}\\n\\nfragment MediaSheetListItemMeta on ListItemNode {\\n  description {\\n    originalText {\\n      plaidHtml\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"\n" +
                            "\t,\n" +
                            "  \"variables\":{\n" +
                            "  \"id\": \"%s\",\n" +
                            "  \"first\": %s,\n" +
                            "  \"last\": 0,\n" +
                            "  \"lastYes\": true,\n" +
                            "  \"firstYes\": true\n" +
                            "}\n" +
                            "}"
                    ,listId,size
            );

            params = new StringEntity(reqBody);
            httppost.addHeader("content-type", "application/json");
            httppost.setEntity(params);
            HttpResponse response = httpClient.execute(httppost);
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));

            try {
                imageGallery.setTitle(responseJson.getJSONObject("data").getJSONObject("list").getJSONObject("name").getString("originalText"));
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                List<ImageGallery.Image> images = new ArrayList<>();

                for (Object o: responseJson.getJSONObject("data").getJSONObject("list").getJSONObject("items").getJSONArray("edges")){

                    ImageGallery.Image image = new ImageGallery.Image();

                    try {
                        JSONObject edge = (JSONObject) o;
                        try {
                            image.setPosition(edge.getInt("position"));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try {
                            JSONObject node = edge.getJSONObject("node");

                            try {
                                JSONObject item = node.getJSONObject("item");
                                try {
                                    image.setId(item.getString("id"));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try {
                                    image.setUrl(item.getString("url"));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try {
                                    image.setCaption(item.getJSONObject("caption").getString("plainText"));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try {
                                    image.setCopyRight(item.getString("copyright"));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try {
                                    image.setCreatedBy(item.getString("createdBy"));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try {
                                    List<ImageGallery.Image.LinkText> linkTexts = new ArrayList<>();
                                    for (Object t : item.getJSONArray("titles")){
                                        ImageGallery.Image.LinkText linkText = new ImageGallery.Image.LinkText();
                                        linkText.setId(((JSONObject) t).getString("id"));
                                        linkText.setText(((JSONObject) t).getJSONObject("titleText").getString("text"));
                                        linkTexts.add(linkText);
                                    }
                                    image.setTitles(linkTexts);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try {
                                    List<ImageGallery.Image.LinkText> linkTexts = new ArrayList<>();
                                    for (Object t : item.getJSONArray("names")){
                                        ImageGallery.Image.LinkText linkText = new ImageGallery.Image.LinkText();
                                        linkText.setId(((JSONObject) t).getString("id"));
                                        linkText.setText(((JSONObject) t).getJSONObject("nameText").getString("text"));
                                        linkTexts.add(linkText);
                                    }
                                    image.setNames(linkTexts);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try {
                                    List<String> stringList = new ArrayList<>();
                                    for (Object t : item.getJSONArray("countries")){
                                        stringList.add(((JSONObject) t).getString("text"));
                                    }
                                    image.setCountries(stringList);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try {
                                    List<String> stringList = new ArrayList<>();
                                    for (Object t : item.getJSONArray("languages")){
                                        stringList.add(((JSONObject) t).getString("text"));
                                    }
                                    image.setLanguages(stringList);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            try {
                                JSONObject description = node.getJSONObject("description");
                                image.setDescriptionHtml(description.getJSONObject("originalText").getString("plaidHtml"));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    images.add(image);
                }

                imageGallery.setImages(images);
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            return new ApiResponse<>(null,e.getMessage(),false);
        }

        return new ApiResponse<>(imageGallery,null,true);
    }

    private String extractImageId(String text) {
        Matcher m = imagePattern.matcher(text);
        if (m.find())
            return m.group();

        return null;
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
