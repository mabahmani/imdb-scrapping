package ir.mab.imdbscrapping.model;

import java.util.List;

public class NameDetails {
    String id;
    String name;
    List<String> jobTitles;
    String avatar;
    Trailer trailer;
    String bioSummary;
    String birthDate;
    String birthDateYear;
    String birthDateMonthDay;
    String birthPlace;
    List<Photo> photos;
    List<KnownFor> knownForList;
    List<Filmography> filmographies;
    List<RelatedVideo> relatedVideos;
    List<PersonalDetail> personalDetails;

    public Trailer getTrailer() {
        return trailer;
    }

    public void setTrailer(Trailer trailer) {
        this.trailer = trailer;
    }

    public List<PersonalDetail> getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(List<PersonalDetail> personalDetails) {
        this.personalDetails = personalDetails;
    }

    public List<RelatedVideo> getRelatedVideos() {
        return relatedVideos;
    }

    public void setRelatedVideos(List<RelatedVideo> relatedVideos) {
        this.relatedVideos = relatedVideos;
    }

    public List<Filmography> getFilmographies() {
        return filmographies;
    }

    public void setFilmographies(List<Filmography> filmographies) {
        this.filmographies = filmographies;
    }

    public List<KnownFor> getKnownForList() {
        return knownForList;
    }

    public void setKnownForList(List<KnownFor> knownForList) {
        this.knownForList = knownForList;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getJobTitles() {
        return jobTitles;
    }

    public void setJobTitles(List<String> jobTitles) {
        this.jobTitles = jobTitles;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBioSummary() {
        return bioSummary;
    }

    public void setBioSummary(String bioSummary) {
        this.bioSummary = bioSummary;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthDateYear() {
        return birthDateYear;
    }

    public void setBirthDateYear(String birthDateYear) {
        this.birthDateYear = birthDateYear;
    }

    public String getBirthDateMonthDay() {
        return birthDateMonthDay;
    }

    public void setBirthDateMonthDay(String birthDateMonthDay) {
        this.birthDateMonthDay = birthDateMonthDay;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public static class Trailer{
        String cover;
        String videoId;
        String caption;

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }
    }
    public static class Photo{
        String id;
        String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class KnownFor{
        String cover;
        String title;
        String titleId;
        String inMovieName;
        String year;

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
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

        public String getInMovieName() {
            return inMovieName;
        }

        public void setInMovieName(String inMovieName) {
            this.inMovieName = inMovieName;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }
    }

    public static class Filmography{
        String headTitle;
        String numberOfCredits;
        List<Credit> credits;

        public List<Credit> getCredits() {
            return credits;
        }

        public void setCredits(List<Credit> credits) {
            this.credits = credits;
        }

        public String getHeadTitle() {
            return headTitle;
        }

        public void setHeadTitle(String headTitle) {
            this.headTitle = headTitle;
        }

        public String getNumberOfCredits() {
            return numberOfCredits;
        }

        public void setNumberOfCredits(String numberOfCredits) {
            this.numberOfCredits = numberOfCredits;
        }

        public static class Credit{
            String id;
            String title;
            String subtitle;
            String year;
            List<Episode> episodes;

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

            public String getYear() {
                return year;
            }

            public void setYear(String year) {
                this.year = year;
            }

            public List<Episode> getEpisodes() {
                return episodes;
            }

            public void setEpisodes(List<Episode> episodes) {
                this.episodes = episodes;
            }

            public static class Episode{
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
    }

    public static class RelatedVideo{
        String videoId;
        String title;
        String cover;

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
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
    }

    public static class PersonalDetail{
        String title;
        String subtitle;
        List<LinkText> linkTexts;

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

        public List<LinkText> getLinkTexts() {
            return linkTexts;
        }

        public void setLinkTexts(List<LinkText> linkTexts) {
            this.linkTexts = linkTexts;
        }

        public static class LinkText{
            String text;
            String url;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
