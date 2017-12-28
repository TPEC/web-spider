package src.utils.observer;

import java.util.HashMap;
import java.util.Map;

public class SimpleTag {
    private String tagName;
    private final Map<String, String> tagAttr;
    private String content;

    public SimpleTag() {
        this("");
    }

    public SimpleTag(final String tagName) {
        this(tagName, "");
    }

    public SimpleTag(final String tagName, String content) {
        this(tagName, content, null);
    }

    public SimpleTag(final String tagName, final String content, final Map<String, String> tagAttr) {
        this.tagName = tagName;
        this.content = content;
        if (tagAttr == null) {
            this.tagAttr = new HashMap<>();
        } else {
            this.tagAttr = tagAttr;
        }
    }

    public String getTagName() {
        return tagName;
    }

    public String getContent() {
        return content;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttr(String attrName) {
        return tagAttr.get(attrName);
    }

    public void setTagAttr(final Map<String, String> tagAttr) {
        this.tagAttr.clear();
        for (Map.Entry<String, String> e : tagAttr.entrySet()) {
            this.tagAttr.put(e.getKey(), e.getValue());
        }
    }
}
