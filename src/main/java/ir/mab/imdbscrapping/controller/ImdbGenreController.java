package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.Genre;
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

@RestController
@RequestMapping(path = AppConstants.Api.GENRE)
public class ImdbGenreController {

    @GetMapping("/")
    ApiResponse<List<Genre>> fetchPopularGenres(){
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_GENRE).get();
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

}
