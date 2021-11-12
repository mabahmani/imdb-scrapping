package ir.mab.imdbscrapping.controller;

import ir.mab.imdbscrapping.model.ApiResponse;
import ir.mab.imdbscrapping.model.Event;
import ir.mab.imdbscrapping.model.EventSummary;
import ir.mab.imdbscrapping.model.News;
import ir.mab.imdbscrapping.util.AppConstants;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = AppConstants.Api.EVENTS)
public class ImdbEventsController {

    @GetMapping("/{eventId}/{year}")
    ApiResponse<Event> fetchEventDetails(@PathVariable("eventId") String eventId,@PathVariable("year") String year) {
        Event event = new Event();
        try {
            Document doc = Jsoup.connect(String.format(AppConstants.IMDB_EVENT + "%s/%s", eventId, year)).get();

            try {
                event.setTitle(doc.getElementsByClass("event-header__title").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                event.setSubtitle(doc.getElementsByClass("event-header__subtitle").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                event.setYear(doc.getElementsByClass("event-year-header__year").text());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                String scriptData = doc.getElementsByClass("ab_widget").get(0).getElementsByTag("script").get(0).data();
                int sIndex = scriptData.indexOf("{\"nomineesWidgetModel\"");
                int eIndex = scriptData.lastIndexOf("]);");
                JSONObject jsonObject = new JSONObject(scriptData.substring(sIndex,eIndex));
                List<Event.Award> awards = new ArrayList<>();

                try {
                    event.setYear(String.valueOf(jsonObject.getJSONObject("nomineesWidgetModel").getJSONObject("eventEditionSummary").getInt("year")));
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {

                    for (Object awardObject : jsonObject.getJSONObject("nomineesWidgetModel").getJSONObject("eventEditionSummary").getJSONArray("awards")){
                        JSONObject awardJsonObject = (JSONObject) awardObject;
                        Event.Award award = new Event.Award();
                        try {
                            award.setName(awardJsonObject.getString("awardName"));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            List<Event.Award.AwardCategory> awardCategories = new ArrayList<>();
                            for (Object awardCategoryObject : awardJsonObject.getJSONArray("categories")){
                                JSONObject awardCategoryJsonObject = (JSONObject) awardCategoryObject;
                                Event.Award.AwardCategory awardCategory = new Event.Award.AwardCategory();
                                try {
                                    awardCategory.setTitle(awardCategoryJsonObject.getString("categoryName"));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                try {
                                    List<Event.Award.AwardCategory.AwardNomination> awardNominations = new ArrayList<>();
                                    for (Object awardNominationObject: awardCategoryJsonObject.getJSONArray("nominations")){
                                        JSONObject awardNominationJsonObject = (JSONObject) awardNominationObject;
                                        Event.Award.AwardCategory.AwardNomination awardNomination = new Event.Award.AwardCategory.AwardNomination();
                                        try {
                                            awardNomination.setWinner(awardNominationJsonObject.getBoolean("isWinner"));
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        try {
                                            awardNomination.setNotes(awardNominationJsonObject.getString("notes"));
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                        try {
                                            List<Event.Award.AwardCategory.AwardNomination.Nominee> nominees = new ArrayList<>();
                                            for (Object nomineeObject: awardNominationJsonObject.getJSONArray("primaryNominees")){
                                                JSONObject nomineeJsonObject = (JSONObject) nomineeObject;
                                                Event.Award.AwardCategory.AwardNomination.Nominee nominee = new Event.Award.AwardCategory.AwardNomination.Nominee();
                                                try {
                                                    nominee.setId(nomineeJsonObject.getString("const"));
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    nominee.setImageUrl(nomineeJsonObject.getString("imageUrl"));
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    nominee.setNote(nomineeJsonObject.getString("note"));
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    nominee.setName(nomineeJsonObject.getString("name"));
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                                nominees.add(nominee);
                                            }
                                            awardNomination.setPrimaryNominees(nominees);
                                        }catch (Exception e){
                                            e.printStackTrace();;
                                        }
                                        try {
                                            List<Event.Award.AwardCategory.AwardNomination.Nominee> nominees = new ArrayList<>();
                                            for (Object nomineeObject: awardNominationJsonObject.getJSONArray("secondaryNominees")){
                                                JSONObject nomineeJsonObject = (JSONObject) nomineeObject;
                                                Event.Award.AwardCategory.AwardNomination.Nominee nominee = new Event.Award.AwardCategory.AwardNomination.Nominee();
                                                try {
                                                    nominee.setId(nomineeJsonObject.getString("const"));
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    nominee.setImageUrl(nomineeJsonObject.getString("imageUrl"));
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    nominee.setNote(nomineeJsonObject.getString("note"));
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    nominee.setName(nomineeJsonObject.getString("name"));
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                                nominees.add(nominee);
                                            }
                                            awardNomination.setSecondaryNominees(nominees);
                                        }catch (Exception e){
                                            e.printStackTrace();;
                                        }
                                        try {
                                            List<String> stringList = new ArrayList<>();
                                            for (Object stringObject: awardNominationJsonObject.getJSONArray("songNames")){
                                                String stringJsonObject = (String) stringObject;
                                                try {
                                                    stringList.add(stringJsonObject);
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }

                                                stringList.add(stringJsonObject);
                                            }
                                            awardNomination.setSongNames(stringList);
                                        }catch (Exception e){
                                            e.printStackTrace();;
                                        }
                                        try {
                                            List<String> stringList = new ArrayList<>();
                                            for (Object stringObject: awardNominationJsonObject.getJSONArray("episodeNames")){
                                                String stringJsonObject = (String) stringObject;
                                                try {
                                                    stringList.add(stringJsonObject);
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }

                                                stringList.add(stringJsonObject);
                                            }
                                            awardNomination.setEpisodeNames(stringList);
                                        }catch (Exception e){
                                            e.printStackTrace();;
                                        }

                                        awardNominations.add(awardNomination);
                                    }
                                    awardCategory.setAwardNominations(awardNominations);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                awardCategories.add(awardCategory);
                            }
                            award.setAwardCategories(awardCategories);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        awards.add(award);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                event.setAwards(awards);
            }catch (Exception e){
                e.printStackTrace();
            }


        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(event, null, true);
    }

    @GetMapping("/")
    ApiResponse<List<EventSummary>> fetchAllEvents() {
        List<EventSummary> events = new ArrayList<>();

        try {
            Document doc = Jsoup.connect("https://d2vyvawkvp20f9.cloudfront.net/event/index/data.json").ignoreContentType(true).get();

            try {

                JSONArray responseJson = new JSONArray(doc.body().text());
                for (Object o : responseJson){
                    try {
                        JSONObject eventObject = (JSONObject) o;
                        EventSummary eventSummary = new EventSummary();
                        eventSummary.setEvent(eventObject.getString("name"));
                        eventSummary.setEventId(eventObject.getString("id"));
                        events.add(eventSummary);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                return new ApiResponse<>(null, e.getMessage(), false);
            }

        } catch (IOException e) {
            return new ApiResponse<>(null, e.getMessage(), false);
        }

        return new ApiResponse<>(events, null, true);
    }

}
