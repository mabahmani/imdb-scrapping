package ir.mab.imdbscrapping.util;

public class AppConstants {
    public static final String IMDB_URL  = "https://www.imdb.com";
    public static final String IMDB_TOP_250  = IMDB_URL + "/chart/top/";
    public static final String IMDB_TOP_TV_250  = IMDB_URL + "/chart/toptv/";

    public static class Api{
        public static final String BASE_URL = "/api";
        public static final String MOVIES = BASE_URL + "/movies";
        public static final String VIDEOS = BASE_URL + "/videos";
    }
}
