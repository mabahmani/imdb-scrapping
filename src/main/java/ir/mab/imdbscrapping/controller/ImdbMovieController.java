package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
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
@RequestMapping(path = AppConstants.Api.MOVIES)
public class ImdbMovieController {

    private final Pattern namePattern = Pattern.compile("nm+[0-9]+");
    private final Pattern videoPattern = Pattern.compile("vi+[0-9]+");
    private final Pattern titlePattern = Pattern.compile("tt+[0-9]+");

    @GetMapping("/top250")
    ApiResponse<List<MovieSummary>> fetchTop250Movies() {
        List<MovieSummary> movies = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_TOP_250).get();

            for (Element element : doc.getElementsByClass("lister-list").get(0).getElementsByTag("tr")) {

                Element posterColumn = element.getElementsByClass("posterColumn").get(0);
                Element titleColumn = element.getElementsByClass("titleColumn").get(0);
                Element watchlistColumn = element.getElementsByClass("watchlistColumn").get(0);

                Integer rank = Integer.valueOf(posterColumn.selectFirst("[name=rk]").attr("data-value"));
                Double imdbRating = Double.valueOf(posterColumn.selectFirst("[name=ir]").attr("data-value"));
                Long numberOfRating = Long.valueOf(posterColumn.selectFirst("[name=nv]").attr("data-value"));
                String cover = generateCover(posterColumn.getElementsByTag("a").get(0).getElementsByTag("img").attr("src"), 450, 670);

                String title = titleColumn.getElementsByTag("a").get(0).text();
                String year = titleColumn.getElementsByClass("secondaryInfo").get(0).text();
                String link = AppConstants.IMDB_URL + titleColumn.getElementsByTag("a").get(0).attr("href");

                String titleId = watchlistColumn.getElementsByAttribute("data-tconst").get(0).attr("data-tconst");


                MovieSummary movieSummary = new MovieSummary();
                movieSummary.setCover(cover);
                movieSummary.setImdbRating(imdbRating);
                movieSummary.setLink(link);
                movieSummary.setRank(rank);
                movieSummary.setTitle(title);
                movieSummary.setNumberOfRating(numberOfRating);
                movieSummary.setYear(year);
                movieSummary.setTitleId(titleId);

                movies.add(movieSummary);

            }
            return new ApiResponse<>(movies, null, true);
        } catch (Exception e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    @GetMapping("/{titleId}")
    ApiResponse<MovieDetails> getMovieDetails(@PathVariable("titleId") String titleId) {
        MovieDetails movieDetails = new MovieDetails();
        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_URL + String.format("/title/%s", titleId)).get();
            MovieDetails.Overview overview = new MovieDetails.Overview();
            overview.setTitle(doc.getElementsByAttributeValue("data-testid", "hero-title-block__title").get(0).text());
            Elements sMetaData = doc.getElementsByAttributeValue("data-testid", "hero-title-block__metadata").get(0).getElementsByTag("li");
            overview.setReleaseYear(sMetaData.get(0).getElementsByTag("a").get(0).text());
            overview.setParentalGuidCertificate(sMetaData.get(1).getElementsByTag("a").get(0).text());
            overview.setRuntime(sMetaData.get(2).text());
            overview.setCover(generateCover(doc.getElementsByAttributeValue("data-testid", "hero-media__poster").get(0).getElementsByTag("img").get(0).attr("src"), 0, 0));
            overview.setTrailerPreview(generateCover(doc.getElementsByAttributeValue("data-testid", "hero-media__slate").get(0).getElementsByTag("img").get(0).attr("src"), 0, 0));
            overview.setTrailerDuration(doc.getElementsByAttributeValue("data-testid", "hero-media__slate-overlay-text").get(0).parent().getElementsByTag("span").get(1).text());
            List<MovieDetails.Genre> sGenres = new ArrayList<>();
            Elements sGenreElements = doc.getElementsByAttributeValue("data-testid", "genres").get(0).getElementsByTag("a");
            for (Element element : sGenreElements) {
                MovieDetails.Genre genre = new MovieDetails.Genre();
                genre.setTitle(element.getElementsByTag("span").get(0).text());
                genre.setLink(AppConstants.IMDB_URL + element.attr("href"));
                sGenres.add(genre);
            }
            overview.setGenres(sGenres);
            overview.setPlot(doc.getElementsByAttributeValue("data-testid", "plot").get(0).getAllElements().get(0).text());
            overview.setImdbRating(doc.getElementsByAttributeValue("data-testid", "hero-rating-bar__aggregate-rating__score").get(0).getAllElements().get(0).text());
            overview.setNumberOfRate(doc.getElementsByAttributeValue("data-testid", "hero-rating-bar__aggregate-rating__score").get(0).parent().getAllElements().get(5).getAllElements().get(0).text());
            Elements sDirectorsElements = doc.getElementsByAttributeValue("data-testid", "title-pc-principal-credit").get(0).getAllElements().get(2).getElementsByTag("a");
            Elements sWritersElements = doc.getElementsByAttributeValue("data-testid", "title-pc-principal-credit").get(1).getAllElements().get(2).getElementsByTag("a");
            Elements sStarsElements = doc.getElementsByAttributeValue("data-testid", "title-pc-principal-credit").get(2).getAllElements().get(2).getElementsByTag("a");
            List<MovieDetails.Person> sDirectors = new ArrayList<>();
            List<MovieDetails.Person> sWriters = new ArrayList<>();
            List<MovieDetails.Person> sStars = new ArrayList<>();

            extractOverviewPersons(sDirectorsElements, sDirectors);
            extractOverviewPersons(sWritersElements, sWriters);
            extractOverviewPersons(sStarsElements, sStars);

            overview.setDirectors(sDirectors);
            overview.setWriters(sWriters);
            overview.setStars(sStars);

            movieDetails.setOverview(overview);

            List<MovieDetails.Video> videos = new ArrayList<>();
            for (Element element : doc.getElementsByClass("ipc-shoveler").get(0).getElementsByClass("ipc-slate-card")){

                MovieDetails.Video video = new MovieDetails.Video();
                video.setDuration(element.getElementsByClass("ipc-lockup-overlay__text").text());
                video.setPreview(generateCover(element.getElementsByTag("img").attr("src"),0,0));
                video.setLink(AppConstants.IMDB_URL + element.getElementsByClass("ipc-slate-card__title").attr("href"));
                video.setTitle(element.getElementsByClass("ipc-slate-card__title-text").text());
                video.setId(extractVideoId(video.getLink()));

                videos.add(video);
            }

            movieDetails.setVideos(videos);

            List<MovieDetails.Photo> photos = new ArrayList<>();

            for (Element element: doc.getElementsByClass("ipc-photo")){
                MovieDetails.Photo photo = new MovieDetails.Photo();
                photo.setOriginal(generateCover(element.getElementsByTag("img").attr("src"),0,0));
                photo.setThumbnail(generateCover(element.getElementsByTag("img").attr("src"),512,512));
                photos.add(photo);
            }

            movieDetails.setPhotos(photos);

            List<MovieDetails.Person> topCasts = new ArrayList<>();

            for (Element element: doc.getElementsByAttributeValue("data-testid","title-cast-item")){
                MovieDetails.Person person = new MovieDetails.Person();
                person.setImage(generateCover(element.getElementsByTag("img").attr("src"),0,0));
                person.setId(extractNameId(element.getElementsByAttributeValue("data-testid","title-cast-item__actor").attr("href")));
                person.setLink(AppConstants.IMDB_URL + element.getElementsByAttributeValue("data-testid","title-cast-item__actor").attr("href"));
                person.setRealName(element.getElementsByAttributeValue("data-testid","title-cast-item__actor").text());
                person.setMovieName(element.getElementsByAttributeValue("data-testid","cast-item-characters-link").get(0).getElementsByTag("span").first().text());

                topCasts.add(person);
            }

            movieDetails.setTopCasts(topCasts);


            List<MovieDetails.RelatedMovie> relatedMovies = new ArrayList<>();

            for (Element element: doc.getElementsByClass("ipc-poster-card")){
                MovieDetails.RelatedMovie relatedMovie = new MovieDetails.RelatedMovie();
                relatedMovie.setCover(generateCover(element.getElementsByTag("img").attr("src"),430,621));
                relatedMovie.setRate(element.getElementsByClass("ipc-rating-star").text());
                relatedMovie.setTitle(element.getElementsByAttributeValue("data-testid","title").text());
                relatedMovie.setLink(AppConstants.IMDB_URL + element.getElementsByClass("ipc-poster-card__title").attr("href"));
                relatedMovie.setId(extractTitleId(element.getElementsByClass("ipc-poster-card__title").attr("href")));

                relatedMovies.add(relatedMovie);
            }

            movieDetails.setRelatedMovies(relatedMovies);

            MovieDetails.Storyline storyline = new MovieDetails.Storyline();
            storyline.setStory(doc.getElementsByAttributeValue("data-testid","storyline-plot-summary").text());
            List<MovieDetails.Keyword> keywords = new ArrayList<>();
            for (Element element: doc.getElementsByAttributeValue("data-testid","storyline-plot-keywords").get(0).getElementsByClass("ipc-chip")){
                MovieDetails.Keyword keyword = new MovieDetails.Keyword();
                keyword.setTitle(element.text());
                keyword.setLink(AppConstants.IMDB_URL + element.attr("href"));
                keywords.add(keyword);
            }
            storyline.setKeywords(keywords);
            storyline.setTaglines(doc.getElementsByAttributeValue("data-testid","storyline-taglines").get(0).getElementsByTag("ul").text());

            List<MovieDetails.Genre> genres = new ArrayList<>();
            for (Element element: doc.getElementsByAttributeValue("data-testid","storyline-genres").get(0).getElementsByTag("a")){
                MovieDetails.Genre genre = new MovieDetails.Genre();
                genre.setTitle(element.text());
                genre.setLink(AppConstants.IMDB_URL + element.attr("href"));
                genres.add(genre);
            }
            storyline.setGenres(genres);

            MovieDetails.LinkTitle motionPictureRatingLinkTitle = new MovieDetails.LinkTitle();
            motionPictureRatingLinkTitle.setLink(AppConstants.IMDB_URL + doc.getElementsByAttributeValue("data-testid","storyline-certificate").get(0).getElementsByTag("a").attr("href"));
            motionPictureRatingLinkTitle.setTitle(doc.getElementsByAttributeValue("data-testid","storyline-certificate").get(0).getElementsByClass("ipc-metadata-list-item__list-content-item").text());
            storyline.setMotionPictureRating(motionPictureRatingLinkTitle);

            MovieDetails.LinkTitle parentsGuidLinkTitle = new MovieDetails.LinkTitle();
            parentsGuidLinkTitle.setLink(AppConstants.IMDB_URL + doc.getElementsByAttributeValue("data-testid","storyline-parents-guide").get(0).getElementsByTag("a").attr("href"));
            storyline.setParentsGuide(parentsGuidLinkTitle);

            movieDetails.setStoryline(storyline);


            MovieDetails.Review review = new MovieDetails.Review();
            review.setTitle(doc.getElementsByAttributeValue("data-testid","review-summary").text());
            review.setReview(doc.getElementsByAttributeValue("data-testid","review-overflow").text());
            review.setRating(doc.getElementsByAttributeValue("data-testid","review-featured-header").get(0).getElementsByClass("ipc-rating-star").text());
            review.setDate(doc.getElementsByClass("ipc-inline-list__item review-date").get(0).text());
            review.setUsername(doc.getElementsByAttributeValue("data-testid","author-link").get(0).text());

            movieDetails.setTopReview(review);

            MovieDetails.Details details = new MovieDetails.Details();
            details.setReleaseDate(extractDetails(doc.getElementsByAttributeValue("data-testid","title-details-releasedate").get(0)));
            details.setCountryOfOrigin(extractDetails(doc.getElementsByAttributeValue("data-testid","title-details-origin").get(0)));
            details.setOfficialSites(extractDetails(doc.getElementsByAttributeValue("data-testid","title-details-officialsites").get(0)));
            details.setLanguage(extractDetails(doc.getElementsByAttributeValue("data-testid","title-details-languages").get(0)));
            details.setFilmingLocations(extractDetails(doc.getElementsByAttributeValue("data-testid","title-details-filminglocations").get(0)));
            details.setProductionCompanies(extractDetails(doc.getElementsByAttributeValue("data-testid","title-details-companies").get(0)));

            movieDetails.setDetails(details);

            MovieDetails.BoxOffice boxOffice =  new MovieDetails.BoxOffice();
            boxOffice.setBudget(doc.getElementsByAttributeValue("data-testid","title-boxoffice-budget").get(0).getElementsByClass("ipc-metadata-list-item__list-content-item").text());
            boxOffice.setGrossUsAndCanada(doc.getElementsByAttributeValue("data-testid","title-boxoffice-grossdomestic").get(0).getElementsByClass("ipc-metadata-list-item__list-content-item").text());
            boxOffice.setOpeningWeekendUsAndCanada(doc.getElementsByAttributeValue("data-testid","title-boxoffice-openingweekenddomestic").get(0).getElementsByClass("ipc-metadata-list-item__list-content-item").text());
            boxOffice.setGrossWorldwide(doc.getElementsByAttributeValue("data-testid","title-boxoffice-cumulativeworldwidegross").get(0).getElementsByClass("ipc-metadata-list-item__list-content-item").text());

            movieDetails.setBoxOffice(boxOffice);

            List<MovieDetails.TechnicalSpecs> technicalSpecs = new ArrayList<>();

            for (Element element: doc.getElementsByAttributeValue("data-testid","title-techspecs-section").get(0).getElementsByTag("ul").get(0).getElementsByClass("ipc-metadata-list__item")){
                MovieDetails.TechnicalSpecs technicalSpecsItem = new MovieDetails.TechnicalSpecs();
                technicalSpecsItem.setTitle(element.getElementsByClass("ipc-metadata-list-item__label").text());
                technicalSpecsItem.setSubtitle(element.getElementsByClass("ipc-metadata-list-item__list-content-item").text());
                technicalSpecs.add(technicalSpecsItem);
            }

            movieDetails.setTechnicalSpecs(technicalSpecs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ApiResponse<>(movieDetails, null, true);
    }

    private List<MovieDetails.LinkTitle> extractDetails(Element e) {
        List<MovieDetails.LinkTitle> linkTitles = new ArrayList<>();
        if (e.getElementsByTag("ul").isEmpty()){
            for (Element element: e.getElementsByTag("li")){
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
        else {
            for (Element element: e.getElementsByTag("ul").get(0).getElementsByTag("li")){
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
            MovieDetails.Person person = new MovieDetails.Person();
            person.setRealName(element.text());
            person.setLink(AppConstants.IMDB_URL + element.attr("href"));
            person.setId(extractNameId(person.getLink()));
            persons.add(person);
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
        String options = String.format("UY%s_CR%s,0,%s,%s_AL_.jpg", height,0, 0, 0);
        return baseUrl + options;
    }

    private String extractNameId(String text){
        Matcher m = namePattern.matcher(text);
        if (m.find())
            return m.group();

        return null;
    }

    private String extractTitleId(String text){
        Matcher m = titlePattern.matcher(text);
        if (m.find())
            return m.group();

        return null;
    }

    private String extractVideoId(String text){
        Matcher m = videoPattern.matcher(text);
        if (m.find())
            return m.group();

        return null;
    }
}
