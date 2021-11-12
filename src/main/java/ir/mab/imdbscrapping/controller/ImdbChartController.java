package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.BoxOffice;
import ir.mab.imdbscrapping.model.MovieSummary;
import ir.mab.imdbscrapping.util.AppConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ir.mab.imdbscrapping.util.Utils.extractTitleId;
import static ir.mab.imdbscrapping.util.Utils.generateImage;

@RestController
@RequestMapping(path = AppConstants.Api.CHART)
public class ImdbChartController {

    @GetMapping("/top250")
    ApiResponse<List<MovieSummary>> fetchTop250Movies() {
        List<MovieSummary> movies = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_TOP_250).get();
            return extractTitles(movies, doc);
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    @GetMapping("/popular")
    ApiResponse<List<MovieSummary>> fetchPopularMovies() {
        List<MovieSummary> movies = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_POPULAR).get();
            return extractTitles(movies, doc);
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    @GetMapping("/tv/popular")
    ApiResponse<List<MovieSummary>> fetchPopularTvMovies() {
        List<MovieSummary> movies = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_POPULAR_TV).get();
            return extractTitles(movies, doc);
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    @GetMapping("/bottom100")
    ApiResponse<List<MovieSummary>> fetchBottom100Movies() {
        List<MovieSummary> movies = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_BOTTOM_100).get();
            return extractTitles(movies, doc);
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    @GetMapping("/tv/top250")
    ApiResponse<List<MovieSummary>> fetchTopTv250Movies() {
        List<MovieSummary> movies = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_TOP_TV_250).get();
            return extractTitles(movies, doc);
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    @GetMapping("/boxoffice")
    ApiResponse<BoxOffice> fetchBoxOffice() {

        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_BOX_OFFICE).get();
            try {
                return new ApiResponse<>(getBoxOffice(doc),null, true);
            }catch (Exception e){
                return new ApiResponse<>(null, e.getMessage(), false);
            }
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    private ApiResponse<List<MovieSummary>> extractTitles(List<MovieSummary> movies, Document doc) {
        try {
            for (Element element : doc.getElementsByClass("lister-list").get(0).getElementsByTag("tr")) {

                try {
                    Element posterColumn = element.getElementsByClass("posterColumn").get(0);
                    Element titleColumn = element.getElementsByClass("titleColumn").get(0);
                    Element watchlistColumn = element.getElementsByClass("watchlistColumn").get(0);

                    MovieSummary movieSummary = new MovieSummary();

                    try {
                        movieSummary.setCover(generateImage(posterColumn.getElementsByTag("a").get(0).getElementsByTag("img").attr("src"), 450, 670));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSummary.setImdbRating(Double.valueOf(Objects.requireNonNull(posterColumn.selectFirst("[name=ir]")).attr("data-value")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSummary.setLink(AppConstants.IMDB_URL + titleColumn.getElementsByTag("a").get(0).attr("href"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSummary.setRank(Integer.valueOf(Objects.requireNonNull(posterColumn.selectFirst("[name=rk]")).attr("data-value")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSummary.setTitle(titleColumn.getElementsByTag("a").get(0).text());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSummary.setNumberOfRating(Long.valueOf(Objects.requireNonNull(posterColumn.selectFirst("[name=nv]")).attr("data-value")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSummary.setYear(titleColumn.getElementsByClass("secondaryInfo").get(0).text());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSummary.setTitleId(watchlistColumn.getElementsByAttribute("data-tconst").get(0).attr("data-tconst"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    movies.add(movieSummary);

                } catch (Exception e) {
                    return new ApiResponse<>(null, e.getMessage(), false);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ApiResponse<>(movies, null, true);
    }

    private BoxOffice getBoxOffice(Document doc) {
        BoxOffice boxOffice = new BoxOffice();
        try {
            boxOffice.setWeekendDate(Objects.requireNonNull(doc.getElementById("boxoffice")).getElementsByTag("h4").text());
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            List<BoxOffice.BoxOfficeTitle> boxOfficeTitles = new ArrayList<>();
            for (Element element: Objects.requireNonNull(doc.getElementById("boxoffice")).getElementsByClass("chart").get(0).getElementsByTag("tbody").get(0).getElementsByTag("tr")){
                BoxOffice.BoxOfficeTitle boxOfficeTitle = new BoxOffice.BoxOfficeTitle();
                try {
                    boxOfficeTitle.setCover(generateImage(element.getElementsByClass("posterColumn").get(0).getElementsByTag("img").attr("src"),180,268));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    boxOfficeTitle.setTitle(Objects.requireNonNull(element.getElementsByClass("titleColumn").get(0).getElementsByTag("a").first()).ownText());
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    boxOfficeTitle.setTitleId(extractTitleId(element.getElementsByClass("titleColumn").get(0).getElementsByTag("a").attr("href")));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    boxOfficeTitle.setWeekend(element.getElementsByClass("ratingColumn").get(0).text());
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    boxOfficeTitle.setGross(element.getElementsByClass("ratingColumn").get(1).text());
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    boxOfficeTitle.setWeeks(element.getElementsByClass("weeksColumn").text());
                }catch (Exception e){
                    e.printStackTrace();
                }

                boxOfficeTitles.add(boxOfficeTitle);
            }
            boxOffice.setBoxOfficeTitles(boxOfficeTitles);
        }catch (Exception e){
            e.printStackTrace();
        }
        return boxOffice;
    }

}
