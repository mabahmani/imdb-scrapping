package ir.mab.imdbscrapping.model;

import java.util.List;

public class Calender {
    String date;
    List<LinkTitle> titles;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<LinkTitle> getTitles() {
        return titles;
    }

    public void setTitles(List<LinkTitle> titles) {
        this.titles = titles;
    }

    public static class LinkTitle{
        String title;
        String titleId;

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
