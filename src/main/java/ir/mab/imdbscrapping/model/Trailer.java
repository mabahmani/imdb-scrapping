package ir.mab.imdbscrapping.model;

public class Trailer {
    String videoId;
    String videoName;
    Integer videoRuntime;
    String videoDescription;
    String videoThumbnail;
    String titleId;
    String title;
    String titleCover;
    Integer titleReleaseYear;
    Integer titleReleaseMonth;
    Integer titleReleaseDay;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public Integer getVideoRuntime() {
        return videoRuntime;
    }

    public void setVideoRuntime(Integer videoRuntime) {
        this.videoRuntime = videoRuntime;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleCover() {
        return titleCover;
    }

    public void setTitleCover(String titleCover) {
        this.titleCover = titleCover;
    }

    public Integer getTitleReleaseYear() {
        return titleReleaseYear;
    }

    public void setTitleReleaseYear(Integer titleReleaseYear) {
        this.titleReleaseYear = titleReleaseYear;
    }

    public Integer getTitleReleaseMonth() {
        return titleReleaseMonth;
    }

    public void setTitleReleaseMonth(Integer titleReleaseMonth) {
        this.titleReleaseMonth = titleReleaseMonth;
    }

    public Integer getTitleReleaseDay() {
        return titleReleaseDay;
    }

    public void setTitleReleaseDay(Integer titleReleaseDay) {
        this.titleReleaseDay = titleReleaseDay;
    }
}
