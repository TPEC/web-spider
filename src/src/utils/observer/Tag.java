package src.utils.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tag {
    private Tag father;
    private final List<Tag> children = new ArrayList<>();

    private String tagName;
    private final Map<String, String> tagAttr = new HashMap<>();

    public Tag() {
        this.father = null;
        tagName = "";
    }

    public int decodeFromHtml(final String html, final Tag father) throws Exception {
        boolean simple = false;
        this.father = father;
        int start = html.indexOf('<');
        int end = html.indexOf('>', start);
        String inner = html.substring(start + 1, end);
        if (inner.charAt(inner.length() - 1) == '/') {
            inner = inner.substring(0, inner.length() - 1);
            simple = true;
        }
        String[] attrs = inner.split(" ");
        this.tagName = attrs[0];
        //todo attrs

        if (!simple) {
            int i;
            int j = 1;
            for (i = end + 1; i < html.length() - 1 && j != 0; i++) {
                if (html.charAt(i) == '/') {
                    if (html.charAt(i + 1) == '>') {
                        j--;
                    }
                } else if (html.charAt(i) == '<') {
                    if (html.charAt(i + 1) == '/') {
                        j--;
                    } else {
                        if (!html.substring(i + 1, i + 4).equals("hr>")) {
                            j++;
                        }
                    }
                }
            }
            if (j == 0) {
                while (true) {
                    Tag child = new Tag();
                    int k = child.decodeFromHtml(html.substring(end + 1), this);
                    this.children.add(child);
                    if (k == -1) {
                        break;
                    }
                }
                end = html.indexOf('>', i);
                if (html.indexOf('<', end) == -1) {
                    return -1;
                } else {
                    return end + 1;
                }
            } else {
                throw new Exception("format error");
            }
        } else {
            if (html.indexOf('<', end) == -1) {
                return -1;
            } else {
                return end + 1;
            }
        }
    }
}
