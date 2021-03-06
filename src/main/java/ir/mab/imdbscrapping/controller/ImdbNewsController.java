package ir.mab.imdbscrapping.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.News;
import ir.mab.imdbscrapping.util.AppConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static ir.mab.imdbscrapping.util.Utils.generateImage;

@RestController
@RequestMapping(path = AppConstants.Api.NEWS)
public class ImdbNewsController {

    @GetMapping("/{newsId}")
    @ApiOperation("Details of a news")
    ApiResponse<News> fetchNewsDetails(@ApiParam("Ex. ni63456805") @PathVariable("newsId") String newsId) {
        News news = new News();
        try {
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_NEWS + "%s", newsId)).get();

            try {
                news.setTitle(doc.getElementsByClass("news-article__title").text());
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                news.setLink(doc.getElementsByClass("news-article__title").get(0).getElementsByTag("a").attr("href"));
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                news.setDate(doc.getElementsByClass("news-article__date").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                news.setAuthor(doc.getElementsByClass("news-article__author").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                news.setNewsAgency(doc.getElementsByClass("news-article__source").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                news.setImage(generateImage(doc.getElementsByClass("news-article__image").attr("src"),0,0));
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                news.setNews(doc.getElementsByClass("news-article__content").text());
            }catch (Exception e){
                e.printStackTrace();
            }

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(news, null, true);
    }

}
