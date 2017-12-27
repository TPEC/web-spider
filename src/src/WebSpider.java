package src;

import utils.HtmlUtils;
import utils.observer.HtmlObserver;
import utils.observer.Tag;

public class WebSpider {
    public static void main(String[] args) {
        HtmlUtils htmlUtils = new HtmlUtils();
        htmlUtils.setObserver(new HtmlObserver() {
            @Override
            public void onFinish(String data) {
                System.out.println(data);
                Tag root = new Tag();
                try {
                    root.decodeFromHtml(data, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                htmlUtils.finish();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                htmlUtils.finish();
            }
        });
//        htmlUtils.getHtml("https://3344mf.com/tupianqu/katong/");
        htmlUtils.getHtml("http://www.baidu.com");
    }
}
