package ir.mab.imdbscrapping.model;

import java.util.List;

public class MovieComingSoon {
    String date;
    List<Title> titles;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Title> getTitles() {
        return titles;
    }

    public void setTitles(List<Title> titles) {
        this.titles = titles;
    }

    public static class Title{
        String title;
        String titleId;
        String cover;
        String certificate;
        String runtime;
        String genres;
        String summary;
        String metaScore;
        List<Name> directors;
        List<Name> stars;

        public String getMetaScore() {
            return metaScore;
        }

        public void setMetaScore(String metaScore) {
            this.metaScore = metaScore;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitleId() {
            return titleId;
        }

        public void setTitleId(String titleId) {
            this.titleId = titleId;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getCertificate() {
            return certificate;
        }

        public void setCertificate(String certificate) {
            this.certificate = certificate;
        }

        public String getRuntime() {
            return runtime;
        }

        public void setRuntime(String runtime) {
            this.runtime = runtime;
        }

        public String getGenres() {
            return genres;
        }

        public void setGenres(String genres) {
            this.genres = genres;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<Name> getDirectors() {
            return directors;
        }

        public void setDirectors(List<Name> directors) {
            this.directors = directors;
        }

        public List<Name> getStars() {
            return stars;
        }

        public void setStars(List<Name> stars) {
            this.stars = stars;
        }

        public static class Name{
            String name;
            String nameId;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getNameId() {
                return nameId;
            }

            public void setNameId(String nameId) {
                this.nameId = nameId;
            }
        }
    }

}
