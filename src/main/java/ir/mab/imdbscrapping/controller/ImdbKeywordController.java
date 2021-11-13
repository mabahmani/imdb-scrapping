package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
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
@RequestMapping(path = AppConstants.Api.KEYWORD)
public class ImdbKeywordController {

    @GetMapping("/")
    ApiResponse<List<String>> fetchListOfKeywords(){
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_KEYWORD).get();
            List<String> keywords = new ArrayList<>();
            try {
                for (Element element : doc.getElementsByClass("table-row")) {
                    keywords.add(element.text());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return new ApiResponse<>(keywords, null, true);
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

}
