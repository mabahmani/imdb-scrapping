package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.Genre;
import ir.mab.imdbscrapping.model.MovieSearch;
import ir.mab.imdbscrapping.model.NameSearchBirthDay;
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

@RestController
@RequestMapping(path = AppConstants.Api.GENRE)
public class ImdbGenreController {

    @GetMapping("/")
    ApiResponse<List<Genre>> fetchPopularGenres(){
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_URL + "/feature/genre/").get();
            List<Genre> genres = new ArrayList<>();
            try {
                for (Element element : doc.getElementById("main").getElementsByClass("ninja_image")) {
                    if (!element.hasClass("ninja_image_relative_padding")){
                        Genre genre = new Genre();

                        try {
                            genre.setImage(element.getElementsByTag("img").attr("src"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            genre.setGenre(element.getElementsByTag("img").attr("title"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        genres.add(genre);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return new ApiResponse<>(genres, null, true);
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
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
