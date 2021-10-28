package ir.mab.imdbscrapping.model;

import java.util.List;

public class MovieDetails {

    Overview overview;
    List<Video> videos;
    List<Photo> photos;
    List<Person> topCasts;
    List<RelatedMovie> relatedMovies;
    Storyline storyline;
    Review topReview;
    Details details;
    BoxOffice boxOffice;
    List<TechnicalSpecs> technicalSpecs;

    public Overview getOverview() {
        return overview;
    }

    public void setOverview(Overview overview) {
        this.overview = overview;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public List<Person> getTopCasts() {
        return topCasts;
    }

    public void setTopCasts(List<Person> topCasts) {
        this.topCasts = topCasts;
    }

    public List<RelatedMovie> getRelatedMovies() {
        return relatedMovies;
    }

    public void setRelatedMovies(List<RelatedMovie> relatedMovies) {
        this.relatedMovies = relatedMovies;
    }

    public Storyline getStoryline() {
        return storyline;
    }

    public void setStoryline(Storyline storyline) {
        this.storyline = storyline;
    }

    public Review getTopReview() {
        return topReview;
    }

    public void setTopReview(Review topReview) {
        this.topReview = topReview;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public BoxOffice getBoxOffice() {
        return boxOffice;
    }

    public void setBoxOffice(BoxOffice boxOffice) {
        this.boxOffice = boxOffice;
    }

    public List<TechnicalSpecs> getTechnicalSpecs() {
        return technicalSpecs;
    }

    public void setTechnicalSpecs(List<TechnicalSpecs> technicalSpecs) {
        this.technicalSpecs = technicalSpecs;
    }

    public static class Overview{
        String title;
        String releaseYear;
        String parentalGuidCertificate;
        String runtime;
        String imdbRating;
        String numberOfRate;
        String cover;
        String trailerPreview;
        String trailerDuration;
        List<Genre> genres;
        String plot;
        List<Person> directors;
        List<Person> writers;
        List<Person> stars;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getReleaseYear() {
            return releaseYear;
        }

        public void setReleaseYear(String releaseYear) {
            this.releaseYear = releaseYear;
        }

        public String getParentalGuidCertificate() {
            return parentalGuidCertificate;
        }

        public void setParentalGuidCertificate(String parentalGuidCertificate) {
            this.parentalGuidCertificate = parentalGuidCertificate;
        }

        public String getRuntime() {
            return runtime;
        }

        public void setRuntime(String runtime) {
            this.runtime = runtime;
        }

        public String getImdbRating() {
            return imdbRating;
        }

        public void setImdbRating(String imdbRating) {
            this.imdbRating = imdbRating;
        }

        public String getNumberOfRate() {
            return numberOfRate;
        }

        public void setNumberOfRate(String numberOfRate) {
            this.numberOfRate = numberOfRate;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getTrailerPreview() {
            return trailerPreview;
        }

        public void setTrailerPreview(String trailerPreview) {
            this.trailerPreview = trailerPreview;
        }

        public String getTrailerDuration() {
            return trailerDuration;
        }

        public void setTrailerDuration(String trailerDuration) {
            this.trailerDuration = trailerDuration;
        }

        public List<Genre> getGenres() {
            return genres;
        }

        public void setGenres(List<Genre> genres) {
            this.genres = genres;
        }

        public String getPlot() {
            return plot;
        }

        public void setPlot(String plot) {
            this.plot = plot;
        }

        public List<Person> getDirectors() {
            return directors;
        }

        public void setDirectors(List<Person> directors) {
            this.directors = directors;
        }

        public List<Person> getWriters() {
            return writers;
        }

        public void setWriters(List<Person> writers) {
            this.writers = writers;
        }

        public List<Person> getStars() {
            return stars;
        }

        public void setStars(List<Person> stars) {
            this.stars = stars;
        }
    }

    public static class Genre{
        String title;
        String link;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    public static class Keyword{
        String title;
        String link;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    public static class Person{
        String realName;
        String movieName;
        String image;
        String link;
        String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getMovieName() {
            return movieName;
        }

        public void setMovieName(String movieName) {
            this.movieName = movieName;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    public static class Video{
        String title;
        String duration;
        String preview;
        String link;
        String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getPreview() {
            return preview;
        }

        public void setPreview(String preview) {
            this.preview = preview;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    public static class Photo {
        String thumbnail;
        String original;

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }
    }

    public static class RelatedMovie{
        String title;
        String rate;
        String cover;
        String link;
        String id;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class Storyline{
        String story;
        List<Keyword> keywords;
        String taglines;
        List<Genre> genres;
        LinkTitle motionPictureRating;
        LinkTitle parentsGuide;

        public String getStory() {
            return story;
        }

        public void setStory(String story) {
            this.story = story;
        }

        public List<Keyword> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<Keyword> keywords) {
            this.keywords = keywords;
        }

        public String getTaglines() {
            return taglines;
        }

        public void setTaglines(String taglines) {
            this.taglines = taglines;
        }

        public List<Genre> getGenres() {
            return genres;
        }

        public void setGenres(List<Genre> genres) {
            this.genres = genres;
        }

        public LinkTitle getMotionPictureRating() {
            return motionPictureRating;
        }

        public void setMotionPictureRating(LinkTitle motionPictureRating) {
            this.motionPictureRating = motionPictureRating;
        }

        public LinkTitle getParentsGuide() {
            return parentsGuide;
        }

        public void setParentsGuide(LinkTitle parentsGuide) {
            this.parentsGuide = parentsGuide;
        }
    }

    public static class LinkTitle {
        String title;
        String link;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    public static class Review{
        String title;
        String review;
        String date;
        String rating;
        String username;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getReview() {
            return review;
        }

        public void setReview(String review) {
            this.review = review;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    public static class Details{
        List<LinkTitle> releaseDate;
        List<LinkTitle> countryOfOrigin;
        List<LinkTitle> officialSites;
        List<LinkTitle> language;
        List<LinkTitle> filmingLocations;
        List<LinkTitle> productionCompanies;

        public List<LinkTitle> getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(List<LinkTitle> releaseDate) {
            this.releaseDate = releaseDate;
        }

        public List<LinkTitle> getCountryOfOrigin() {
            return countryOfOrigin;
        }

        public void setCountryOfOrigin(List<LinkTitle> countryOfOrigin) {
            this.countryOfOrigin = countryOfOrigin;
        }

        public List<LinkTitle> getOfficialSites() {
            return officialSites;
        }

        public void setOfficialSites(List<LinkTitle> officialSites) {
            this.officialSites = officialSites;
        }

        public List<LinkTitle> getLanguage() {
            return language;
        }

        public void setLanguage(List<LinkTitle> language) {
            this.language = language;
        }

        public List<LinkTitle> getFilmingLocations() {
            return filmingLocations;
        }

        public void setFilmingLocations(List<LinkTitle> filmingLocations) {
            this.filmingLocations = filmingLocations;
        }

        public List<LinkTitle> getProductionCompanies() {
            return productionCompanies;
        }

        public void setProductionCompanies(List<LinkTitle> productionCompanies) {
            this.productionCompanies = productionCompanies;
        }
    }

    public static class BoxOffice{
        String budget;
        String grossUsAndCanada;
        String openingWeekendUsAndCanada;
        String grossWorldwide;

        public String getBudget() {
            return budget;
        }

        public void setBudget(String budget) {
            this.budget = budget;
        }

        public String getGrossUsAndCanada() {
            return grossUsAndCanada;
        }

        public void setGrossUsAndCanada(String grossUsAndCanada) {
            this.grossUsAndCanada = grossUsAndCanada;
        }

        public String getOpeningWeekendUsAndCanada() {
            return openingWeekendUsAndCanada;
        }

        public void setOpeningWeekendUsAndCanada(String openingWeekendUsAndCanada) {
            this.openingWeekendUsAndCanada = openingWeekendUsAndCanada;
        }

        public String getGrossWorldwide() {
            return grossWorldwide;
        }

        public void setGrossWorldwide(String grossWorldwide) {
            this.grossWorldwide = grossWorldwide;
        }
    }

    public static class TechnicalSpecs{
        String title;
        String subtitle;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }
    }
}
