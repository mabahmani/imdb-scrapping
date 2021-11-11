package ir.mab.imdbscrapping.model;

import java.util.List;

public class BoxOffice {
    String weekendDate;
    List<BoxOfficeTitle> boxOfficeTitles;

    public String getWeekendDate() {
        return weekendDate;
    }

    public void setWeekendDate(String weekendDate) {
        this.weekendDate = weekendDate;
    }

    public List<BoxOfficeTitle> getBoxOfficeTitles() {
        return boxOfficeTitles;
    }

    public void setBoxOfficeTitles(List<BoxOfficeTitle> boxOfficeTitles) {
        this.boxOfficeTitles = boxOfficeTitles;
    }

    public static class BoxOfficeTitle{
        String titleId;
        String title;
        String cover;
        String weekend;
        String gross;
        String weeks;

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

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getWeekend() {
            return weekend;
        }

        public void setWeekend(String weekend) {
            this.weekend = weekend;
        }

        public String getGross() {
            return gross;
        }

        public void setGross(String gross) {
            this.gross = gross;
        }

        public String getWeeks() {
            return weeks;
        }

        public void setWeeks(String weeks) {
            this.weeks = weeks;
        }
    }
}
