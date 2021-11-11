package ir.mab.imdbscrapping.model;

public class NameSearchBirthDay {
    String position;
    String name;
    String nameId;
    String avatar;
    String summary;
    TopMovie topMovie;

    public TopMovie getTopMovie() {
        return topMovie;
    }

    public void setTopMovie(TopMovie topMovie) {
        this.topMovie = topMovie;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public static class TopMovie{
        String role;
        String title;
        String titleId;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
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
