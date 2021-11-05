package ir.mab.imdbscrapping.model;

import java.util.List;

public class NameBio {
    String name;
    String avatar;
    List<TitleSubtitle> overview;
    String miniBio;
    List<TitleSubtitle> family;
    List<String> trademark;
    List<String> trivia;
    List<TitleSubtitle> salary ;

    public List<TitleSubtitle> getSalary() {
        return salary;
    }

    public void setSalary(List<TitleSubtitle> salary) {
        this.salary = salary;
    }

    public List<String> getTrivia() {
        return trivia;
    }

    public void setTrivia(List<String> trivia) {
        this.trivia = trivia;
    }

    public List<String> getTrademark() {
        return trademark;
    }

    public void setTrademark(List<String> trademark) {
        this.trademark = trademark;
    }

    public List<TitleSubtitle> getFamily() {
        return family;
    }

    public void setFamily(List<TitleSubtitle> family) {
        this.family = family;
    }

    public String getMiniBio() {
        return miniBio;
    }

    public void setMiniBio(String miniBio) {
        this.miniBio = miniBio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<TitleSubtitle> getOverview() {
        return overview;
    }

    public void setOverview(List<TitleSubtitle> overview) {
        this.overview = overview;
    }

    public static class TitleSubtitle {
        String id;
        String title;
        String subtitle;

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

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }
    }
}
