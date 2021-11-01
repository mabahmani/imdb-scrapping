package ir.mab.imdbscrapping.model;

import java.util.List;

public class HomeGraphQl {
    List<MovieCard> fanPicksTitles;
    List<MovieCard> comingSoonMovies;
    List<MovieCard> showTimesTitles;
    List<StreamProvider> streamingTitles;
    List<BornToday> bornTodayList;

    public List<MovieCard> getShowTimesTitles() {
        return showTimesTitles;
    }

    public void setShowTimesTitles(List<MovieCard> showTimesTitles) {
        this.showTimesTitles = showTimesTitles;
    }

    public List<MovieCard> getComingSoonMovies() {
        return comingSoonMovies;
    }

    public void setComingSoonMovies(List<MovieCard> comingSoonMovies) {
        this.comingSoonMovies = comingSoonMovies;
    }

    public List<StreamProvider> getStreamingTitles() {
        return streamingTitles;
    }

    public void setStreamingTitles(List<StreamProvider> streamingTitles) {
        this.streamingTitles = streamingTitles;
    }

    public List<MovieCard> getFanPicksTitles() {
        return fanPicksTitles;
    }

    public void setFanPicksTitles(List<MovieCard> fanPicksTitles) {
        this.fanPicksTitles = fanPicksTitles;
    }

    public List<BornToday> getBornTodayList() {
        return bornTodayList;
    }

    public void setBornTodayList(List<BornToday> bornTodayList) {
        this.bornTodayList = bornTodayList;
    }

    public static class StreamProvider{
        String name;
        List<MovieCard> titles;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<MovieCard> getTitles() {
            return titles;
        }

        public void setTitles(List<MovieCard> titles) {
            this.titles = titles;
        }
    }
    public static class MovieCard{
        String cover;
        Float rate;
        Integer voteCount;
        Integer runtime;
        String title;
        String releaseYear;
        String releaseMonth;
        String releaseDay;
        String certificate;
        String titleId;
        String videoId;

        public String getReleaseMonth() {
            return releaseMonth;
        }

        public void setReleaseMonth(String releaseMonth) {
            this.releaseMonth = releaseMonth;
        }

        public String getReleaseDay() {
            return releaseDay;
        }

        public void setReleaseDay(String releaseDay) {
            this.releaseDay = releaseDay;
        }

        public Integer getVoteCount() {
            return voteCount;
        }

        public void setVoteCount(Integer voteCount) {
            this.voteCount = voteCount;
        }

        public Integer getRuntime() {
            return runtime;
        }

        public void setRuntime(Integer runtime) {
            this.runtime = runtime;
        }

        public String getReleaseYear() {
            return releaseYear;
        }

        public void setReleaseYear(String releaseYear) {
            this.releaseYear = releaseYear;
        }

        public String getCertificate() {
            return certificate;
        }

        public void setCertificate(String certificate) {
            this.certificate = certificate;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public Float getRate() {
            return rate;
        }

        public void setRate(Float rate) {
            this.rate = rate;
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

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }
    }

    public static class BornToday{
        String image;
        String title;
        String birth;
        String death;
        String nameId;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBirth() {
            return birth;
        }

        public void setBirth(String birth) {
            this.birth = birth;
        }

        public String getDeath() {
            return death;
        }

        public void setDeath(String death) {
            this.death = death;
        }

        public String getNameId() {
            return nameId;
        }

        public void setNameId(String nameId) {
            this.nameId = nameId;
        }
    }
}
