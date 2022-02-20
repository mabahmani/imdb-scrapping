package ir.mab.imdbscrapping.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

import static ir.mab.imdbscrapping.util.Utils.extractImageId;
import static ir.mab.imdbscrapping.util.Utils.generateImage;

@RestController
@RequestMapping(path = AppConstants.Api.IMAGES)
public class ImdbImageController {

    @GetMapping("/list/{listId}")
    @ApiOperation("All images of a list")
    ApiResponse<ImageList> fetchImagesOfList(
            @ApiParam("Ex. ls505300263")
            @PathVariable("listId") String listId) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                imageGallery.setImages(extractImages(doc));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(imageGallery, null, true);
    }

    @GetMapping("/gallery/{galleryId}")
    @ApiOperation("All images of a gallery")
    ApiResponse<ImageList> fetchImagesOfGallery(
            @ApiParam("Ex. rg1858378496")
            @PathVariable("galleryId") String galleryId,
            @RequestParam(value = "page", required = false) Integer page
    ) {
        ImageList imageGallery = new ImageList();
        try {
            Document doc;

            if (page != null) {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_GALLERY + "%s?page=%s", galleryId, page)).get();
            } else {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_GALLERY + "%s", galleryId)).get();
            }

            try {
                imageGallery.setTitle(doc.getElementsByClass("header list-name").text());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setSubtitle(doc.getElementsByClass("list-description").text());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                imageGallery.setImages(extractImages(doc));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(imageGallery, null, true);
    }

    @GetMapping("/names/{nameId}")
    @ApiOperation("All images of a name (celebrities, directors, ...)")
    ApiResponse<ImageList> fetchListImagesOfName(
            @ApiParam("Ex. nm0000190")
            @PathVariable("nameId") String nameId,
            @RequestParam(value = "page", required = false) Integer page) {
        ImageList imageGallery = new ImageList();
        try {
            Document doc;
            if (page != null) {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_NAME + "%s/mediaindex?page=%s", nameId, page)).get();
            } else {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_NAME + "%s/mediaindex", nameId)).get();
            }

            extractHeaderTitle(imageGallery, doc);

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(imageGallery, null, true);
    }

    @GetMapping("/titles/{titleId}")
    @ApiOperation("All images of a titles (Movies, Tv Shows, ...)")
    ApiResponse<ImageList> fetchListImagesOfTitle(
            @ApiParam("Ex. tt1160419")
            @PathVariable("titleId") String titleId,
            @RequestParam(value = "page", required = false) Integer page) {
        ImageList imageGallery = new ImageList();
        try {
            Document doc;
            if (page != null) {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_TITLE + "%s/mediaindex?page=%s", titleId, page)).get();
            } else {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_TITLE + "%s/mediaindex", titleId)).get();
            }

            extractHeaderTitle(imageGallery, doc);

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(imageGallery, null, true);
    }

    @GetMapping("/list/{listId}/slider")
    @ApiOperation("Images of a list as slider with details")
    ApiResponse<ImageGallery> fetchImagesOfListAsSliderWithDetails(
            @ApiParam("Ex. ls505300263")
            @PathVariable("listId") String listId,
            @ApiParam("Ex. rm4288413441")
            @RequestParam(value = "imageId", required = false) String imageId,
            @ApiParam("images before this cursor id")
            @RequestParam(value = "before", required = false) String beforeId,
            @ApiParam("images after this cursor id")
            @RequestParam(value = "after", required = false) String afterId,
            @ApiParam("number of last images")
            @RequestParam(value = "last", required = false, defaultValue = "6") Integer last,
            @ApiParam("number of first images")
            @RequestParam(value = "first", required = false, defaultValue = "6") Integer first) {

        ImageGallery imageGallery = new ImageGallery();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(AppConstants.IMDB_URL_GRAPH_QL);
        StringEntity params;
        try {
            JSONObject reqObject = new JSONObject();
            reqObject.put("query", "query ListImages($id: ID!, $before: ID, $after: ID, $jumpTo: ID, $first: Int, $last: Int, $lastYes: Boolean!, $firstYes: Boolean!) {\n  list(id: $id) {\n    name {\n      originalText\n      __typename\n    }\n    items(first: $first, after: $after, jumpTo: $jumpTo) @include(if: $firstYes) {\n      total\n      ...MediaViewerListMeta\n      __typename\n    }\n    wrapFront: items(last: $last, before: $before) @include(if: $lastYes) {\n      total\n      ...MediaViewerListMeta\n      __typename\n    }\n    wrapBack: items(first: $first) @include(if: $firstYes) {\n      total\n      ...MediaViewerListMeta\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment MediaViewerListMeta on ListConnection {\n  pageInfo {\n    endCursor\n    hasNextPage\n    hasPreviousPage\n    startCursor\n    __typename\n  }\n  edges {\n    position\n    cursor\n    node {\n      item {\n        ...MediaViewerImageMeta\n        ...MediaSheetImageMeta\n        __typename\n      }\n      ...MediaSheetListItemMeta\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment MediaViewerImageMeta on Image {\n  id\n  url\n  height\n  width\n  caption {\n    plainText\n    __typename\n  }\n}\n\nfragment MediaSheetImageMeta on Image {\n  copyright\n  createdBy\n  caption {\n    plaidHtml\n    __typename\n  }\n  titles {\n    id\n    titleText {\n      text\n      __typename\n    }\n    __typename\n  }\n  source {\n    attributionUrl\n    text\n    banner {\n      url\n      attributionUrl\n      __typename\n    }\n    __typename\n  }\n  names {\n    id\n    nameText {\n      text\n      __typename\n    }\n    __typename\n  }\n  countries {\n    text\n    __typename\n  }\n  languages {\n    text\n    __typename\n  }\n  correctionLink(relatedId: $id, contributionContext: {isInIframe: true, returnUrl: \"https://www.imdb.com/close_me\", business: \"consumer\"}) {\n    url\n    __typename\n  }\n  reportingLink(relatedId: $id, contributionContext: {isInIframe: true, returnUrl: \"https://www.imdb.com/close_me\", business: \"consumer\"}) {\n    url\n    __typename\n  }\n}\n\nfragment MediaSheetListItemMeta on ListItemNode {\n  description {\n    originalText {\n      plaidHtml\n      __typename\n    }\n    __typename\n  }\n}\n");
            reqObject.put("variables", initVariableObject(listId, imageId, beforeId, afterId, first, last));
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
                                setImageFields(image, item);
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

    @GetMapping("/names/{nameId}/slider")
    @ApiOperation("Images of a name as slider with details")
    ApiResponse<ImageGallery> fetchImagesOfNameAsSliderWithDetails(
            @ApiParam("Ex. nm0000190")
            @PathVariable("nameId") String nameId,
            @ApiParam("Ex. rm1484838913")
            @RequestParam(value = "imageId", required = false) String imageId,
            @ApiParam("images before this cursor id")
            @RequestParam(value = "before", required = false) String beforeId,
            @ApiParam("images after this cursor id")
            @RequestParam(value = "after", required = false) String afterId,
            @ApiParam("number of last images")
            @RequestParam(value = "last", required = false, defaultValue = "6") Integer last,
            @ApiParam("number of first images")
            @RequestParam(value = "first", required = false, defaultValue = "6") Integer first) {

        ImageGallery imageGallery = new ImageGallery();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(AppConstants.IMDB_URL_GRAPH_QL);
        StringEntity params;
        try {
            JSONObject reqObject = new JSONObject();
            reqObject.put("query", "query NameImages($id: ID!, $before: ID, $after: ID, $jumpTo: ID, $first: Int, $last: Int, $lastYes: Boolean!, $firstYes: Boolean!) {\n  name(id: $id) {\n    nameText {\n      text\n      __typename\n    }\n    meta {\n      publicationStatus\n      __typename\n    }\n    images(first: $first, after: $after, jumpTo: $jumpTo) @include(if: $firstYes) {\n      total\n      ...MediaViewerMeta\n      __typename\n    }\n    wrapFront: images(last: $last, before: $before) @include(if: $lastYes) {\n      total\n      ...MediaViewerMeta\n      __typename\n    }\n    wrapBack: images(first: $first) @include(if: $firstYes) {\n      total\n      ...MediaViewerMeta\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment MediaViewerMeta on ImageConnection {\n  pageInfo {\n    endCursor\n    hasNextPage\n    hasPreviousPage\n    startCursor\n    __typename\n  }\n  edges {\n    position\n    cursor\n    node {\n      ...MediaViewerImageMeta\n      ...MediaSheetImageMeta\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment MediaViewerImageMeta on Image {\n  id\n  url\n  height\n  width\n  caption {\n    plainText\n    __typename\n  }\n}\n\nfragment MediaSheetImageMeta on Image {\n  copyright\n  createdBy\n  caption {\n    plaidHtml\n    __typename\n  }\n  titles {\n    id\n    titleText {\n      text\n      __typename\n    }\n    __typename\n  }\n  source {\n    attributionUrl\n    text\n    banner {\n      url\n      attributionUrl\n      __typename\n    }\n    __typename\n  }\n  names {\n    id\n    nameText {\n      text\n      __typename\n    }\n    __typename\n  }\n  countries {\n    text\n    __typename\n  }\n  languages {\n    text\n    __typename\n  }\n  correctionLink(relatedId: $id, contributionContext: {isInIframe: true, returnUrl: \"https://www.imdb.com/close_me\", business: \"consumer\"}) {\n    url\n    __typename\n  }\n  reportingLink(relatedId: $id, contributionContext: {isInIframe: true, returnUrl: \"https://www.imdb.com/close_me\", business: \"consumer\"}) {\n    url\n    __typename\n  }\n}\n");
            reqObject.put("variables", initVariableObject(nameId, imageId, beforeId, afterId, first, last));
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

                            setImageFields(image, item);

                            try {
                                JSONObject description = item.getJSONObject("caption");
                                image.setDescriptionHtml(description.getString("plaidHtml"));
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

    @GetMapping("/titles/{titleId}/slider")
    @ApiOperation("Images of a title as slider with details")
    ApiResponse<ImageGallery> fetchImagesOfTitleAsSliderWithDetails(
            @ApiParam("Ex. tt1160419")
            @PathVariable("titleId") String titleId,
            @ApiParam("Ex. rm1484838913")
            @RequestParam(value = "imageId", required = false) String imageId,
            @ApiParam("images before this cursor id")
            @RequestParam(value = "before", required = false) String beforeId,
            @ApiParam("images after this cursor id")
            @RequestParam(value = "after", required = false) String afterId,
            @ApiParam("number of last images")
            @RequestParam(value = "last", required = false, defaultValue = "6") Integer last,
            @ApiParam("number of first images")
            @RequestParam(value = "first", required = false, defaultValue = "6") Integer first) {

        ImageGallery imageGallery = new ImageGallery();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(AppConstants.IMDB_URL_GRAPH_QL);
        StringEntity params;
        try {
            JSONObject reqObject = new JSONObject();
            reqObject.put("query", "query TitleImages($id: ID!, $before: ID, $after: ID, $jumpTo: ID, $first: Int, $last: Int, $lastYes: Boolean!, $firstYes: Boolean!) {\n  title(id: $id) {\n    titleText {\n      text\n      __typename\n    }\n    meta {\n      publicationStatus\n      __typename\n    }\n    releaseYear {\n      year\n      __typename\n    }\n    images(first: $first, after: $after, jumpTo: $jumpTo) @include(if: $firstYes) {\n      total\n      ...MediaViewerMeta\n      __typename\n    }\n    wrapFront: images(last: $last, before: $before) @include(if: $lastYes) {\n      total\n      ...MediaViewerMeta\n      __typename\n    }\n    wrapBack: images(first: $first) @include(if: $firstYes) {\n      total\n      ...MediaViewerMeta\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment MediaViewerMeta on ImageConnection {\n  pageInfo {\n    endCursor\n    hasNextPage\n    hasPreviousPage\n    startCursor\n    __typename\n  }\n  edges {\n    position\n    cursor\n    node {\n      ...MediaViewerImageMeta\n      ...MediaSheetImageMeta\n      __typename\n    }\n    __typename\n  }\n}\n\nfragment MediaViewerImageMeta on Image {\n  id\n  url\n  height\n  width\n  caption {\n    plainText\n    __typename\n  }\n}\n\nfragment MediaSheetImageMeta on Image {\n  copyright\n  createdBy\n  caption {\n    plaidHtml\n    __typename\n  }\n  titles {\n    id\n    titleText {\n      text\n      __typename\n    }\n    __typename\n  }\n  source {\n    attributionUrl\n    text\n    banner {\n      url\n      attributionUrl\n      __typename\n    }\n    __typename\n  }\n  names {\n    id\n    nameText {\n      text\n      __typename\n    }\n    __typename\n  }\n  countries {\n    text\n    __typename\n  }\n  languages {\n    text\n    __typename\n  }\n  correctionLink(relatedId: $id, contributionContext: {isInIframe: true, returnUrl: \"https://www.imdb.com/close_me\", business: \"consumer\"}) {\n    url\n    __typename\n  }\n  reportingLink(relatedId: $id, contributionContext: {isInIframe: true, returnUrl: \"https://www.imdb.com/close_me\", business: \"consumer\"}) {\n    url\n    __typename\n  }\n}\n");
            reqObject.put("variables", initVariableObject(titleId, imageId, beforeId, afterId, first, last));
            params = new StringEntity(reqObject.toString());
            httppost.addHeader("content-type", "application/json");
            httppost.setEntity(params);
            HttpResponse response = httpClient.execute(httppost);
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            try {
                imageGallery.setTitle(responseJson.getJSONObject("data").getJSONObject("title").getJSONObject("titleText").getString("text"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setEndCursor(responseJson.getJSONObject("data").getJSONObject("title").getJSONObject("images").getJSONObject("pageInfo").getString("endCursor"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setStartCursor(responseJson.getJSONObject("data").getJSONObject("title").getJSONObject("images").getJSONObject("pageInfo").getString("startCursor"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setHasNextPage(responseJson.getJSONObject("data").getJSONObject("title").getJSONObject("images").getJSONObject("pageInfo").getBoolean("hasNextPage"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                imageGallery.setHasPreviousPage(responseJson.getJSONObject("data").getJSONObject("title").getJSONObject("images").getJSONObject("pageInfo").getBoolean("hasPreviousPage"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                List<ImageGallery.Image> images = new ArrayList<>();

                for (Object o : responseJson.getJSONObject("data").getJSONObject("title").getJSONObject("images").getJSONArray("edges")) {

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

                            setImageFields(image, item);

                            try {
                                JSONObject description = item.getJSONObject("caption");
                                image.setDescriptionHtml(description.getString("plaidHtml"));
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

    private JSONObject initVariableObject(String id, String imageId, String beforeId, String afterId, Integer first, Integer last) {
        JSONObject varObject = new JSONObject();
        varObject.put("id", id);
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

        return varObject;
    }

    private void extractHeaderTitle(ImageList imageGallery, Document doc) {
        try {
            imageGallery.setTitle(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("h3").text());

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            imageGallery.setSubtitle(generateImage(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("a").get(0).getElementsByTag("img").attr("src"), 0, 0));

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            imageGallery.setImages(extractImages(doc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageFields(ImageGallery.Image image, JSONObject item) {
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
    }

    private List<ImageList.Image> extractImages(Document doc) {
        List<ImageList.Image> images = new ArrayList<>();

        for (Element element : doc.getElementsByClass("media_index_thumb_list").get(0).getElementsByTag("a")) {
            try {
                ImageList.Image image = new ImageList.Image();
                image.setId(extractImageId(element.attr("href")));
                image.setUrl(generateImage(element.getElementsByTag("img").get(0).attr("src"), 512, 512));
                images.add(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return images;
    }

}
