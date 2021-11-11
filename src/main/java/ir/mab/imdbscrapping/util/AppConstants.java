package ir.mab.imdbscrapping.util;

public class AppConstants {
    public static final String IMDB_URL  = "https://www.imdb.com";
    public static final String IMDB_URL_GRAPH_QL  = "https://api.graphql.imdb.com/";
    public static final String IMDB_TOP_250  = IMDB_URL + "/chart/top/";
    public static final String IMDB_TOP_TV_250  = IMDB_URL + "/chart/toptv/";
    public static final String IMDB_BOTTOM_100  = IMDB_URL + "/chart/bottom/";
    public static final String IMDB_BOX_OFFICE  = IMDB_URL + "/chart/boxoffice/";
    public static final String IMDB_CALENDER  = IMDB_URL + "/calendar/";
    public static final String IMDB_COMING_SOON  = IMDB_URL + "/movies-coming-soon/";
    public static final String IMDB_POPULAR  = IMDB_URL + "/chart/moviemeter/";
    public static final String IMDB_POPULAR_TV  = IMDB_URL + "/chart/tvmeter/";

    public static class Api{
        public static final String BASE_URL = "/api";
        public static final String TITLES = BASE_URL + "/titles";
        public static final String VIDEOS = BASE_URL + "/videos";
        public static final String IMAGES = BASE_URL + "/images";
        public static final String NAMES = BASE_URL + "/names";
        public static final String NEWS = BASE_URL + "/news";
        public static final String EVENTS = BASE_URL + "/events";
        public static final String SEARCH = BASE_URL + "/search";
        public static final String GENRE = BASE_URL + "/genres";
        public static final String KEYWORD = BASE_URL + "/keywords";
    }
}
