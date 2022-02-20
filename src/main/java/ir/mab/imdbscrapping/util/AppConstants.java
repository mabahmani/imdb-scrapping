package ir.mab.imdbscrapping.util;

public class AppConstants {
    public static final String IMDB_URL             = "https://www.imdb.com";
    public static final String IMDB_URL_GRAPH_QL    = "https://api.graphql.imdb.com/";
    public static final String IMDB_URL_SUGGESTION  = "https://v2.sg.media-imdb.com/suggestion/";
    public static final String IMDB_TOP_250         = IMDB_URL + "/chart/top/";
    public static final String IMDB_TOP_TV_250      = IMDB_URL + "/chart/toptv/";
    public static final String IMDB_BOTTOM_100      = IMDB_URL + "/chart/bottom/";
    public static final String IMDB_BOX_OFFICE      = IMDB_URL + "/chart/boxoffice/";
    public static final String IMDB_CALENDER        = IMDB_URL + "/calendar/";
    public static final String IMDB_COMING_SOON     = IMDB_URL + "/movies-coming-soon/";
    public static final String IMDB_POPULAR         = IMDB_URL + "/chart/moviemeter/";
    public static final String IMDB_POPULAR_TV      = IMDB_URL + "/chart/tvmeter/";
    public static final String IMDB_EVENT           = IMDB_URL + "/event/";
    public static final String IMDB_GENRE           = IMDB_URL + "/feature/genre/";
    public static final String IMDB_LIST            = IMDB_URL + "/list/";
    public static final String IMDB_GALLERY            = IMDB_URL + "/gallery/";
    public static final String IMDB_NAME            = IMDB_URL + "/name/";
    public static final String IMDB_TITLE           = IMDB_URL + "/title/";
    public static final String IMDB_KEYWORD         = IMDB_URL + "/search/keyword/";
    public static final String IMDB_NEWS            = IMDB_URL + "/news/";
    public static final String IMDB_SEARCH_TITLE    = IMDB_URL + "/search/title/";
    public static final String IMDB_SEARCH_NAME     = IMDB_URL + "/search/name/";
    public static final String IMDB_VIDEO           = IMDB_URL + "/video/";

    public static class Api {
        private static final String BASE_URL =   "/api";
        public  static final String HOME     =   BASE_URL + "/home";
        public  static final String CHART    =   BASE_URL + "/chart";
        public  static final String TITLES   =   BASE_URL + "/titles";
        public  static final String VIDEOS   =   BASE_URL + "/videos";
        public  static final String IMAGES   =   BASE_URL + "/images";
        public  static final String NAMES    =   BASE_URL + "/names";
        public  static final String NEWS     =   BASE_URL + "/news";
        public  static final String EVENTS   =   BASE_URL + "/events";
        public  static final String SEARCH   =   BASE_URL + "/search";
        public  static final String GENRE    =   BASE_URL + "/genres";
        public  static final String KEYWORD  =   BASE_URL + "/keywords";
        public  static final String TRAILER  =   BASE_URL + "/trailers";
    }
}
