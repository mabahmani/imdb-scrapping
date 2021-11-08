package ir.mab.imdbscrapping.model;

import java.util.List;

public class ParentsGuide {
    String title;
    String year;
    String cover;
    List<Certification> certifications;
    List<Guide> noSpoilGuides;
    List<Guide> spoilGuides;


    public List<Guide> getNoSpoilGuides() {
        return noSpoilGuides;
    }

    public void setNoSpoilGuides(List<Guide> noSpoilGuides) {
        this.noSpoilGuides = noSpoilGuides;
    }

    public List<Guide> getSpoilGuides() {
        return spoilGuides;
    }

    public void setSpoilGuides(List<Guide> spoilGuides) {
        this.spoilGuides = spoilGuides;
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }


    public List<Certification> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<Certification> certifications) {
        this.certifications = certifications;
    }

    public static class Certification{
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

    public static class Guide{
        String title;
        String typeRate;
        List<String> items;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTypeRate() {
            return typeRate;
        }

        public void setTypeRate(String typeRate) {
            this.typeRate = typeRate;
        }

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }
    }

}
