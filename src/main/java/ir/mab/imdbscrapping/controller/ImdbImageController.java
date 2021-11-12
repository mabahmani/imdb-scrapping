package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.ImageGallery;
import ir.mab.imdbscrapping.model.ImageList;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ir.mab.imdbscrapping.util.AppConstants.IMDB_LIST;

@RestController
@RequestMapping(path = AppConstants.Api.IMAGES)
public class ImdbImageController {
    private final Pattern imagePattern = Pattern.compile("rm+[0-9]+");

    @GetMapping("/list/{listId}")
    ApiResponse<ImageList> fetchListImages(@PathVariable("listId") String listId) {
        ImageList imageGallery = new ImageList();
        try {
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_LIST + "%s", listId)).get();

            try {
                imageGallery.setTitle(doc.getElementsByClass("header list-name").text());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setSubtitle(doc.getElementsByClass("list-description").text());
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                List<ImageList.Image> images = new ArrayList<>();
                for (Element element : doc.getElementsByClass("media_index_thumb_list").get(0).getElementsByTag("a")) {
                    try {
                        ImageList.Image image = new ImageList.Image();
                        image.setId(extractImageId(element.attr("href")));
                        image.setUrl(generateCover(element.getElementsByTag("img").get(0).attr("src"), 512, 512));
                        images.add(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                imageGallery.setImages(images);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(imageGallery, null, true);
    }

    @GetMapping("/name/{nameId}")
    ApiResponse<ImageList> fetchNameImages(@PathVariable("nameId") String nameId, @RequestParam(value = "page", required = false) Integer page) {
        ImageList imageGallery = new ImageList();
        try {
            Document doc = null;
            if (page != null) {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_NAME + "%s/mediaindex?page=%s", nameId, page)).get();
            } else {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_NAME +"%s/mediaindex", nameId)).get();
            }

            try {
                imageGallery.setTitle(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("h3").text());

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setSubtitle(generateCover(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("a").get(0).getElementsByTag("img").attr("src"),0,0));

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                List<ImageList.Image> images = new ArrayList<>();
                for (Element element : doc.getElementsByClass("media_index_thumb_list").get(0).getElementsByTag("a")) {
                    try {
                        ImageList.Image image = new ImageList.Image();
                        image.setId(extractImageId(element.attr("href")));
                        image.setUrl(generateCover(element.getElementsByTag("img").get(0).attr("src"), 512, 512));
                        images.add(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                imageGallery.setImages(images);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(imageGallery, null, true);
    }

    @GetMapping("/title/{titleId}")
    ApiResponse<ImageList> fetchTitleImages(@PathVariable("titleId") String titleId, @RequestParam(value = "page", required = false) Integer page) {
        ImageList imageGallery = new ImageList();
        try {
            Document doc = null;
            if (page != null) {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_TITLE + "%s/mediaindex?page=%s", titleId, page)).get();
            } else {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_TITLE + "%s/mediaindex", titleId)).get();
            }

            try {
                imageGallery.setTitle(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("h3").text());

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setSubtitle(generateCover(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("a").get(0).getElementsByTag("img").attr("src"),0,0));

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                List<ImageList.Image> images = new ArrayList<>();
                for (Element element : doc.getElementsByClass("media_index_thumb_list").get(0).getElementsByTag("a")) {
                    try {
                        ImageList.Image image = new ImageList.Image();
                        image.setId(extractImageId(element.attr("href")));
                        image.setUrl(generateCover(element.getElementsByTag("img").get(0).attr("src"), 512, 512));
                        images.add(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                imageGallery.setImages(images);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(imageGallery, null, true);
    }

    @GetMapping("/list/{listId}/extra")
    ApiResponse<ImageGallery> fetchListImagesExtra(
            @PathVariable("listId") String listId,
            @RequestParam(value = "imageId", required = false) String imageId,
            @RequestParam(value = "before", required = false) String beforeId,
            @RequestParam(value = "after", required = false) String afterId,
            @RequestParam(value = "last", required = false, defaultValue = "6") Integer last,
            @RequestParam(value = "first", required = false, defaultValue = "6") Integer first) {

        ImageGallery imageGallery = new ImageGallery();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(AppConstants.IMDB_URL_GRAPH_QL);
        StringEntity params = null;
        try {
            JSONObject reqObject = new JSONObject();
            reqObject.put("query", "query ListImages($id: ID!, $before: ID, $after: ID, $jumpTo: ID, $first: Int, $last: Int, $lastYes: Boolean!, $firstYes: Boolean!) {\n  list(id: $id) {\n    name {\n      originalText\n      __typename\n    }\n    items(first: $first, after: $after, jumpTo: $jumpTo) @include(if: $firstYes) {\n      total\n      ...MediaViewerListMeta\n      __typename\n    }\n    wrapFront: items(last: $last, before: $before) @include(if: $lastYes) {\n      total\n      ...MediaViewerListMeta\n      __typename\n    }\n    wrapBack: items(first: $first) @include(if: $firstYes) {\n      total\n      ...MediaViewerListMeta\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment MediaViewerListMeta on ListConnection {\n  pageInfo {\n    endCursor\n    hasNextPage\n    hasPreviousPage\n    startCursor\n    __typename\n  }\n  edges {\n    position\n    cursor\n    node {\n      item {\n        ...MediaViewerImageMeta\n        ...MediaSheetImageMeta\n        __typename\n      }\n      ...MediaSheetListItemMeta\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment MediaViewerImageMeta on Image {\n  id\n  url\n  height\n  width\n  caption {\n    plainText\n    __typename\n  }\n}\n\nfragment MediaSheetImageMeta on Image {\n  copyright\n  createdBy\n  caption {\n    plaidHtml\n    __typename\n  }\n  titles {\n    id\n    titleText {\n      text\n      __typename\n    }\n    __typename\n  }\n  source {\n    attributionUrl\n    text\n    banner {\n      url\n      attributionUrl\n      __typename\n    }\n    __typename\n  }\n  names {\n    id\n    nameText {\n      text\n      __typename\n    }\n    __typename\n  }\n  countries {\n    text\n    __typename\n  }\n  languages {\n    text\n    __typename\n  }\n  correctionLink(relatedId: $id, contributionContext: {isInIframe: true, returnUrl: \"https://www.imdb.com/close_me\", business: \"consumer\"}) {\n    url\n    __typename\n  }\n  reportingLink(relatedId: $id, contributionContext: {isInIframe: true, returnUrl: \"https://www.imdb.com/close_me\", business: \"consumer\"}) {\n    url\n    __typename\n  }\n}\n\nfragment MediaSheetListItemMeta on ListItemNode {\n  description {\n    originalText {\n      plaidHtml\n      __typename\n    }\n    __typename\n  }\n}\n");
            JSONObject varObject = new JSONObject();
            varObject.put("id", listId);
            varObject.put("lastYes", true);
            varObject.put("firstYes", true);
            if (imageId != null)
                varObject.put("jumpTo", imageId);
            if (beforeId != null)
                varObject.put("before", beforeId);
            else if (afterId != null)
                varObject.put("after", afterId);
            if (first != null)
                varObject.put("first", first);
            else if (last != null)
                varObject.put("last", last);

            reqObject.put("variables", varObject);


            params = new StringEntity(reqObject.toString());
            httppost.addHeader("content-type", "application/json");
            httppost.setEntity(params);
            HttpResponse response = httpClient.execute(httppost);
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));

            try {
                imageGallery.setTitle(responseJson.getJSONObject("data").getJSONObject("list").getJSONObject("name").getString("originalText"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                imageGallery.setEndCursor(responseJson.getJSONObject("data").getJSONObject("list").getJSONObject("items").getJSONObject("pageInfo").getString("endCursor"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setStartCursor(responseJson.getJSONObject("data").getJSONObject("list").getJSONObject("items").getJSONObject("pageInfo").getString("startCursor"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setHasNextPage(responseJson.getJSONObject("data").getJSONObject("list").getJSONObject("items").getJSONObject("pageInfo").getBoolean("hasNextPage"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setHasPreviousPage(responseJson.getJSONObject("data").getJSONObject("list").getJSONObject("items").getJSONObject("pageInfo").getBoolean("hasPreviousPage"));
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                List<ImageGallery.Image> images = new ArrayList<>();

                for (Object o : responseJson.getJSONObject("data").getJSONObject("list").getJSONObject("items").getJSONArray("edges")) {

                    ImageGallery.Image image = new ImageGallery.Image();

                    try {
                        JSONObject edge = (JSONObject) o;
                        try {
                            image.setPosition(edge.getInt("position"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject node = edge.getJSONObject("node");

                            try {
                                JSONObject item = node.getJSONObject("item");
                                try {
                                    image.setId(item.getString("id"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    image.setUrl(item.getString("url"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    image.setCaption(item.getJSONObject("caption").getString("plainText"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    image.setCopyRight(item.getString("copyright"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    image.setCreatedBy(item.getString("createdBy"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    List<ImageGallery.Image.LinkText> linkTexts = new ArrayList<>();
                                    for (Object t : item.getJSONArray("titles")) {
                                        ImageGallery.Image.LinkText linkText = new ImageGallery.Image.LinkText();
                                        linkText.setId(((JSONObject) t).getString("id"));
                                        linkText.setText(((JSONObject) t).getJSONObject("titleText").getString("text"));
                                        linkTexts.add(linkText);
                                    }
                                    image.setTitles(linkTexts);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    List<ImageGallery.Image.LinkText> linkTexts = new ArrayList<>();
                                    for (Object t : item.getJSONArray("names")) {
                                        ImageGallery.Image.LinkText linkText = new ImageGallery.Image.LinkText();
                                        linkText.setId(((JSONObject) t).getString("id"));
                                        linkText.setText(((JSONObject) t).getJSONObject("nameText").getString("text"));
                                        linkTexts.add(linkText);
                                    }
                                    image.setNames(linkTexts);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    List<String> stringList = new ArrayList<>();
                                    for (Object t : item.getJSONArray("countries")) {
                                        stringList.add(((JSONObject) t).getString("text"));
                                    }
                                    image.setCountries(stringList);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    List<String> stringList = new ArrayList<>();
                                    for (Object t : item.getJSONArray("languages")) {
                                        stringList.add(((JSONObject) t).getString("text"));
                                    }
                                    image.setLanguages(stringList);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                JSONObject description = node.getJSONObject("description");
                                image.setDescriptionHtml(description.getJSONObject("originalText").getString("plaidHtml"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    images.add(image);
                }

                imageGallery.setImages(images);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(imageGallery, null, true);
    }

    @GetMapping("/name/{nameId}/extra")
    ApiResponse<ImageGallery> fetchNameImagesExtra(
            @PathVariable("nameId") String nameId,
            @RequestParam(value = "imageId", required = false) String imageId,
            @RequestParam(value = "before", required = false) String beforeId,
            @RequestParam(value = "after", required = false) String afterId,
            @RequestParam(value = "last", required = false, defaultValue = "6") Integer last,
            @RequestParam(value = "first", required = false, defaultValue = "6") Integer first) {

        ImageGallery imageGallery = new ImageGallery();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(AppConstants.IMDB_URL_GRAPH_QL);
        StringEntity params = null;
        try {
            JSONObject reqObject = new JSONObject();
            reqObject.put("query", "query NameImages($id: ID!, $before: ID, $after: ID, $jumpTo: ID, $first: Int, $last: Int, $lastYes: Boolean!, $firstYes: Boolean!) {\n  name(id: $id) {\n    nameText {\n      text\n      __typename\n    }\n    meta {\n      publicationStatus\n      __typename\n    }\n    images(first: $first, after: $after, jumpTo: $jumpTo) @include(if: $firstYes) {\n      total\n      ...MediaViewerMeta\n      __typename\n    }\n    wrapFront: images(last: $last, before: $before) @include(if: $lastYes) {\n      total\n      ...MediaViewerMeta\n      __typename\n    }\n    wrapBack: images(first: $first) @include(if: $firstYes) {\n      total\n      ...MediaViewerMeta\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment MediaViewerMeta on ImageConnection {\n  pageInfo {\n    endCursor\n    hasNextPage\n    hasPreviousPage\n    startCursor\n    __typename\n  }\n  edges {\n    position\n    cursor\n    node {\n      ...MediaViewerImageMeta\n      ...MediaSheetImageMeta\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment MediaViewerImageMeta on Image {\n  id\n  url\n  height\n  width\n  caption {\n    plainText\n    __typename\n  }\n}\n\nfragment MediaSheetImageMeta on Image {\n  copyright\n  createdBy\n  caption {\n    plaidHtml\n    __typename\n  }\n  titles {\n    id\n    titleText {\n      text\n      __typename\n    }\n    __typename\n  }\n  source {\n    attributionUrl\n    text\n    banner {\n      url\n      attributionUrl\n      __typename\n    }\n    __typename\n  }\n  names {\n    id\n    nameText {\n      text\n      __typename\n    }\n    __typename\n  }\n  countries {\n    text\n    __typename\n  }\n  languages {\n    text\n    __typename\n  }\n  correctionLink(relatedId: $id, contributionContext: {isInIframe: true, returnUrl: \"https://www.imdb.com/close_me\", business: \"consumer\"}) {\n    url\n    __typename\n  }\n  reportingLink(relatedId: $id, contributionContext: {isInIframe: true, returnUrl: \"https://www.imdb.com/close_me\", business: \"consumer\"}) {\n    url\n    __typename\n  }\n}\n");

            JSONObject varObject = new JSONObject();
            varObject.put("id", nameId);
            varObject.put("lastYes", true);
            varObject.put("firstYes", true);
            if (imageId != null)
                varObject.put("jumpTo", imageId);
            if (beforeId != null)
                varObject.put("before", beforeId);
            else if (afterId != null)
                varObject.put("after", afterId);
            if (first != null)
                varObject.put("first", first);
            else if (last != null)
                varObject.put("last", last);

            reqObject.put("variables", varObject);

            params = new StringEntity(reqObject.toString());
            httppost.addHeader("content-type", "application/json");
            httppost.setEntity(params);
            HttpResponse response = httpClient.execute(httppost);
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            try {
                imageGallery.setTitle(responseJson.getJSONObject("data").getJSONObject("name").getJSONObject("nameText").getString("text"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setEndCursor(responseJson.getJSONObject("data").getJSONObject("name").getJSONObject("images").getJSONObject("pageInfo").getString("endCursor"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setStartCursor(responseJson.getJSONObject("data").getJSONObject("name").getJSONObject("images").getJSONObject("pageInfo").getString("startCursor"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setHasNextPage(responseJson.getJSONObject("data").getJSONObject("name").getJSONObject("images").getJSONObject("pageInfo").getBoolean("hasNextPage"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setHasPreviousPage(responseJson.getJSONObject("data").getJSONObject("name").getJSONObject("images").getJSONObject("pageInfo").getBoolean("hasPreviousPage"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                List<ImageGallery.Image> images = new ArrayList<>();

                for (Object o : responseJson.getJSONObject("data").getJSONObject("name").getJSONObject("images").getJSONArray("edges")) {

                    ImageGallery.Image image = new ImageGallery.Image();

                    try {
                        JSONObject edge = (JSONObject) o;
                        try {
                            image.setPosition(edge.getInt("position"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject item = edge.getJSONObject("node");
                            try {
                                image.setId(item.getString("id"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                image.setUrl(item.getString("url"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                image.setCaption(item.getJSONObject("caption").getString("plainText"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                image.setCopyRight(item.getString("copyright"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                image.setCreatedBy(item.getString("createdBy"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                List<ImageGallery.Image.LinkText> linkTexts = new ArrayList<>();
                                for (Object t : item.getJSONArray("titles")) {
                                    ImageGallery.Image.LinkText linkText = new ImageGallery.Image.LinkText();
                                    linkText.setId(((JSONObject) t).getString("id"));
                                    linkText.setText(((JSONObject) t).getJSONObject("titleText").getString("text"));
                                    linkTexts.add(linkText);
                                }
                                image.setTitles(linkTexts);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                List<ImageGallery.Image.LinkText> linkTexts = new ArrayList<>();
                                for (Object t : item.getJSONArray("names")) {
                                    ImageGallery.Image.LinkText linkText = new ImageGallery.Image.LinkText();
                                    linkText.setId(((JSONObject) t).getString("id"));
                                    linkText.setText(((JSONObject) t).getJSONObject("nameText").getString("text"));
                                    linkTexts.add(linkText);
                                }
                                image.setNames(linkTexts);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                List<String> stringList = new ArrayList<>();
                                for (Object t : item.getJSONArray("countries")) {
                                    stringList.add(((JSONObject) t).getString("text"));
                                }
                                image.setCountries(stringList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                List<String> stringList = new ArrayList<>();
                                for (Object t : item.getJSONArray("languages")) {
                                    stringList.add(((JSONObject) t).getString("text"));
                                }
                                image.setLanguages(stringList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                        try {
//                            JSONObject description = node.getJSONObject("description");
//                            image.setDescriptionHtml(description.getJSONObject("originalText").getString("plaidHtml"));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    images.add(image);
                }

                imageGallery.setImages(images);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(imageGallery, null, true);
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
