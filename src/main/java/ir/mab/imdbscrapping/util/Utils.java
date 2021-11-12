package ir.mab.imdbscrapping.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String generateImage(String url, int width, int height){
        if (url.isEmpty())
            return null;

        if (width == 0 || height == 0) {
            String[] coverUrlSplits = url.split("._V1_");
            return coverUrlSplits[0] + "._V1_.jpg";
        }

        String[] coverUrlSplits = url.split("._V1_");
        String baseUrl = coverUrlSplits[0] + "._V1_";
        String options = String.format("UY%s_CR%s,0,%s,%s_AL_.jpg", height, 0, 0, 0);
        return baseUrl + options;
    }

    public static String extractNameId(String text) {
        final Pattern namePattern = Pattern.compile("nm+[0-9]+");
        Matcher m = namePattern.matcher(text);
        if (m.find())
            return m.group();

        return null;
    }

    public static String extractTitleId(String text) {
        final Pattern titlePattern = Pattern.compile("tt+[0-9]+");
        Matcher m = titlePattern.matcher(text);
        if (m.find())
            return m.group();

        return null;
    }

    public static String extractVideoId(String text) {
        final Pattern videoPattern = Pattern.compile("vi+[0-9]+");
        Matcher m = videoPattern.matcher(text);
        if (m.find())
            return m.group();

        return null;
    }

    public static String extractEventId(String text) {
        final Pattern eventPattern = Pattern.compile("ev+[0-9]+");
        Matcher m = eventPattern.matcher(text);
        if (m.find())
            return m.group();

        return null;
    }
}
