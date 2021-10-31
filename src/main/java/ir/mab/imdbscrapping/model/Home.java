package ir.mab.imdbscrapping.model;

import java.util.List;

public class Home {
    List<Trailer> trailers;
    List<Featured> featuredToday;
    List<Featured> imdbOriginals;
    List<Featured> editorPicks;
    BoxOffice boxOffice;
    List<News> news;

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    public List<Featured> getFeaturedToday() {
        return featuredToday;
    }

    public void setFeaturedToday(List<Featured> featuredToday) {
        this.featuredToday = featuredToday;
    }

    public List<Featured> getImdbOriginals() {
        return imdbOriginals;
    }

    public void setImdbOriginals(List<Featured> imdbOriginals) {
        this.imdbOriginals = imdbOriginals;
    }

    public List<Featured> getEditorPicks() {
        return editorPicks;
    }

    public void setEditorPicks(List<Featured> editorPicks) {
        this.editorPicks = editorPicks;
    }

    public BoxOffice getBoxOffice() {
        return boxOffice;
    }

    public void setBoxOffice(BoxOffice boxOffice) {
        this.boxOffice = boxOffice;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    public static class Trailer {
        String title;
        String subTitle;
        String titleId;
        String videoId;
        String preview;
        String cover;
        String duration;

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
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

        public String getPreview() {
            return preview;
        }

        public void setPreview(String preview) {
            this.preview = preview;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
    }

    public static class Featured{
        String title;
        String caption;
        String cover;
        Boolean image;
        Boolean video;
        String link;
        String id;
        String rmId;

        public String getRmId() {
            return rmId;
        }

        public void setRmId(String rmId) {
            this.rmId = rmId;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

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

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public Boolean getImage() {
            return image;
        }

        public void setImage(Boolean image) {
            this.image = image;
        }

        public Boolean getVideo() {
            return video;
        }

        public void setVideo(Boolean video) {
            this.video = video;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    public static class BoxOffice{
        String weekendStartDate;
        String weekendEndDate;
        List<Data> data;

        public String getWeekendStartDate() {
            return weekendStartDate;
        }

        public void setWeekendStartDate(String weekendStartDate) {
            this.weekendStartDate = weekendStartDate;
        }

        public String getWeekendEndDate() {
            return weekendEndDate;
        }

        public void setWeekendEndDate(String weekendEndDate) {
            this.weekendEndDate = weekendEndDate;
        }

        public List<Data> getData() {
            return data;
        }

        public void setData(List<Data> data) {
            this.data = data;
        }

        public static class Data {
            Integer weekendGross;
            String currency;
            Integer cinemas;
            String title;
            String titleId;

            public Integer getWeekendGross() {
                return weekendGross;
            }

            public void setWeekendGross(Integer weekendGross) {
                this.weekendGross = weekendGross;
            }

            public String getCurrency() {
                return currency;
            }

            public void setCurrency(String currency) {
                this.currency = currency;
            }

            public Integer getCinemas() {
                return cinemas;
            }

            public void setCinemas(Integer cinemas) {
                this.cinemas = cinemas;
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
        }
    }

    public static class News{
        String date;
        String image;
        String title;
        String id;
        String source;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }
    public static class MovieCard{
        String cover;
        String rate;
        String title;
        String titleId;
        String videoId;

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
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
}
