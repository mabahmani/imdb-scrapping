package ir.mab.imdbscrapping.model;

import java.util.List;

public class Video {
    String highQ;
    String lowQ;
    String videoTitle;
    String videoSubTitle;
    String videoRuntime;
    String videoDescription;
    String videoCover;
    String titleId;
    String relationTitle;
    Boolean relationIsReleased;
    String relationReleaseDate;
    String relationReleaseYear;
    String relationRuntime;
    String relationCertificateRating;
    String relationCertificateRatingsBody;
    Double relationIMDbRating;
    String relationPoster;
    Boolean relationIsIMDbTVTitle;
    List<String> relationGenres;
    List<RelatedVideo> relatedVideos;

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoSubTitle() {
        return videoSubTitle;
    }

    public void setVideoSubTitle(String videoSubTitle) {
        this.videoSubTitle = videoSubTitle;
    }

    public String getVideoRuntime() {
        return videoRuntime;
    }

    public void setVideoRuntime(String videoRuntime) {
        this.videoRuntime = videoRuntime;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoCover() {
        return videoCover;
    }

    public void setVideoCover(String videoCover) {
        this.videoCover = videoCover;
    }

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public String getRelationTitle() {
        return relationTitle;
    }

    public void setRelationTitle(String relationTitle) {
        this.relationTitle = relationTitle;
    }

    public Boolean getRelationIsReleased() {
        return relationIsReleased;
    }

    public void setRelationIsReleased(Boolean relationIsReleased) {
        this.relationIsReleased = relationIsReleased;
    }

    public String getRelationReleaseDate() {
        return relationReleaseDate;
    }

    public void setRelationReleaseDate(String relationReleaseDate) {
        this.relationReleaseDate = relationReleaseDate;
    }

    public String getRelationReleaseYear() {
        return relationReleaseYear;
    }

    public void setRelationReleaseYear(String relationReleaseYear) {
        this.relationReleaseYear = relationReleaseYear;
    }

    public String getRelationRuntime() {
        return relationRuntime;
    }

    public void setRelationRuntime(String relationRuntime) {
        this.relationRuntime = relationRuntime;
    }

    public String getRelationCertificateRating() {
        return relationCertificateRating;
    }

    public void setRelationCertificateRating(String relationCertificateRating) {
        this.relationCertificateRating = relationCertificateRating;
    }

    public String getRelationCertificateRatingsBody() {
        return relationCertificateRatingsBody;
    }

    public void setRelationCertificateRatingsBody(String relationCertificateRatingsBody) {
        this.relationCertificateRatingsBody = relationCertificateRatingsBody;
    }

    public Double getRelationIMDbRating() {
        return relationIMDbRating;
    }

    public void setRelationIMDbRating(Double relationIMDbRating) {
        this.relationIMDbRating = relationIMDbRating;
    }

    public String getRelationPoster() {
        return relationPoster;
    }

    public void setRelationPoster(String relationPoster) {
        this.relationPoster = relationPoster;
    }

    public Boolean getRelationIsIMDbTVTitle() {
        return relationIsIMDbTVTitle;
    }

    public void setRelationIsIMDbTVTitle(Boolean relationIsIMDbTVTitle) {
        this.relationIsIMDbTVTitle = relationIsIMDbTVTitle;
    }

    public List<String> getRelationGenres() {
        return relationGenres;
    }

    public void setRelationGenres(List<String> relationGenres) {
        this.relationGenres = relationGenres;
    }

    public String getHighQ() {
        return highQ;
    }

    public void setHighQ(String highQ) {
        this.highQ = highQ;
    }

    public String getLowQ() {
        return lowQ;
    }

    public void setLowQ(String lowQ) {
        this.lowQ = lowQ;
    }

    public List<RelatedVideo> getRelatedVideos() {
        return relatedVideos;
    }

    public void setRelatedVideos(List<RelatedVideo> relatedVideos) {
        this.relatedVideos = relatedVideos;
    }

    public static class RelatedVideo{
        String cover;
        String duration;
        String title;
        String subtitle;
        String videoId;
        String titleId;

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

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

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

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }
    }
}
