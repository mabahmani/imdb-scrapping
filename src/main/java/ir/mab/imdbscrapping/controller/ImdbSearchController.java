package ir.mab.imdbscrapping.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import java.util.Objects;

import static ir.mab.imdbscrapping.util.Utils.*;

@RestController
@RequestMapping(path = AppConstants.Api.SEARCH)
public class ImdbSearchController {

    @GetMapping("/titles")
    @ApiOperation("Search Titles (Movies, Tv Shows, ...)")
    ApiResponse<List<MovieSearch>> searchMoviesByGenresAndKeywords(@RequestParam(value = "genre", required = false) String genre, @RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "start", defaultValue = "1") String start) {
        List<MovieSearch> movieSearches = new ArrayList<>();
        try {
            Document doc;
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
                        movieSearch.setSummary(Objects.requireNonNull(element.getElementsByClass("ratings-bar").get(0).nextElementSibling()).text());
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
                        Elements ghostElements = Objects.requireNonNull(castElement).getElementsByClass("ghost");

                        if (ghostElements.isEmpty()) {
                            try {
                                List<MovieSearch.Name> names = new ArrayList<>();
                                for (Element starElement : castElement.getElementsByTag("a")) {

                                    names.add(extractMovieNameList(starElement));
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
                                    MovieSearch.Name name = extractMovieNameList(childElement);
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

            return getNameListResponse(nameSearches, doc);
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    @GetMapping("/names")
    @ApiOperation("Search Names (Celebrities, Directors, Casts, ...)")
    ApiResponse<List<NameSearch>> searchNames(
            @ApiParam("Ex. Tom Hardy")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam("Names that birth date are between two dates \n Format: YYYY-MM-DD, YYYY-MM, or YYYY\n Ex. 1999-01-01,2000-12-31")
            @RequestParam(value = "birthDate", required = false) String birthDate,
            @ApiParam("Names that birth date are in this month/day\n Format: Format: MM-DD\n Ex. 10-02")
            @RequestParam(value = "birthMonthDay", required = false) String birthMonthDay,
            @ApiParam("Name Groups\n oscar_best_actress_nominees,\n oscar_best_actor_nominees,\n oscar_best_actress_winners,\n oscar_best_actor_winners,\n oscar_best_supporting_actress_nominees,\n oscar_best_supporting_actor_nominees,\n oscar_best_supporting_actress_winners,\n oscar_best_supporting_actor_winners,\n oscar_best_director_nominees,\n best_director_winner,\n oscar_winner,\n oscar_nominee")
            @RequestParam(value = "groups", required = false) String groups,
            @ApiParam("Star Sign\n aquarius, pisces, aries, taurus, gemini, cancer, leo, virgo, libra, scorpio, sagittarius, capricorn")
            @RequestParam(value = "starSign", required = false) String starSign,
            @ApiParam("Ex. Canada")
            @RequestParam(value = "birthPlace", required = false) String birthPlace,
            @ApiParam("Names that death date are between two dates \n Format: YYYY-MM-DD, YYYY-MM, or YYYY\n Ex. 1999-01-01,2000-12-31")
            @RequestParam(value = "deathDate", required = false) String deathDate,
            @ApiParam("Ex. Canada")
            @RequestParam(value = "deathPlace", required = false) String deathPlace,
            @ApiParam("Ex. male,female")
            @RequestParam(value = "gender", defaultValue = "male,female") String gender,
            @ApiParam("Filmography \n Ex. tt0111161")
            @RequestParam(value = "roles", required = false) String roles,
            @ApiParam("Search for words that might appear in the Mini-Biography.")
            @RequestParam(value = "bio", required = false) String bio,
            @ApiParam("Ex. starmeter,asc\n starmeter,desc\n alpha,asc\n alpha,desc\n birth_date,asc\n birth_date,desc\n death_date,asc\n death_date,desc")
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "start", defaultValue = "1") String start) {
        List<NameSearch> nameSearches = new ArrayList<>();
        try {
            StringBuilder reqUrl = new StringBuilder(AppConstants.IMDB_SEARCH_NAME + "?");

            reqUrl.append(String.format("gender=%s",gender));

            if (name != null && !name.isEmpty()){
                reqUrl.append(String.format("&name=%s",name));
            }
            if (birthDate != null && !birthDate.isEmpty()){
                reqUrl.append(String.format("&birth_date=%s",birthDate));
            }
            if (birthMonthDay != null && !birthMonthDay.isEmpty()){
                reqUrl.append(String.format("&birth_monthday=%s",birthDate));
            }
            if (groups != null && !groups.isEmpty()){
                reqUrl.append(String.format("&groups=%s",groups));
            }
            if (starSign != null && !starSign.isEmpty()){
                reqUrl.append(String.format("&star_sign=%s",starSign));
            }
            if (birthPlace != null && !birthPlace.isEmpty()){
                reqUrl.append(String.format("&birth_place=%s",birthPlace));
            }
            if (deathDate != null && !deathDate.isEmpty()){
                reqUrl.append(String.format("&death_date=%s",deathDate));
            }
            if (deathPlace != null && !deathPlace.isEmpty()){
                reqUrl.append(String.format("&death_place=%s",deathPlace));
            }
            if (roles != null && !roles.isEmpty()){
                reqUrl.append(String.format("&roles=%s",roles));
            }
            if (bio != null && !bio.isEmpty()){
                reqUrl.append(String.format("&bio=%s",bio));
            }
            if (sort != null && !sort.isEmpty()){
                reqUrl.append(String.format("&sort=%s",sort));
            }
            if (start != null && !start.isEmpty()){
                reqUrl.append(String.format("&start=%s",start));
            }

            Document doc = Jsoup.connect(reqUrl.toString()).get();

            return getNameListResponse(nameSearches, doc);
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    private ApiResponse<List<NameSearch>> getNameListResponse(List<NameSearch> nameSearches, Document doc) {
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
    }

    private MovieSearch.Name extractMovieNameList(Element starElement) {
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

        return name;
    }

}
