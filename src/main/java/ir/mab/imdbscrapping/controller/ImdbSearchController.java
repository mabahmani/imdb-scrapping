package ir.mab.imdbscrapping.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.MovieSearch;
import ir.mab.imdbscrapping.model.NameSearch;
import ir.mab.imdbscrapping.model.Suggestion;
import ir.mab.imdbscrapping.util.AppConstants;
import org.json.JSONObject;
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

    @GetMapping("/")
    @ApiOperation("Search All")
    ApiResponse<Suggestion> search(
            @ApiParam("a word or phrase that describe your search")
            @RequestParam(value = "term") String term
    ){
        Suggestion suggestion = new Suggestion();
        try {
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_URL_SUGGESTION + "%s/%s.json",term.charAt(0),term)).ignoreContentType(true).get();
            JSONObject response = new JSONObject(doc.body().text());
            List<Suggestion.Data> dataList = new ArrayList<>();
            for (Object o: response.getJSONArray("d")){
                try {
                    JSONObject dataObject = (JSONObject) o;
                    Suggestion.Data data = new Suggestion.Data();
                    try {
                        data.setImage(dataObject.getJSONObject("i").getString("imageUrl"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        data.setId(dataObject.getString("id"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        data.setTitle(dataObject.getString("l"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        data.setType(dataObject.getString("q"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        data.setRank(dataObject.getInt("rank"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        data.setSubtitle(dataObject.getString("s"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        data.setYear(dataObject.getInt("y"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        List<Suggestion.Data.Video> videos = new ArrayList<>();
                        for (Object v: dataObject.getJSONArray("v")){
                            try {
                                JSONObject videoObject = (JSONObject) v;
                                Suggestion.Data.Video video = new Suggestion.Data.Video();

                                try {
                                    video.setId(videoObject.getString("id"));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try {
                                    video.setTitle(videoObject.getString("l"));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try {
                                    video.setRuntime(videoObject.getString("s"));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try {
                                    video.setPreview(videoObject.getJSONObject("i").getString("imageUrl"));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                videos.add(video);

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        data.setVideos(videos);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    dataList.add(data);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            suggestion.setData(dataList);
        }catch (IOException e){
            return new ApiResponse<>(null,e.getMessage(),false);
        }

        return new ApiResponse<>(suggestion,null,true);
    }

    @GetMapping("/titles")
    @ApiOperation("Search Titles (Movies, Tv Shows, ...)")
    ApiResponse<List<MovieSearch>> searchTitles(
            @ApiParam("Ex.The Godfather")
            @RequestParam(value = "title", required = false) String title,
            @ApiParam("Ex. \nfeature,\ntv_movie,\ntv_series,\ntv_episode,\ntv_special,\ntv_miniseries,\ndocumentary,\nvideo_game,\nshort,\nvideo,\ntv_short,\npodcast_series,\npodcast_episode")
            @RequestParam(value = "titleType", required = false) String titleType,
            @ApiParam("Search Titles that release date are between two dates \n Format: YYYY-MM-DD, YYYY-MM, or YYYY \n Ex. 1999-01-01,2000-12-31")
            @RequestParam(value = "releaseDate", required = false) String releaseDate,
            @ApiParam("Search Titles that user rating are between two rates \n Format: 1.0,10 \n Ex. 5.6,7.8")
            @RequestParam(value = "userRating", required = false) String userRating,
            @ApiParam("Ex. Action,Drama, ...")
            @RequestParam(value = "genres", required = false) String genres,
            @ApiParam("Title Groups \n top_100,\ntop_250,\ntop_1000,\nbottom_100,\nbottom_250,\nbottom_1000\noscar_winner,\nemmy_winner,\ngolden_globe_winner,\noscar_nominee,\nemmy_nominee,\ngolden_globe_nominee,\nbest_picture_winner,\nbest_director_winner,\noscar_best_picture_nominees,\noscar_best_director_nominees,\nnational_film_preservation_board_winner,\nrazzie_winner,\nrazzie_nominee")
            @RequestParam(value = "groups", required = false) String groups,
            @ApiParam("Companies \n Ex. fox, sony, dreamworks, mgm, paramount, universal, disney, warner, ...")
            @RequestParam(value = "companies", required = false) String companies,
            @ApiParam("Certificates \n Ex. US%3AG,\n US%3APG,\n US%3APG-13,\n US%3AR,\n US%3ANC-17")
            @RequestParam(value = "certificates", required = false) String certificates,
            @ApiParam("Colors \n Ex. color,\n black_and_white,\n colorized,\n aces")
            @RequestParam(value = "colors", required = false) String colors,
            @ApiParam("Countries \n Ex. af, in, ir, ...")
            @RequestParam(value = "countries", required = false) String countries,
            @ApiParam("Search for a notable object, concept, style or aspect. \n Ex. superhero, ...")
            @RequestParam(value = "keywords", required = false) String keywords,
            @ApiParam("Ex. fr, fa, en, ...")
            @RequestParam(value = "languages", required = false) String languages,
            @ApiParam("Filming Locations \n Ex. canada, iran, ...")
            @RequestParam(value = "locations", required = false) String locations,
            @ApiParam("Search for words that might appear in the plot summary.")
            @RequestParam(value = "plot", required = false) String plot,
            @ApiParam("Cast \n Ex. nm1869101")
            @RequestParam(value = "role", required = false) String role,
            @ApiParam("Runtime between Minutes \n Ex. 100,120")
            @RequestParam(value = "runtime", required = false) String runtime,
            @ApiParam("Sort \n moviemeter,acs \n moviemeter,desc \n alpha,asc \n alpha,desc \n user_rating,asc \n user_rating,desc \n num_votes,asc \n num_votes,desc \n boxoffice_gross_us,asc \n boxoffice_gross_us,desc \n runtime,asc \n runtime,desc \n year,asc \n year,desc \n release_date,asc \n release_date,desc")
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "start", defaultValue = "1") String start) {
        List<MovieSearch> movieSearches = new ArrayList<>();
        try {

            StringBuilder reqUrl = new StringBuilder(AppConstants.IMDB_SEARCH_TITLE + "?");
            reqUrl.append(String.format("start=%s", start));
            if (
                    title == null &&
                            titleType == null &&
                            releaseDate == null &&
                            userRating == null &&
                            genres == null &&
                            groups == null &&
                            companies == null &&
                            certificates == null &&
                            colors == null &&
                            countries == null &&
                            keywords == null &&
                            languages == null &&
                            locations == null &&
                            plot == null &&
                            role == null &&
                            runtime == null

            ) {
                titleType = "feature,tv_series";
            }

            if (title != null && !title.isEmpty()) {
                reqUrl.append(String.format("&title=%s", title));
            }
            if (titleType != null && !titleType.isEmpty()) {
                reqUrl.append(String.format("&title_type=%s", titleType));
            }
            if (releaseDate != null && !releaseDate.isEmpty()) {
                reqUrl.append(String.format("&release_date=%s", releaseDate));
            }
            if (userRating != null && !userRating.isEmpty()) {
                reqUrl.append(String.format("&user_rating=%s", userRating));
            }
            if (genres != null && !genres.isEmpty()) {
                reqUrl.append(String.format("&genres=%s", genres));
            }
            if (groups != null && !groups.isEmpty()) {
                reqUrl.append(String.format("&groups=%s", groups));
            }
            if (companies != null && !companies.isEmpty()) {
                reqUrl.append(String.format("&companies=%s", companies));
            }
            if (certificates != null && !certificates.isEmpty()) {
                reqUrl.append(String.format("&certificates=%s", certificates));
            }
            if (colors != null && !colors.isEmpty()) {
                reqUrl.append(String.format("&colors=%s", colors));
            }
            if (countries != null && !countries.isEmpty()) {
                reqUrl.append(String.format("&countries=%s", countries));
            }
            if (keywords != null && !keywords.isEmpty()) {
                reqUrl.append(String.format("&keywords=%s", keywords));
            }
            if (languages != null && !languages.isEmpty()) {
                reqUrl.append(String.format("&languages=%s", languages));
            }
            if (locations != null && !locations.isEmpty()) {
                reqUrl.append(String.format("&locations=%s", locations));
            }
            if (plot != null && !plot.isEmpty()) {
                reqUrl.append(String.format("&plot=%s", plot));
            }
            if (role != null && !role.isEmpty()) {
                reqUrl.append(String.format("&role=%s", role));
            }
            if (runtime != null && !runtime.isEmpty()) {
                reqUrl.append(String.format("&runtime=%s", runtime));
            }
            if (sort != null && !sort.isEmpty()) {
                reqUrl.append(String.format("&sort=%s", sort));
            }


            System.out.println(reqUrl.toString());
            Document doc = Jsoup.connect(reqUrl.toString()).get();


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
                        movieSearch.setSummary(element.getElementsByClass("lister-item-content").get(0).getElementsByClass("text-muted").get(2).text());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSearch.setNumberOfVotes(element.getElementsByAttributeValue("name", "nv").get(0).attr("data-value"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Element castElement = element.getElementsByClass("lister-item-content").get(0).getElementsByClass("text-muted").get(2).nextElementSibling();

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

    @GetMapping("/names")
    @ApiOperation("Search Names (Celebrities, Directors, Casts, ...)")
    ApiResponse<List<NameSearch>> searchNames(
            @ApiParam("Ex. Tom Hardy")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam("Search Names that birth date are between two dates \n Format: YYYY-MM-DD, YYYY-MM, or YYYY\n Ex. 1999-01-01,2000-12-31")
            @RequestParam(value = "birthDate", required = false) String birthDate,
            @ApiParam("Search Names that birth date are in this month/day\n Format: MM-DD\n Ex. 10-02")
            @RequestParam(value = "birthMonthDay", required = false) String birthMonthDay,
            @ApiParam("Name Groups\n oscar_best_actress_nominees,\n oscar_best_actor_nominees,\n oscar_best_actress_winners,\n oscar_best_actor_winners,\n oscar_best_supporting_actress_nominees,\n oscar_best_supporting_actor_nominees,\n oscar_best_supporting_actress_winners,\n oscar_best_supporting_actor_winners,\n oscar_best_director_nominees,\n best_director_winner,\n oscar_winner,\n oscar_nominee")
            @RequestParam(value = "groups", required = false) String groups,
            @ApiParam("Star Sign\n aquarius, pisces, aries, taurus, gemini, cancer, leo, virgo, libra, scorpio, sagittarius, capricorn")
            @RequestParam(value = "starSign", required = false) String starSign,
            @ApiParam("Ex. Canada")
            @RequestParam(value = "birthPlace", required = false) String birthPlace,
            @ApiParam("Search Names that death date are between two dates \n Format: YYYY-MM-DD, YYYY-MM, or YYYY\n Ex. 1999-01-01,2000-12-31")
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

            reqUrl.append(String.format("gender=%s", gender));

            if (name != null && !name.isEmpty()) {
                reqUrl.append(String.format("&name=%s", name));
            }
            if (birthDate != null && !birthDate.isEmpty()) {
                reqUrl.append(String.format("&birth_date=%s", birthDate));
            }
            if (birthMonthDay != null && !birthMonthDay.isEmpty()) {
                reqUrl.append(String.format("&birth_monthday=%s", birthMonthDay));
            }
            if (groups != null && !groups.isEmpty()) {
                reqUrl.append(String.format("&groups=%s", groups));
            }
            if (starSign != null && !starSign.isEmpty()) {
                reqUrl.append(String.format("&star_sign=%s", starSign));
            }
            if (birthPlace != null && !birthPlace.isEmpty()) {
                reqUrl.append(String.format("&birth_place=%s", birthPlace));
            }
            if (deathDate != null && !deathDate.isEmpty()) {
                reqUrl.append(String.format("&death_date=%s", deathDate));
            }
            if (deathPlace != null && !deathPlace.isEmpty()) {
                reqUrl.append(String.format("&death_place=%s", deathPlace));
            }
            if (roles != null && !roles.isEmpty()) {
                reqUrl.append(String.format("&roles=%s", roles));
            }
            if (bio != null && !bio.isEmpty()) {
                reqUrl.append(String.format("&bio=%s", bio));
            }
            if (sort != null && !sort.isEmpty()) {
                reqUrl.append(String.format("&sort=%s", sort));
            }
            if (start != null && !start.isEmpty()) {
                reqUrl.append(String.format("&start=%s", start));
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
