package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.MovieSearch;
import ir.mab.imdbscrapping.model.NameSearch;
import ir.mab.imdbscrapping.util.AppConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ir.mab.imdbscrapping.util.Utils.generateImage;

@RestController
@RequestMapping(path = AppConstants.Api.SEARCH)
public class ImdbSearchController {

    private final Pattern namePattern = Pattern.compile("nm+[0-9]+");
    private final Pattern titlePattern = Pattern.compile("tt+[0-9]+");

    @GetMapping("/titles")
    ApiResponse<List<MovieSearch>> searchMoviesByGenreKeywords(@RequestParam(value = "genre", required = false) String genre, @RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "start", defaultValue = "1") String start) {
        List<MovieSearch> movieSearches = new ArrayList<>();
        try {
            Document doc = null;
            if (genre != null && keyword != null) {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_SEARCH_TITLE + "?genres=%s&keywords=%s&start=%s", genre, keyword, start)).get();
            } else if (genre != null) {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_SEARCH_TITLE + "?genres=%s&start=%s", genre, start)).get();
            } else if (keyword != null) {
                doc = Jsoup.connect(String.format(AppConstants.IMDB_SEARCH_TITLE + "?keywords=%s&start=%s", keyword, start)).get();
            } else {
                return new ApiResponse<>(null, "genre or keyword query needed!", false);
            }

            try {
                for (Element element : doc.getElementsByClass("lister-list").get(0).getElementsByClass("lister-item")) {
                    MovieSearch movieSearch = new MovieSearch();
                    try {
                        movieSearch.setCover(generateImage(element.getElementsByClass("lister-item-image").get(0).getElementsByTag("img").attr("loadLate"), 268, 392));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSearch.setTitleId(element.getElementsByClass("lister-item-image").get(0).getElementsByTag("img").attr("data-tconst"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSearch.setPosition(element.getElementsByClass("lister-item-content").get(0).getElementsByClass("lister-item-header").get(0).getElementsByClass("lister-item-index").get(0).ownText().replace(".", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSearch.setTitle(element.getElementsByClass("lister-item-content").get(0).getElementsByClass("lister-item-header").get(0).getElementsByTag("a").get(0).ownText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSearch.setYear(element.getElementsByClass("lister-item-content").get(0).getElementsByClass("lister-item-header").get(0).getElementsByClass("lister-item-year").get(0).ownText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSearch.setCertificate(element.getElementsByClass("lister-item-content").get(0).getElementsByClass("certificate").text());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSearch.setRuntime(element.getElementsByClass("lister-item-content").get(0).getElementsByClass("runtime").text());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSearch.setGenres(element.getElementsByClass("lister-item-content").get(0).getElementsByClass("genre").text());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSearch.setImdbRating(element.getElementsByClass("lister-item-content").get(0).getElementsByAttributeValue("name", "ir").attr("data-value"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSearch.setSummary(element.getElementsByClass("ratings-bar").get(0).nextElementSibling().text());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSearch.setNumberOfVotes(element.getElementsByAttributeValue("name", "nv").get(0).attr("data-value"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Element castElement = element.getElementsByClass("sort-num_votes-visible").get(0).previousElementSibling();
                        Elements ghostElements = castElement.getElementsByClass("ghost");

                        if (ghostElements.isEmpty()) {
                            try {
                                List<MovieSearch.Name> names = new ArrayList<>();
                                for (Element starElement : castElement.getElementsByTag("a")) {

                                    MovieSearch.Name name = new MovieSearch.Name();
                                    try {
                                        name.setName(starElement.text());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        name.setNameId(extractNameId(starElement.attr("href")));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    names.add(name);
                                }
                                movieSearch.setStars(names);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            List<MovieSearch.Name> directors = new ArrayList<>();
                            List<MovieSearch.Name> stars = new ArrayList<>();
                            boolean addToDirectors = true;

                            for (Element childElement : castElement.children()) {
                                if (childElement.tagName().equals("a")) {
                                    MovieSearch.Name name = new MovieSearch.Name();
                                    try {
                                        name.setName(childElement.text());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        name.setNameId(extractNameId(childElement.attr("href")));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (addToDirectors)
                                        directors.add(name);
                                    else
                                        stars.add(name);
                                } else if (childElement.tagName().equals("span")) {
                                    addToDirectors = false;
                                }
                            }

                            movieSearch.setStars(stars);
                            movieSearch.setDirectors(directors);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    movieSearches.add(movieSearch);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return new ApiResponse<>(movieSearches, null, true);
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    @GetMapping("/birthday")
    ApiResponse<List<NameSearch>> searchNamesByBirthday(@RequestParam(value = "monthday") String date, @RequestParam(value = "start", defaultValue = "1") String start) {
        List<NameSearch> nameSearches = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_SEARCH_NAME + "?birth_monthday=%s&start=%s", date, start)).get();

            try {
                for (Element element : doc.getElementsByClass("lister-list").get(0).getElementsByClass("lister-item")) {
                    NameSearch nameSearch = new NameSearch();
                    try {
                        nameSearch.setAvatar(generateImage(element.getElementsByClass("lister-item-image").get(0).getElementsByTag("img").attr("src"), 280, 418));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        nameSearch.setNameId(extractNameId(element.getElementsByClass("lister-item-image").get(0).getElementsByTag("a").attr("href")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        nameSearch.setPosition(element.getElementsByClass("lister-item-content").get(0).getElementsByClass("lister-item-header").get(0).getElementsByClass("lister-item-index").get(0).ownText().replace(".", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        nameSearch.setName(element.getElementsByClass("lister-item-content").get(0).getElementsByClass("lister-item-header").get(0).getElementsByTag("a").get(0).ownText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        nameSearch.setSummary(element.getElementsByClass("lister-item-content").get(0).getElementsByTag("p").get(1).text());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        NameSearch.TopMovie topMovie = new NameSearch.TopMovie();
                        try {
                            topMovie.setRole(element.getElementsByClass("lister-item-content").get(0).getElementsByTag("p").get(0).ownText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            topMovie.setTitle(element.getElementsByClass("lister-item-content").get(0).getElementsByTag("p").get(0).getElementsByTag("a").get(0).ownText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            topMovie.setTitleId(extractTitleId(element.getElementsByClass("lister-item-content").get(0).getElementsByTag("p").get(0).getElementsByTag("a").attr("href")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        nameSearch.setTopMovie(topMovie);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    nameSearches.add(nameSearch);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return new ApiResponse<>(nameSearches, null, true);
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    @GetMapping("/names")
    ApiResponse<List<NameSearch>> searchNames(@RequestParam(value = "gender", defaultValue = "male,female") String gender, @RequestParam(value = "start", defaultValue = "1") String start) {
        List<NameSearch> nameSearches = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_SEARCH_NAME + "?gender=%s&start=%s", gender, start)).get();

            try {
                for (Element element : doc.getElementsByClass("lister-list").get(0).getElementsByClass("lister-item")) {
                    NameSearch nameSearch = new NameSearch();
                    try {
                        nameSearch.setAvatar(generateImage(element.getElementsByClass("lister-item-image").get(0).getElementsByTag("img").attr("src"), 280, 418));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        nameSearch.setNameId(extractNameId(element.getElementsByClass("lister-item-image").get(0).getElementsByTag("a").attr("href")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        nameSearch.setPosition(element.getElementsByClass("lister-item-content").get(0).getElementsByClass("lister-item-header").get(0).getElementsByClass("lister-item-index").get(0).ownText().replace(".", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        nameSearch.setName(element.getElementsByClass("lister-item-content").get(0).getElementsByClass("lister-item-header").get(0).getElementsByTag("a").get(0).ownText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        nameSearch.setSummary(element.getElementsByClass("lister-item-content").get(0).getElementsByTag("p").get(1).text());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        NameSearch.TopMovie topMovie = new NameSearch.TopMovie();
                        try {
                            topMovie.setRole(element.getElementsByClass("lister-item-content").get(0).getElementsByTag("p").get(0).ownText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            topMovie.setTitle(element.getElementsByClass("lister-item-content").get(0).getElementsByTag("p").get(0).getElementsByTag("a").get(0).ownText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            topMovie.setTitleId(extractTitleId(element.getElementsByClass("lister-item-content").get(0).getElementsByTag("p").get(0).getElementsByTag("a").attr("href")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        nameSearch.setTopMovie(topMovie);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    nameSearches.add(nameSearch);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return new ApiResponse<>(nameSearches, null, true);
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
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

}
