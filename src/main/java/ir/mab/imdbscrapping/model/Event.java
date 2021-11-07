package ir.mab.imdbscrapping.model;

import java.util.List;

public class Event {
    String title;
    String subtitle;
    String year;
    List<Award> awards;

    public List<Award> getAwards() {
        return awards;
    }

    public void setAwards(List<Award> awards) {
        this.awards = awards;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public static class Award{
        String name;
        List<AwardCategory> awardCategories;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<AwardCategory> getAwardCategories() {
            return awardCategories;
        }

        public void setAwardCategories(List<AwardCategory> awardCategories) {
            this.awardCategories = awardCategories;
        }

        public static class AwardCategory{
            String title;
            List<AwardNomination> awardNominations;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public List<AwardNomination> getAwardNominations() {
                return awardNominations;
            }

            public void setAwardNominations(List<AwardNomination> awardNominations) {
                this.awardNominations = awardNominations;
            }

            public static class AwardNomination{
                List<Nominee> primaryNominees;
                List<Nominee> secondaryNominees;
                String notes;
                List<String> songNames;
                List<String> episodeNames;
                Boolean isWinner = false;

                public List<Nominee> getPrimaryNominees() {
                    return primaryNominees;
                }

                public void setPrimaryNominees(List<Nominee> primaryNominees) {
                    this.primaryNominees = primaryNominees;
                }

                public List<Nominee> getSecondaryNominees() {
                    return secondaryNominees;
                }

                public void setSecondaryNominees(List<Nominee> secondaryNominees) {
                    this.secondaryNominees = secondaryNominees;
                }

                public String getNotes() {
                    return notes;
                }

                public void setNotes(String notes) {
                    this.notes = notes;
                }

                public List<String> getSongNames() {
                    return songNames;
                }

                public void setSongNames(List<String> songNames) {
                    this.songNames = songNames;
                }

                public List<String> getEpisodeNames() {
                    return episodeNames;
                }

                public void setEpisodeNames(List<String> episodeNames) {
                    this.episodeNames = episodeNames;
                }

                public Boolean getWinner() {
                    return isWinner;
                }

                public void setWinner(Boolean winner) {
                    isWinner = winner;
                }

                public static class Nominee{
                    String name;
                    String note;
                    String imageUrl;
                    String id;

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getNote() {
                        return note;
                    }

                    public void setNote(String note) {
                        this.note = note;
                    }

                    public String getImageUrl() {
                        return imageUrl;
                    }

                    public void setImageUrl(String imageUrl) {
                        this.imageUrl = imageUrl;
                    }

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }
                }
            }
        }

    }

}
