package utils.observer;

import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;

public class SimpleTag {
    private String tagName;
    private final Map<String, String> tagAttr = new HashMap<>();
    private String content;

    public SimpleTag(String html) throws IllegalFormatException {
        tagName = "";
        content = "";
        if (html.charAt(0) != '<') {
            throw new IllegalArgumentException("not start with \'<\'");
        }

    }
}
