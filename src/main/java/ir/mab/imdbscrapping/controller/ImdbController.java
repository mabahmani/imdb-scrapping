package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.Home;
import ir.mab.imdbscrapping.util.AppConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@RequestMapping(path = AppConstants.Api.BASE_URL)
public class ImdbController {

    @GetMapping("/home")
    ApiResponse<Home> fetchHome(){
        Home home = new Home();
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ApiResponse<>(home, null, true);
    }
}
