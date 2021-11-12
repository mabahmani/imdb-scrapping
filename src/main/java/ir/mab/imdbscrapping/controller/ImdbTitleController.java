package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.*;
import ir.mab.imdbscrapping.util.AppConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.*;

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
    private final Pattern eventPattern = Pattern.compile("ev+[0-9]+");

    @GetMapping("/calender")
    ApiResponse<List<Calender>> fetchCalender() {

        try {
            Document doc = Jsoup.connect(AppConstants.IMDB_CALENDER).get();
            try {
                return new ApiResponse<>(getCalender(doc),null, true);
            }catch (Exception e){
                return new ApiResponse<>(null, e.getMessage(), false);
            }
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    @GetMapping("/comingsoon")
    ApiResponse<List<MovieComingSoon>> fetchComingSoonTitles(@RequestParam(value = "yearmonth", required = false, defaultValue = "") String yearmonth) {

        try {
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_COMING_SOON + "%s", yearmonth)).get();
            try {
                return new ApiResponse<>(getComingSoonTitles(doc),null, true);
            }catch (Exception e){
                return new ApiResponse<>(null, e.getMessage(), false);
            }
        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }
    }

    @GetMapping("/{titleId}")
    ApiResponse<MovieDetails> fetchMovie(@PathVariable("titleId") String titleId) {
        MovieDetails movieDetails = new MovieDetails();
        try {
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_TITLE + "%s", titleId)).get();
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
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_TITLE + "%s/fullcredits", titleId)).get();
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

    @GetMapping("/{titleId}/technical")
    ApiResponse<TechnicalSpecifications> fetchTechnicalSpecs(@PathVariable("titleId") String titleId){
        try {
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_TITLE + "%s/technical", titleId)).get();
            TechnicalSpecifications technicalSpecifications = new TechnicalSpecifications();
            try {
                technicalSpecifications.setTitle(doc.getElementsByClass("subpage_title_block").get(0).getElementsByClass("subpage_title_block__right-column").get(0).getElementsByTag("h3").get(0).getElementsByTag("a").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                technicalSpecifications.setYear(doc.getElementsByClass("subpage_title_block").get(0).getElementsByClass("subpage_title_block__right-column").get(0).getElementsByTag("h3").get(0).getElementsByTag("span").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                technicalSpecifications.setCover(generateCover(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("img").attr("src"),0,0));
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                List<TechnicalSpecifications.Spec> specs = new ArrayList<>();
                for (Element specElement: doc.getElementById("technical_content").getElementsByClass("dataTable").get(0).getElementsByTag("tr")){
                    TechnicalSpecifications.Spec spec = new TechnicalSpecifications.Spec();
                    try {
                        spec.setTitle(specElement.getElementsByTag("td").get(0).text());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        spec.setSubtitle(specElement.getElementsByTag("td").get(1).text());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    specs.add(spec);
                }
                technicalSpecifications.setSpecs(specs);
            }catch (Exception e){
                e.printStackTrace();
            }
            return new ApiResponse<>(technicalSpecifications,null,true);
        }catch (IOException ioException){
            return new ApiResponse<>(null,ioException.getMessage(),false);
        }
    }

    @GetMapping("/{titleId}/faqs")
    ApiResponse<Faqs> fetchFaqs(@PathVariable("titleId") String titleId){
        try {
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_TITLE + "%s/faq", titleId)).get();
            Faqs faqs = new Faqs();
            try {
                faqs.setTitle(doc.getElementsByClass("subpage_title_block").get(0).getElementsByClass("subpage_title_block__right-column").get(0).getElementsByTag("h3").get(0).getElementsByTag("a").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                faqs.setYear(doc.getElementsByClass("subpage_title_block").get(0).getElementsByClass("subpage_title_block__right-column").get(0).getElementsByTag("h3").get(0).getElementsByTag("span").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                faqs.setCover(generateCover(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("img").attr("src"),0,0));
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                List<Faqs.Faq> faqList = new ArrayList<>();
                for (Element faqElement: doc.getElementById("faq-no-spoilers").getElementsByTag("li")){
                    Faqs.Faq faq = new Faqs.Faq();
                    try {
                        faq.setQuestion(faqElement.getElementsByClass("faq-question").text());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        faq.setAnswer(faqElement.getElementsByClass("ipl-hideable-container").get(0).getElementsByTag("p").first().ownText());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    faqList.add(faq);
                }
                faqs.setFaqsNoSpoiler(faqList);
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                List<Faqs.Faq> faqList = new ArrayList<>();
                for (Element faqElement: doc.getElementById("faq-spoilers").getElementsByTag("li")){
                    Faqs.Faq faq = new Faqs.Faq();
                    try {
                        faq.setQuestion(faqElement.getElementsByClass("faq-question").text());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        faq.setAnswer(faqElement.getElementsByClass("ipl-hideable-container").get(0).getElementsByTag("p").first().ownText());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    faqList.add(faq);
                }
                faqs.setFaqsSpoiler(faqList);
            }catch (Exception e){
                e.printStackTrace();
            }

            return new ApiResponse<>(faqs,null,true);
        }catch (IOException ioException){
            return new ApiResponse<>(null,ioException.getMessage(),false);
        }
    }

    @GetMapping("/{titleId}/parentalguide")
    ApiResponse<ParentsGuide> fetchParentsGuide(@PathVariable("titleId") String titleId){
        try {
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_TITLE + "%s/parentalguide", titleId)).get();
            ParentsGuide parentsGuide = new ParentsGuide();
            try {
                parentsGuide.setTitle(doc.getElementsByClass("subpage_title_block").get(0).getElementsByClass("subpage_title_block__right-column").get(0).getElementsByTag("h3").get(0).getElementsByTag("a").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                parentsGuide.setYear(doc.getElementsByClass("subpage_title_block").get(0).getElementsByClass("subpage_title_block__right-column").get(0).getElementsByTag("h3").get(0).getElementsByTag("span").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                parentsGuide.setCover(generateCover(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("img").attr("src"),0,0));
            }catch (Exception e){
                e.printStackTrace();
            }


            try {
                List<ParentsGuide.Guide> noSpoilGuids = new ArrayList<>();

                for (Element sectionElement: doc.getElementsByClass("content-advisories-index").get(0).getElementsByTag("section")){
                    if (sectionElement.id().equals("certificates")){
                        try {
                            List<ParentsGuide.Certification> certificationList = new ArrayList<>();
                            for (Element tr : sectionElement.getElementsByTag("table").get(0).getElementsByTag("tr")) {
                                ParentsGuide.Certification certification = new ParentsGuide.Certification();
                                try{
                                    certification.setTitle(tr.getElementsByTag("td").get(0).text());
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try{
                                    certification.setSubtitle(tr.getElementsByTag("td").get(1).text());
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                certificationList.add(certification);
                            }
                            parentsGuide.setCertifications(certificationList);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    else if (sectionElement.id().equals("advisory-spoilers")) {
                        List<ParentsGuide.Guide> spoilGuids = new ArrayList<>();
                        try {
                            for (Element sectionElementSpoil: sectionElement.children()){
                                if (sectionElementSpoil.id().contains("advisory")){
                                    ParentsGuide.Guide guide = new ParentsGuide.Guide();

                                    try {
                                        guide.setTitle(sectionElementSpoil.getElementsByClass("ipl-list-title").text());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    try {
                                        List<String> stringList = new ArrayList<>();
                                        for (Element spoilLi: sectionElementSpoil.getElementsByTag("li")){
                                            if (spoilLi.hasClass("advisory-severity-vote")){
                                                try {
                                                    guide.setTypeRate(spoilLi.getElementsByClass("ipl-status-pill").text());
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                            else {
                                                stringList.add(spoilLi.text());
                                            }
                                        }
                                        guide.setItems(stringList);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    spoilGuids.add(guide);
                                }

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    parentsGuide.setSpoilGuides(spoilGuids);
                    }

                    else if (sectionElement.id().contains("advisory") && !sectionElement.id().startsWith("advisory-spoiler")){
                        try {
                            ParentsGuide.Guide guide = new ParentsGuide.Guide();

                            try {
                                guide.setTitle(sectionElement.getElementsByClass("ipl-list-title").text());
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            try {
                                List<String> stringList = new ArrayList<>();
                                for (Element spoilLi: sectionElement.getElementsByTag("li")){
                                    if (spoilLi.hasClass("advisory-severity-vote")){
                                        try {
                                            guide.setTypeRate(spoilLi.getElementsByClass("ipl-status-pill").text());
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                    else {
                                        stringList.add(spoilLi.text());
                                    }
                                }
                                guide.setItems(stringList);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            noSpoilGuids.add(guide);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                parentsGuide.setNoSpoilGuides(noSpoilGuids);
            }catch (Exception e){
                e.printStackTrace();
            }

            return new ApiResponse<>(parentsGuide,null,true);
        }catch (IOException ioException){
            return new ApiResponse<>(null,ioException.getMessage(),false);
        }
    }

    @GetMapping("/{titleId}/awards")
    ApiResponse<MovieAwards> fetchTitleAwards(@PathVariable("titleId") String titleId){
        try {
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_TITLE + "%s/awards", titleId)).get();
            MovieAwards movieAwards = new MovieAwards();
            try {
                movieAwards.setTitle(doc.getElementsByClass("subpage_title_block").get(0).getElementsByClass("subpage_title_block__right-column").get(0).getElementsByTag("h3").get(0).getElementsByTag("a").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                movieAwards.setYear(doc.getElementsByClass("subpage_title_block").get(0).getElementsByClass("subpage_title_block__right-column").get(0).getElementsByTag("h3").get(0).getElementsByTag("span").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                movieAwards.setCover(generateCover(doc.getElementsByClass("subpage_title_block").get(0).getElementsByTag("img").attr("src"),0,0));
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                List<MovieAwards.Event> events = new ArrayList<>();
                for (Element eventElement: doc.getElementsByClass("listo").get(0).children()){
                    MovieAwards.Event event = new MovieAwards.Event();
                    if (eventElement.tagName().equals("h3") && eventElement.getElementsByTag("a").hasClass("event_year")){
                        try {
                            event.setTitle(eventElement.ownText());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            event.setYear(eventElement.getElementsByClass("event_year").text());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            event.setEventId(extractEventId(eventElement.getElementsByTag("a").attr("href")));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            List<MovieAwards.Event.Award> awards = new ArrayList<>();
                            MovieAwards.Event.Award award = null;
                            for (Element tr: eventElement.nextElementSibling().getElementsByTag("tr")){

                                if (!tr.getElementsByClass("title_award_outcome").isEmpty()){
                                    award = new MovieAwards.Event.Award();
                                    awards.add(award);
                                    award.setAwardDescriptions(new ArrayList<>());
                                    try{
                                        award.setAwardOutcome(tr.getElementsByClass("title_award_outcome").get(0).getElementsByTag("b").text());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    try{
                                        award.setAwardCategory(tr.getElementsByClass("title_award_outcome").get(0).getElementsByClass("award_category").text());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                MovieAwards.Event.Award.AwardDescription awardDescription = new MovieAwards.Event.Award.AwardDescription();

                                try{
                                    awardDescription.setTitle(tr.getElementsByClass("award_description").get(0).ownText());
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try{
                                    awardDescription.setNote(tr.getElementsByClass("award_description").get(0).getElementsByClass("award_detail_notes").text());
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try{
                                    List<MovieAwards.Event.Award.AwardDescription.AwardItem> awardItems = new ArrayList<>();
                                    for (Element trDescription: tr.getElementsByClass("award_description").get(0).getElementsByTag("a")){
                                        MovieAwards.Event.Award.AwardDescription.AwardItem awardItem = new MovieAwards.Event.Award.AwardDescription.AwardItem();
                                        try{
                                            awardItem.setTitle(trDescription.ownText());
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        try{
                                            awardItem.setId(extractNameId(trDescription.attr("href")));
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                        awardItems.add(awardItem);
                                    }
                                    awardDescription.setAwardItems(awardItems);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                award.getAwardDescriptions().add(awardDescription);
                            }

                            event.setAwards(awards);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        events.add(event);
                    }
                }

                movieAwards.setEvents(events);
            }catch (Exception e){
                e.printStackTrace();
            }

            return new ApiResponse<>(movieAwards,null,true);
        }catch (IOException ioException){
            return new ApiResponse<>(null,ioException.getMessage(),false);
        }
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

    private List<Calender> getCalender(Document doc) {
        List<Calender> calenders = new ArrayList<>();
        try {
            Calender calender= new Calender();
            for (Element element: doc.getElementById("main").children()){
                try {
                    if (element.tagName().equals("h4")){
                        calender = new Calender();
                        calenders.add(calender);
                        calender.setDate(element.text());
                        calender.setTitles(new ArrayList<>());
                    }
                    else if (element.tagName().equals("ul")){
                        for (Element li: element.getElementsByTag("li")){
                            Calender.LinkTitle linkTitle = new Calender.LinkTitle();
                            linkTitle.setTitle(li.getElementsByTag("a").text());
                            linkTitle.setTitleId(extractTitleId(li.getElementsByTag("a").attr("href")));
                            calender.getTitles().add(linkTitle);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return calenders;
    }

    private List<MovieComingSoon> getComingSoonTitles(Document doc) {
        List<MovieComingSoon> movieComingSoonList = new ArrayList<>();
        try {
            MovieComingSoon movieComingSoon = new MovieComingSoon();
            for (Element element: doc.getElementById("main").getElementsByClass("list").get(0).children()){
                if (element.tagName().equals("h4")){
                    movieComingSoon = new MovieComingSoon();
                    movieComingSoonList.add(movieComingSoon);
                    movieComingSoon.setDate(element.text());
                    movieComingSoon.setTitles(new ArrayList<>());
                }
                else {
                    MovieComingSoon.Title title = new MovieComingSoon.Title();
                    try {
                        Element titleElement = element.getElementsByTag("tr").get(0);

                        try {
                            title.setCover(generateCover(titleElement.getElementsByAttributeValue("id","img_primary").get(0).getElementsByTag("img").attr("src"),280,418));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try {
                            title.setTitle(titleElement.getElementsByClass("overview-top").get(0).getElementsByTag("h4").text());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            title.setTitleId(extractTitleId(titleElement.getElementsByClass("overview-top").get(0).getElementsByTag("h4").get(0).getElementsByTag("a").attr("href")));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            title.setTitleId(extractTitleId(titleElement.getElementsByClass("overview-top").get(0).getElementsByTag("h4").get(0).getElementsByTag("a").attr("href")));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            title.setCertificate(titleElement.getElementsByClass("overview-top").get(0).getElementsByClass("cert-runtime-genre").get(0).getElementsByTag("img").attr("title"));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            title.setRuntime(titleElement.getElementsByClass("overview-top").get(0).getElementsByClass("cert-runtime-genre").get(0).getElementsByTag("time").text());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (Element genreElement: titleElement.getElementsByClass("overview-top").get(0).getElementsByClass("cert-runtime-genre").get(0).getElementsByTag("span")){
                                if (genreElement.hasClass("ghost"))
                                    stringBuilder.append(" | ");
                                else
                                    stringBuilder.append(genreElement.text());
                            }
                            title.setGenres(stringBuilder.toString());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                             title.setMetaScore(titleElement.getElementsByClass("overview-top").get(0).getElementsByClass("rating_txt").get(0).getElementsByClass("metascore").text());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                             title.setSummary(titleElement.getElementsByClass("overview-top").get(0).getElementsByClass("outline").text());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            List<MovieComingSoon.Title.Name> names = new ArrayList<>();
                            for (Element directorElement : titleElement.getElementsByClass("txt-block").get(0).getElementsByTag("a")){
                                MovieComingSoon.Title.Name name = new MovieComingSoon.Title.Name();
                                name.setName(directorElement.text());
                                name.setNameId(extractNameId(directorElement.attr("href")));
                                names.add(name);
                            }
                             title.setDirectors(names);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            List<MovieComingSoon.Title.Name> names = new ArrayList<>();
                            for (Element directorElement : titleElement.getElementsByClass("txt-block").get(1).getElementsByTag("a")){
                                MovieComingSoon.Title.Name name = new MovieComingSoon.Title.Name();
                                name.setName(directorElement.text());
                                name.setNameId(extractNameId(directorElement.attr("href")));
                                names.add(name);
                            }
                             title.setStars(names);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    movieComingSoon.getTitles().add(title);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return movieComingSoonList;
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

    private String extractEventId(String text) {
        Matcher m = eventPattern.matcher(text);
        if (m.find())
            return m.group();

        return null;
    }
}
