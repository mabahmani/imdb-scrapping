package ir.mab.imdbscrapping.model;

public class MovieSummary {
    Integer rank;
    Double imdbRating;
    Long numberOfRating;
    String cover;
    String title;
    String year;
    String link;

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(Double imdbRating) {
        this.imdbRating = imdbRating;
    }

    public Long getNumberOfRating() {
        return numberOfRating;
    }

    public void setNumberOfRating(Long numberOfRating) {
        this.numberOfRating = numberOfRating;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
