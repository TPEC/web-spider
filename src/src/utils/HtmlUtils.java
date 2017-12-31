package src.utils;

import src.utils.observer.SimpleTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlUtils {
    public static List<SimpleTag> getSimpleTags(String html, String tagName) {
        String findStr = "<" + tagName;
        List<SimpleTag> tags = new ArrayList<>();
        int i = -findStr.length();
        while ((i = html.indexOf(findStr, i + findStr.length())) >= 0) {
            char c = html.charAt(i + findStr.length());
            if (c == ' ' || c == '>' || (c == '/' && html.charAt(i + findStr.length() + 1) == '>')) {
                SimpleTag tag = new SimpleTag(html.substring(i + 1, i + findStr.length()));
                Map<String, String> attrs = new HashMap<>();
                int start;
                if ((start = getAttrs(html, i + findStr.length() + 1, attrs)) >= 0 || c == '>') {
                    int end = html.indexOf("</" + tagName + ">", start + 1);
                    if (end >= 0) {
                        tag.setContent(html.substring(start, end));
                        i = end + findStr.length() + 1;
                    }
                } else {
                    i += findStr.length();
                }
                tag.setTagAttr(attrs);
                tags.add(tag);
            }
        }
        return tags;
    }

    private static int getAttrs(String html, int start, Map<String, String> attrs) {
        boolean kvflag = false; //false-key;true-flag
        char inString = 0;
        int j = start;
        String key = "";
        for (int i = start; i < html.length(); i++) {
            char c = html.charAt(i);
            if (inString == 0) {
                if (c == '>' || (c == '/' && html.charAt(i + 1) == '>')) {
                    if (!key.isEmpty()) {
                        attrs.put(removeRedundantSpace(key), removeRedundantSpace(html.substring(j, i)));
                    }
                    if (c == '>') {
                        return i + 1;
                    } else {
                        return -1;
                    }
                }
                if (!kvflag) {
                    if (c == '=') {
                        kvflag = true;
                        key = html.substring(j, i);
                        j = i + 1;
                    }
                } else {
                    if (c == ' ' && i > j) {
                        kvflag = false;
                        attrs.put(removeRedundantSpace(key), removeRedundantSpace(html.substring(j, i)));
                        j = i + 1;
                        key = "";
                    } else if (c == '\'' || c == '\"') {
                        inString = c;
                    }
                }
            } else {
                if (c == inString) {
                    inString = 0;
                }
            }
        }
        return -1;
    }

    public static boolean isAnUrl(String url) {
        url = url.toLowerCase();
        if (url.startsWith("https://") || url.startsWith("http://")) {
            return true;
        }
        if (url.startsWith("/") && url.length() > 1) {
            return true;
        }
        return false;
    }

    public static String getAbsoluteUrl(String url, String root) {
        if (url.startsWith("/")) {
            return getPureRootUrl(root) + url;
        }
        return url;
    }

    /**
     * 去除 '?'及之后的内容
     *
     * @param url
     * @return
     */
    private static String getPureRootUrl(String url) {
        String r = url.split("\\?")[0];
        int i = r.indexOf('/', 8);
        if (i >= 0) {
            return r.substring(0, i);
        }
        return r;
    }


    private static String removeRedundantSpace(String str) {
        int start = 0;
        for (; start < str.length() && str.charAt(start) == ' '; start++) ;
        int end = str.length();
        for (; end > start && str.charAt(end - 1) == ' '; end--) ;
        if (end - start >= 2) {
            if (str.charAt(start) == str.charAt(end - 1)) {
                if (str.charAt(start) == '\'' || str.charAt(start) == '\"') {
                    start++;
                    end--;
                }
            }
        }
        return str.substring(start, end);
    }
}
