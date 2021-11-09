package ir.mab.imdbscrapping.model;

import java.util.List;

public class MovieAwards {
    String title;
    String year;
    String cover;
    List<Event> events;

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

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public static class Event{
        String eventId;
        String title;
        String year;
        List<Award> awards;

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
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

        public List<Award> getAwards() {
            return awards;
        }

        public void setAwards(List<Award> awards) {
            this.awards = awards;
        }

        public static class Award{
            String awardOutcome;
            String awardCategory;
            List<AwardDescription> awardDescriptions;

            public String getAwardOutcome() {
                return awardOutcome;
            }

            public void setAwardOutcome(String awardOutcome) {
                this.awardOutcome = awardOutcome;
            }

            public String getAwardCategory() {
                return awardCategory;
            }

            public void setAwardCategory(String awardCategory) {
                this.awardCategory = awardCategory;
            }

            public List<AwardDescription> getAwardDescriptions() {
                return awardDescriptions;
            }

            public void setAwardDescriptions(List<AwardDescription> awardDescriptions) {
                this.awardDescriptions = awardDescriptions;
            }

            public static class AwardDescription{
                String title;
                String note;
                List<AwardItem> awardItems;

                public String getNote() {
                    return note;
                }

                public void setNote(String note) {
                    this.note = note;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public List<AwardItem> getAwardItems() {
                    return awardItems;
                }

                public void setAwardItems(List<AwardItem> awardItems) {
                    this.awardItems = awardItems;
                }

                public static class AwardItem{
                    String title;
                    String id;

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
                }
            }
        }
    }
}
