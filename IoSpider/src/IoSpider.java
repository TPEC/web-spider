import src.utils.HtmlAccessor;
import src.utils.HtmlUtils;
import src.utils.observer.SimpleTag;

import java.util.ArrayList;
import java.util.List;

public class IoSpider {
    public static void main(String[] args) {
        HtmlAccessor htmlAccessor = new HtmlAccessor();
        htmlAccessor.addAttrs("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");

        List<String> pageUrls = new ArrayList<>();
//        pageUrls.add("https://www.baidu.com");
        pageUrls.add("http://music.baidu.com/search?fr=ps&ie=utf-8&key=");

        while (pageUrls.size() > 0) {
            String html = "";
            System.out.println(pageUrls.get(0));
            try {
                html = htmlAccessor.getHtmlSyc(pageUrls.get(0));
            } catch (Exception e) {
//                e.printStackTrace();
            }
            pageUrls.remove(0);
            if (!html.isEmpty()) {
                List<SimpleTag> tags = HtmlUtils.getSimpleTags(html, "a");
                for (SimpleTag t : tags) {
                    String u = t.getAttr("href");
                    if (u != null && !u.isEmpty()) {
                        if (HtmlUtils.isAnUrl(u)) {
                            pageUrls.add(u);
                        }
                    }
                }
            }
        }
    }
}
