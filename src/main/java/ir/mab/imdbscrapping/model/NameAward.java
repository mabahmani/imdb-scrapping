package ir.mab.imdbscrapping.model;

import java.util.ArrayList;
import java.util.List;

public class NameAward {
    String name;
    String avatar;
    List<Event> events;

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

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public static class Event{
        String title;
        List<Award> awards;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Award> getAwards() {
            return awards;
        }

        public void setAwards(List<Award> awards) {
            this.awards = awards;
        }

        public static class Award{
            LinkYear year;
            List<AwardOutcome> awardOutcomes = new ArrayList<>();

            public LinkYear getYear() {
                return year;
            }

            public void setYear(LinkYear year) {
                this.year = year;
            }

            public List<AwardOutcome> getAwardOutcomes() {
                return awardOutcomes;
            }

            public void setAwardOutcomes(List<AwardOutcome> awardOutcomes) {
                this.awardOutcomes = awardOutcomes;
            }


            public static class LinkYear{
                String id;
                String year;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getYear() {
                    return year;
                }

                public void setYear(String year) {
                    this.year = year;
                }
            }

            public static class AwardOutcome{
                String title;
                String subtitle;
                List<AwardDescription> awardDescriptions = new ArrayList<>();

                public List<AwardDescription> getAwardDescriptions() {
                    return awardDescriptions;
                }

                public void setAwardDescriptions(List<AwardDescription> awardDescriptions) {
                    this.awardDescriptions = awardDescriptions;
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

                public static class AwardDescription{
                    String description;
                    List<Title> titles;

                    public String getDescription() {
                        return description;
                    }

                    public void setDescription(String description) {
                        this.description = description;
                    }

                    public List<Title> getTitles() {
                        return titles;
                    }

                    public void setTitles(List<Title> titles) {
                        this.titles = titles;
                    }

                    public static class Title{
                        String id;
                        String title;
                        String titleYear;

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

                        public String getTitleYear() {
                            return titleYear;
                        }

                        public void setTitleYear(String titleYear) {
                            this.titleYear = titleYear;
                        }
                    }
                }

            }

        }

    }
}
