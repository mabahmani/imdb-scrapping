package ir.mab.imdbscrapping.model;

import java.util.List;

public class Faqs {
    String title;
    String year;
    String cover;
    List<Faq> faqsNoSpoiler;
    List<Faq> faqsSpoiler;

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

    public List<Faq> getFaqsNoSpoiler() {
        return faqsNoSpoiler;
    }

    public void setFaqsNoSpoiler(List<Faq> faqsNoSpoiler) {
        this.faqsNoSpoiler = faqsNoSpoiler;
    }

    public List<Faq> getFaqsSpoiler() {
        return faqsSpoiler;
    }

    public void setFaqsSpoiler(List<Faq> faqsSpoiler) {
        this.faqsSpoiler = faqsSpoiler;
    }

    public static class Faq{
        String question;
        String answer;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}
