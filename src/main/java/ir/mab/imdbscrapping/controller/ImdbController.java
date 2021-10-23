package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.MovieSummary;
import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class ImdbController {

    @GetMapping("/movies/top250")
    ApiResponse<List<MovieSummary>> fetchTop250Movies(){
        List<MovieSummary> movies = new ArrayList<>();

        try {
            Document doc = Jsoup.connect("https://www.imdb.com/chart/top/").get();

            for (Element element: doc.getElementsByClass("lister-list").get(0).getElementsByTag("tr")){

                Element posterColumn = element.getElementsByClass("posterColumn").get(0);
                Element titleColumn = element.getElementsByClass("titleColumn").get(0);

                Integer rank = Integer.valueOf(posterColumn.selectFirst("[name=rk]").attr("data-value"));
                Double imdbRating = Double.valueOf(posterColumn.selectFirst("[name=ir]").attr("data-value"));
                Long numberOfRating = Long.valueOf(posterColumn.selectFirst("[name=nv]").attr("data-value"));
                String cover = generateCover(posterColumn.getElementsByTag("a").get(0).getElementsByTag("img").attr("src"),450,670);

                String title = titleColumn.getElementsByTag("a").get(0).text();
                String year = titleColumn.getElementsByClass("secondaryInfo").get(0).text();
                String link = "https://www.imdb.com/" + titleColumn.getElementsByTag("a").get(0).attr("href");

                MovieSummary movieSummary = new MovieSummary();
                movieSummary.setCover(cover);
                movieSummary.setImdbRating(imdbRating);
                movieSummary.setLink(link);
                movieSummary.setRank(rank);
                movieSummary.setTitle(title);
                movieSummary.setNumberOfRating(numberOfRating);
                movieSummary.setYear(year);

                movies.add(movieSummary);

            }
            return new ApiResponse<>(movies, null, true);
        } catch (Exception e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    private String generateCover(String url, int width, int height) {
        String[] coverUrlSplits = url.split("._V1_");
        String baseUrl = coverUrlSplits[0] + "._V1_";
        String options = String.format("UY%s_CR0,0,%s,%s_AL_.jpg",height,width,height);
        return baseUrl + options;
    }
}
