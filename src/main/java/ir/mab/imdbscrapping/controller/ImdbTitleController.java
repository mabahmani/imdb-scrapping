package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.FullCredits;
import ir.mab.imdbscrapping.model.MovieDetails;
import ir.mab.imdbscrapping.model.MovieSummary;
import ir.mab.imdbscrapping.util.AppConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = AppConstants.Api.TITLES)
public class ImdbTitleController {

    private final Pattern namePattern = Pattern.compile("nm+[0-9]+");
    private final Pattern videoPattern = Pattern.compile("vi+[0-9]+");
    private final Pattern titlePattern = Pattern.compile("tt+[0-9]+");

    @GetMapping("/top250")
    ApiResponse<List<MovieSummary>> fetchTop250Movies() {
        List<MovieSummary> movies = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_TOP_250).get();
            return extractTop250(movies, doc);
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    @GetMapping("/toptv250")
    ApiResponse<List<MovieSummary>> fetchTopTv250Movies() {
        List<MovieSummary> movies = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_TOP_TV_250).get();
            return extractTop250(movies, doc);
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    @GetMapping("/{titleId}")
    ApiResponse<MovieDetails> fetchMovie(@PathVariable("titleId") String titleId) {
        MovieDetails movieDetails = new MovieDetails();
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_URL + String.format("/title/%s", titleId)).get();
            movieDetails.setOverview(getMovieOverview(doc));
            movieDetails.setVideos(getMovieVideos(doc));
            movieDetails.setPhotos(getMoviePhotos(doc));
            movieDetails.setTopCasts(getMovieTopCasts(doc));
            movieDetails.setRelatedMovies(getMovieRelated(doc));
            movieDetails.setStoryline(getMovieStoryline(doc));
            movieDetails.setTopReview(getMovieTopReview(doc));
            movieDetails.setDetails(getMovieDetails(doc));
            movieDetails.setBoxOffice(getMovieBoxOffice(doc));
            movieDetails.setTechnicalSpecs(getMovieTechnicalSpecs(doc));
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(movieDetails, null, true);
    }

    @GetMapping("/{titleId}/fullcredits")
    ApiResponse<FullCredits> fetchTitleFullCredits(@PathVariable("titleId") String titleId){
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_URL + String.format("/title/%s/fullcredits", titleId)).get();
            FullCredits fullCredits = new FullCredits();
            try {
                fullCredits.setTitle(doc.getElementsByClass("subpage_title_block").get(0).getElementsByClass("subpage_title_block__right-column").get(0).getElementsByTag("h3").get(0).getElementsByTag("a").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                fullCredits.setYear(doc.getElementsByClass("subpage_title_block").get(0).getElementsByClass("subpage_title_block__right-column").get(0).getElementsByTag("h3").get(0).getElementsByTag("span").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                fullCredits.setCover(generateCover(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("img").attr("src"),0,0));
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                List<FullCredits.Credit> credits = new ArrayList<>();
                for (Element creditElement: doc.getElementById("fullcredits_content").getElementsByTag("h4")){
                    FullCredits.Credit credit = new FullCredits.Credit();
                    try {
                        credit.setTitle(creditElement.text());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        Element tableElement = creditElement.nextElementSibling();
                        try {
                            List<FullCredits.Credit.Item> items = new ArrayList<>();
                            if (tableElement != null && tableElement.hasClass("simpleTable")){
                                for (Element tr: tableElement.getElementsByTag("tr")){
                                    FullCredits.Credit.Item item = new FullCredits.Credit.Item();

                                    try{
                                        item.setTitle(tr.getElementsByClass("name").text());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    try{
                                        item.setId(extractNameId(tr.getElementsByClass("name").get(0).getElementsByTag("a").attr("href")));
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    try{
                                        item.setSubtitle(tr.getElementsByClass("credit").text());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    items.add(item);
                                }
                            }
                            else if (tableElement != null &&  tableElement.hasClass("cast_list")){
                                for (Element tr: tableElement.getElementsByTag("tr")){
                                    FullCredits.Credit.Item item = new FullCredits.Credit.Item();

                                    try{
                                        item.setTitle(tr.getElementsByTag("td").get(1).text());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    try{
                                        item.setId(extractNameId(tr.getElementsByTag("td").get(1).getElementsByTag("a").attr("href")));
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    try{
                                        item.setSubtitle(tr.getElementsByClass("character").text());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    try{
                                        item.setImage(generateCover(tr.getElementsByClass("primary_photo").get(0).getElementsByTag("img").attr("loadLate"),0,0));
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    items.add(item);
                                }

                            }
                            credit.setItems(items);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    credits.add(credit);
                }
                fullCredits.setCredits(credits);
            }catch (Exception e){
                e.printStackTrace();
            }
            return new ApiResponse<>(fullCredits,null,true);
        }catch (IOException ioException){
            return new ApiResponse<>(null,ioException.getMessage(),false);
        }
    }

    private ApiResponse<List<MovieSummary>> extractTop250(List<MovieSummary> movies, Document doc) {
        try {
            for (Element element : doc.getElementsByClass("lister-list").get(0).getElementsByTag("tr")) {

                try {
                    Element posterColumn = element.getElementsByClass("posterColumn").get(0);
                    Element titleColumn = element.getElementsByClass("titleColumn").get(0);
                    Element watchlistColumn = element.getElementsByClass("watchlistColumn").get(0);

                    MovieSummary movieSummary = new MovieSummary();

                    try {
                        movieSummary.setCover(generateCover(posterColumn.getElementsByTag("a").get(0).getElementsByTag("img").attr("src"), 450, 670));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSummary.setImdbRating(Double.valueOf(posterColumn.selectFirst("[name=ir]").attr("data-value")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSummary.setLink(AppConstants.IMDB_URL + titleColumn.getElementsByTag("a").get(0).attr("href"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSummary.setRank(Integer.valueOf(posterColumn.selectFirst("[name=rk]").attr("data-value")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSummary.setTitle(titleColumn.getElementsByTag("a").get(0).text());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        movieSummary.setNumberOfRating(Long.valueOf(posterColumn.selectFirst("[name=nv]").attr("data-value")));
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

    private List<MovieDetails.TechnicalSpecs> getMovieTechnicalSpecs(Document doc) {

        List<MovieDetails.TechnicalSpecs> technicalSpecs = new ArrayList<>();

        try {
            for (Element element : doc.getElementsByAttributeValue("data-testid", "title-techspecs-section").get(0).getElementsByTag("ul").get(0).getElementsByClass("ipc-metadata-list__item")) {
                try {
                    MovieDetails.TechnicalSpecs technicalSpecsItem = new MovieDetails.TechnicalSpecs();
                    technicalSpecsItem.setTitle(element.getElementsByClass("ipc-metadata-list-item__label").text());
                    technicalSpecsItem.setSubtitle(element.getElementsByClass("ipc-metadata-list-item__list-content-item").text());
                    technicalSpecs.add(technicalSpecsItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return technicalSpecs;
    }

    private MovieDetails.BoxOffice getMovieBoxOffice(Document doc) {

        MovieDetails.BoxOffice boxOffice = new MovieDetails.BoxOffice();
        try {
            boxOffice.setBudget(doc.getElementsByAttributeValue("data-testid", "title-boxoffice-budget").get(0).getElementsByClass("ipc-metadata-list-item__list-content-item").text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            boxOffice.setGrossUsAndCanada(doc.getElementsByAttributeValue("data-testid", "title-boxoffice-grossdomestic").get(0).getElementsByClass("ipc-metadata-list-item__list-content-item").text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            boxOffice.setOpeningWeekendUsAndCanada(doc.getElementsByAttributeValue("data-testid", "title-boxoffice-openingweekenddomestic").get(0).getElementsByClass("ipc-metadata-list-item__list-content-item").text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            boxOffice.setGrossWorldwide(doc.getElementsByAttributeValue("data-testid", "title-boxoffice-cumulativeworldwidegross").get(0).getElementsByClass("ipc-metadata-list-item__list-content-item").text());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return boxOffice;
    }

    private MovieDetails.Details getMovieDetails(Document doc) {

        MovieDetails.Details details = new MovieDetails.Details();
        try {
            details.setReleaseDate(extractDetails(doc.getElementsByAttributeValue("data-testid", "title-details-releasedate").get(0)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            details.setCountryOfOrigin(extractDetails(doc.getElementsByAttributeValue("data-testid", "title-details-origin").get(0)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            details.setOfficialSites(extractDetails(doc.getElementsByAttributeValue("data-testid", "title-details-officialsites").get(0)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            details.setLanguage(extractDetails(doc.getElementsByAttributeValue("data-testid", "title-details-languages").get(0)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            details.setFilmingLocations(extractDetails(doc.getElementsByAttributeValue("data-testid", "title-details-filminglocations").get(0)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            details.setProductionCompanies(extractDetails(doc.getElementsByAttributeValue("data-testid", "title-details-companies").get(0)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return details;

    }

    private MovieDetails.Review getMovieTopReview(Document doc) {

        MovieDetails.Review review = new MovieDetails.Review();
        try {
            review.setTitle(doc.getElementsByAttributeValue("data-testid", "review-summary").text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            review.setReview(doc.getElementsByAttributeValue("data-testid", "review-overflow").text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            review.setRating(doc.getElementsByAttributeValue("data-testid", "review-featured-header").get(0).getElementsByClass("ipc-rating-star").text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            review.setDate(doc.getElementsByClass("ipc-inline-list__item review-date").get(0).text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            review.setUsername(doc.getElementsByAttributeValue("data-testid", "author-link").get(0).text());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return review;
    }

    private MovieDetails.Storyline getMovieStoryline(Document doc) {

        MovieDetails.Storyline storyline = new MovieDetails.Storyline();
        try {
            storyline.setStory(doc.getElementsByAttributeValue("data-testid", "storyline-plot-summary").text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<MovieDetails.Keyword> keywords = new ArrayList<>();
            for (Element element : doc.getElementsByAttributeValue("data-testid", "storyline-plot-keywords").get(0).getElementsByClass("ipc-chip")) {
                try {
                    MovieDetails.Keyword keyword = new MovieDetails.Keyword();
                    keyword.setTitle(element.text());
                    keyword.setLink(AppConstants.IMDB_URL + element.attr("href"));
                    keywords.add(keyword);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            storyline.setKeywords(keywords);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            storyline.setTaglines(doc.getElementsByAttributeValue("data-testid", "storyline-taglines").get(0).getElementsByTag("ul").text());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            List<MovieDetails.Genre> genres = new ArrayList<>();
            for (Element element : doc.getElementsByAttributeValue("data-testid", "storyline-genres").get(0).getElementsByTag("a")) {
                try {
                    MovieDetails.Genre genre = new MovieDetails.Genre();
                    genre.setTitle(element.text());
                    genre.setLink(AppConstants.IMDB_URL + element.attr("href"));
                    genres.add(genre);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            storyline.setGenres(genres);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            MovieDetails.LinkTitle motionPictureRatingLinkTitle = new MovieDetails.LinkTitle();
            motionPictureRatingLinkTitle.setLink(AppConstants.IMDB_URL + doc.getElementsByAttributeValue("data-testid", "storyline-certificate").get(0).getElementsByTag("a").attr("href"));
            motionPictureRatingLinkTitle.setTitle(doc.getElementsByAttributeValue("data-testid", "storyline-certificate").get(0).getElementsByClass("ipc-metadata-list-item__list-content-item").text());
            storyline.setMotionPictureRating(motionPictureRatingLinkTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            MovieDetails.LinkTitle parentsGuidLinkTitle = new MovieDetails.LinkTitle();
            parentsGuidLinkTitle.setLink(AppConstants.IMDB_URL + doc.getElementsByAttributeValue("data-testid", "storyline-parents-guide").get(0).getElementsByTag("a").attr("href"));
            storyline.setParentsGuide(parentsGuidLinkTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return storyline;
    }

    private List<MovieDetails.RelatedMovie> getMovieRelated(Document doc) {
        List<MovieDetails.RelatedMovie> relatedMovies = new ArrayList<>();

        try {
            for (Element element : doc.getElementsByClass("ipc-poster-card")) {
                try {
                    MovieDetails.RelatedMovie relatedMovie = new MovieDetails.RelatedMovie();
                    relatedMovie.setCover(generateCover(element.getElementsByTag("img").attr("src"), 430, 621));
                    relatedMovie.setRate(element.getElementsByClass("ipc-rating-star").text());
                    relatedMovie.setTitle(element.getElementsByAttributeValue("data-testid", "title").text());
                    relatedMovie.setLink(AppConstants.IMDB_URL + element.getElementsByClass("ipc-poster-card__title").attr("href"));
                    relatedMovie.setId(extractTitleId(element.getElementsByClass("ipc-poster-card__title").attr("href")));

                    relatedMovies.add(relatedMovie);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return relatedMovies;
    }

    private List<MovieDetails.Person> getMovieTopCasts(Document doc) {

        List<MovieDetails.Person> topCasts = new ArrayList<>();

        try {
            for (Element element : doc.getElementsByAttributeValue("data-testid", "title-cast-item")) {
                try {
                    MovieDetails.Person person = new MovieDetails.Person();
                    person.setImage(generateCover(element.getElementsByTag("img").attr("src"), 0, 0));
                    person.setId(extractNameId(element.getElementsByAttributeValue("data-testid", "title-cast-item__actor").attr("href")));
                    person.setLink(AppConstants.IMDB_URL + element.getElementsByAttributeValue("data-testid", "title-cast-item__actor").attr("href"));
                    person.setRealName(element.getElementsByAttributeValue("data-testid", "title-cast-item__actor").text());
                    person.setMovieName(element.getElementsByAttributeValue("data-testid", "cast-item-characters-link").get(0).getElementsByTag("span").first().text());

                    topCasts.add(person);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return topCasts;
    }

    private List<MovieDetails.Photo> getMoviePhotos(Document doc) {

        List<MovieDetails.Photo> photos = new ArrayList<>();

        try {
            for (Element element : doc.getElementsByClass("ipc-photo")) {
                try {
                    MovieDetails.Photo photo = new MovieDetails.Photo();
                    photo.setOriginal(generateCover(element.getElementsByTag("img").attr("src"), 0, 0));
                    photo.setThumbnail(generateCover(element.getElementsByTag("img").attr("src"), 512, 512));
                    photos.add(photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photos;
    }

    private List<MovieDetails.Video> getMovieVideos(Document doc) {
        List<MovieDetails.Video> videos = new ArrayList<>();

        try {
            for (Element element : doc.getElementsByClass("ipc-shoveler").get(0).getElementsByClass("ipc-slate-card")) {
                try {
                    MovieDetails.Video video = new MovieDetails.Video();
                    video.setDuration(element.getElementsByClass("ipc-lockup-overlay__text").text());
                    video.setPreview(generateCover(element.getElementsByTag("img").attr("src"), 0, 0));
                    video.setLink(AppConstants.IMDB_URL + element.getElementsByClass("ipc-slate-card__title").attr("href"));
                    video.setTitle(element.getElementsByClass("ipc-slate-card__title-text").text());
                    video.setId(extractVideoId(video.getLink()));
                    videos.add(video);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return videos;
    }

    private MovieDetails.Overview getMovieOverview(Document doc) {
        MovieDetails.Overview overview = new MovieDetails.Overview();

        try {
            overview.setTitle(doc.getElementsByAttributeValue("data-testid", "hero-title-block__title").get(0).text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Elements sMetaData = doc.getElementsByAttributeValue("data-testid", "hero-title-block__metadata").get(0).getElementsByTag("li");
            if (sMetaData.size() > 3){
                try {
                    overview.setReleaseYear(sMetaData.get(1).getElementsByTag("a").get(0).text());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    overview.setParentalGuidCertificate(sMetaData.get(2).getElementsByTag("a").get(0).text());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    overview.setRuntime(sMetaData.get(3).text());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    overview.setReleaseYear(sMetaData.get(0).getElementsByTag("a").get(0).text());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    overview.setParentalGuidCertificate(sMetaData.get(1).getElementsByTag("a").get(0).text());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    overview.setRuntime(sMetaData.get(2).text());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            overview.setCover(generateCover(doc.getElementsByAttributeValue("data-testid", "hero-media__poster").get(0).getElementsByTag("img").get(0).attr("src"), 0, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            overview.setTrailerPreview(generateCover(doc.getElementsByAttributeValue("data-testid", "hero-media__slate").get(0).getElementsByTag("img").get(0).attr("src"), 0, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            overview.setTrailerDuration(doc.getElementsByAttributeValue("data-testid", "hero-media__slate-overlay-text").get(0).parent().getElementsByTag("span").get(1).text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<MovieDetails.Genre> sGenres = new ArrayList<>();
            try {
                Elements sGenreElements = doc.getElementsByAttributeValue("data-testid", "genres").get(0).getElementsByTag("a");
                for (Element element : sGenreElements) {
                    try {
                        MovieDetails.Genre genre = new MovieDetails.Genre();
                        genre.setTitle(element.getElementsByTag("span").get(0).text());
                        genre.setLink(AppConstants.IMDB_URL + element.attr("href"));
                        sGenres.add(genre);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                overview.setGenres(sGenres);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            overview.setPlot(doc.getElementsByAttributeValue("data-testid", "plot").get(0).getAllElements().get(0).text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            overview.setImdbRating(doc.getElementsByAttributeValue("data-testid", "hero-rating-bar__aggregate-rating__score").get(0).getAllElements().get(0).text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            overview.setNumberOfRate(doc.getElementsByAttributeValue("data-testid", "hero-rating-bar__aggregate-rating__score").get(0).parent().getAllElements().get(5).getAllElements().get(0).text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Elements credits = doc.getElementsByAttributeValue("data-testid", "title-pc-principal-credit");

            for (Element element: credits){
                if (element.getElementsByClass("ipc-metadata-list-item__label").text().contains("Director")){
                    try {
                        Elements sDirectorsElements = element.getAllElements().get(2).getElementsByTag("a");
                        List<MovieDetails.Person> sDirectors = new ArrayList<>();
                        extractOverviewPersons(sDirectorsElements, sDirectors);
                        overview.setDirectors(sDirectors);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else if (element.getElementsByClass("ipc-metadata-list-item__label").text().contains("Writer")){
                    try {
                        Elements sWritersElements = element.getAllElements().get(2).getElementsByTag("a");
                        List<MovieDetails.Person> sWriters = new ArrayList<>();
                        extractOverviewPersons(sWritersElements, sWriters);
                        overview.setWriters(sWriters);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (element.getElementsByClass("ipc-metadata-list-item__label").text().contains("Star")){
                    try {
                        Elements sStarsElements = element.getAllElements().get(2).getElementsByTag("a");
                        List<MovieDetails.Person> sStars = new ArrayList<>();
                        extractOverviewPersons(sStarsElements, sStars);
                        overview.setStars(sStars);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return overview;
    }

    private List<MovieDetails.LinkTitle> extractDetails(Element e) {
        List<MovieDetails.LinkTitle> linkTitles = new ArrayList<>();
        if (e.getElementsByTag("ul").isEmpty()) {
            for (Element element : e.getElementsByTag("li")) {
                MovieDetails.LinkTitle linkTitle = new MovieDetails.LinkTitle();
                linkTitle.setTitle(element.getElementsByTag("a").text());
                String link = element.getElementsByTag("a").attr("href");
                if (link.startsWith("http"))
                    linkTitle.setLink(element.getElementsByTag("a").attr("href"));
                else
                    linkTitle.setLink(AppConstants.IMDB_URL + element.getElementsByTag("a").attr("href"));
                linkTitles.add(linkTitle);
            }
        } else {
            for (Element element : e.getElementsByTag("ul").get(0).getElementsByTag("li")) {
                MovieDetails.LinkTitle linkTitle = new MovieDetails.LinkTitle();
                linkTitle.setTitle(element.getElementsByTag("a").text());
                String link = element.getElementsByTag("a").attr("href");
                if (link.startsWith("http"))
                    linkTitle.setLink(element.getElementsByTag("a").attr("href"));
                else
                    linkTitle.setLink(AppConstants.IMDB_URL + element.getElementsByTag("a").attr("href"));
                linkTitles.add(linkTitle);
            }
        }


        return linkTitles;
    }

    private void extractOverviewPersons(Elements elements, List<MovieDetails.Person> persons) {
        for (Element element : elements) {
            try {
                MovieDetails.Person person = new MovieDetails.Person();
                person.setRealName(element.text());
                person.setLink(AppConstants.IMDB_URL + element.attr("href"));
                person.setId(extractNameId(person.getLink()));
                persons.add(person);
            } catch (Exception e) {
                e.printStackTrace();
            }

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

    private String extractVideoId(String text) {
        Matcher m = videoPattern.matcher(text);
        if (m.find())
            return m.group();

        return null;
    }
}
