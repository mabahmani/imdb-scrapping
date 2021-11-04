package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.NameDetails;
import ir.mab.imdbscrapping.model.Video;
import ir.mab.imdbscrapping.util.AppConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = AppConstants.Api.NAMES)
public class ImdbNameController {
    private final Pattern imagePattern = Pattern.compile("rm+[0-9]+");
    private final Pattern titlePattern = Pattern.compile("tt+[0-9]+");

    @GetMapping("/{nameId}")
    ApiResponse<NameDetails> fetchNameDetails(@PathVariable("nameId") String nameId) {
        NameDetails nameDetails = new NameDetails();
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_URL + String.format("/name/%s", nameId)).get();
            nameDetails.setId(nameId);
            try {
                nameDetails.setName(doc.getElementsByClass("header").get(0).getElementsByClass("itemprop").get(0).text());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                List<String> jobs = new ArrayList<>();
                for (Element element : doc.getElementsByClass("infobar").get(0).getElementsByClass("itemprop"))
                    jobs.add(element.text());
                nameDetails.setJobTitles(jobs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                nameDetails.setAvatar(generateCover(doc.getElementsByAttributeValue("id", "name-poster").attr("src"), 0, 0));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                nameDetails.setBioSummary(doc.getElementsByClass("name-trivia-bio-text").get(0).getElementsByClass("inline").get(0).ownText());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                nameDetails.setBirthDate(doc.getElementsByAttributeValue("id", "name-born-info").get(0).getElementsByTag("time").attr("datetime"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                nameDetails.setBirthDateMonthDay(doc.getElementsByAttributeValue("id", "name-born-info").get(0).getElementsByTag("a").get(0).text());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                nameDetails.setBirthDateYear(doc.getElementsByAttributeValue("id", "name-born-info").get(0).getElementsByTag("a").get(1).text());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                nameDetails.setBirthPlace(doc.getElementsByAttributeValue("id", "name-born-info").get(0).getElementsByTag("a").get(2).text());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                List<NameDetails.Photo> photos = new ArrayList<>();
                for (Element element : doc.getElementsByClass("mediastrip").get(0).getElementsByTag("a")) {
                    NameDetails.Photo photo = new NameDetails.Photo();
                    photo.setId(extractImageId(element.attr("href")));
                    photo.setUrl(generateCover(element.getElementsByTag("img").get(0).attr("loadlate"), 512, 512));
                    photos.add(photo);
                }
                nameDetails.setPhotos(photos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                List<NameDetails.KnownFor> knownForList = new ArrayList<>();
                for (Element element : doc.getElementsByClass("knownfor-title")) {
                    NameDetails.KnownFor knownFor = new NameDetails.KnownFor();
                    knownFor.setCover(generateCover(element.getElementsByClass("uc-add-wl-widget-container").get(0).getElementsByTag("img").attr("src"), 256, 768));
                    knownFor.setTitle(element.getElementsByClass("knownfor-title-role").get(0).getElementsByTag("a").text());
                    knownFor.setInMovieName(element.getElementsByClass("knownfor-title-role").get(0).getElementsByTag("span").text());
                    knownFor.setYear(element.getElementsByClass("knownfor-year").text());
                    knownFor.setTitleId(element.getElementsByClass("uc-add-wl-widget-container").get(0).getElementsByTag("img").attr("data-tconst"));
                    knownForList.add(knownFor);
                }
                nameDetails.setKnownForList(knownForList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                List<NameDetails.Filmography> filmographies = new ArrayList<>();
                List<Element> headElements = new ArrayList<>();
                List<Element> categoryElements = new ArrayList<>();
                try {
                    for (Element element : doc.getElementById("filmography").children()) {
                        if (element.attr("class").equals("head")) {
                            headElements.add(element);
                        } else {
                            categoryElements.add(element);
                        }
                    }

                    for (int i = 0; i < headElements.size(); i++) {
                        NameDetails.Filmography filmography = new NameDetails.Filmography();
                        try {
                            filmography.setHeadTitle(headElements.get(i).getElementsByTag("a").text());
                            filmography.setNumberOfCredits(headElements.get(i).ownText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            List<NameDetails.Filmography.Credit> credits = new ArrayList<>();
                            for (Element element : categoryElements.get(i).getElementsByClass("filmo-row")) {
                                NameDetails.Filmography.Credit credit = new NameDetails.Filmography.Credit();
                                try {
                                    credit.setYear(element.getElementsByClass("year_column").text());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    credit.setId(extractTitleId(element.getElementsByTag("b").get(0).getElementsByTag("a").attr("href")));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    credit.setTitle(element.getElementsByTag("b").get(0).getElementsByTag("a").text());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    credit.setSubtitle(element.ownText());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    List<NameDetails.Filmography.Credit.Episode> episodes = new ArrayList<>();
                                    for (Element element1 : element.getElementsByClass("filmo-episodes")) {
                                        NameDetails.Filmography.Credit.Episode episode = new NameDetails.Filmography.Credit.Episode();
                                        try {
                                            episode.setId(extractTitleId(element1.getElementsByTag("a").attr("href")));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            episode.setTitle(element1.getElementsByTag("a").text());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            episode.setSubtitle(element1.ownText());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        episodes.add(episode);
                                    }
                                    credit.setEpisodes(episodes);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                credits.add(credit);
                            }
                            filmography.setCredits(credits);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        filmographies.add(filmography);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                nameDetails.setFilmographies(filmographies);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                List<NameDetails.RelatedVideo> relatedVideos = new ArrayList<>();
                for (Element element : doc.getElementsByClass("mediastrip_big").get(0).children()) {
                    NameDetails.RelatedVideo relatedVideo = new NameDetails.RelatedVideo();
                    relatedVideo.setVideoId(element.getElementsByTag("a").attr("data-video"));
                    relatedVideo.setCover(generateCover(element.getElementsByTag("a").get(0).getElementsByTag("img").attr("loadlate"), 400, 300));
                    relatedVideo.setTitle(element.getElementsByTag("a").attr("title"));
                    relatedVideos.add(relatedVideo);
                }
                nameDetails.setRelatedVideos(relatedVideos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                List<NameDetails.PersonalDetail> personalDetails = new ArrayList<>();

                for (Element element : doc.getElementsByClass("article").get(6).children()) {
                    if (element.id().contains("details")) {
                        NameDetails.PersonalDetail personalDetail = new NameDetails.PersonalDetail();
                        try {
                            personalDetail.setTitle(element.getElementsByTag("h4").get(0).ownText());
                            personalDetail.setSubtitle(element.ownText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        List<NameDetails.PersonalDetail.LinkText> linkTexts = new ArrayList<>();
                        for (Element element1 : element.getElementsByTag("a")) {
                            try {
                                NameDetails.PersonalDetail.LinkText linkText = new NameDetails.PersonalDetail.LinkText();
                                linkText.setText(element1.text());
                                linkText.setUrl(element1.attr("href"));
                                linkTexts.add(linkText);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        personalDetail.setLinkTexts(linkTexts);

                        personalDetails.add(personalDetail);
                    }

                }
                nameDetails.setPersonalDetails(personalDetails);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                NameDetails.Trailer trailer = new NameDetails.Trailer();
                try {
                    trailer.setCover(
                            generateCover(
                                    doc.getElementsByClass("heroWidget").get(0)
                                            .getElementsByClass("slate").get(0)
                                            .getElementsByTag("a").get(0)
                                            .getElementsByTag("img").attr("src"), 954, 536));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    trailer.setVideoId(doc.getElementsByClass("heroWidget").get(0)
                            .getElementsByClass("slate").get(0)
                            .getElementsByTag("a").attr("data-video"));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    trailer.setCaption(doc.getElementsByClass("heroWidget").get(0)
                            .getElementsByClass("slate").get(0)
                            .getElementsByClass("caption").get(0).getElementsByTag("div").get(0).text());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                nameDetails.setTrailer(trailer);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(nameDetails, null, true);
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

    private String extractImageId(String text) {
        Matcher m = imagePattern.matcher(text);
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
}
