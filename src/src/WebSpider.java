package src;

import src.utils.HtmlAccessor;
import src.utils.observer.HtmlObserver;
import src.utils.observer.Tag;

public class WebSpider {
    public static void main(String[] args) {
        HtmlAccessor htmlAccessor = new HtmlAccessor();
        htmlAccessor.setObserver(new HtmlObserver() {
            @Override
            public void onFinish(String data) {
                System.out.println(data);
                Tag root = new Tag();
                try {
                    root.decodeFromHtml(data, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                htmlAccessor.finish();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                htmlAccessor.finish();
            }
        });
//        htmlAccessor.getHtml("https://3344mf.com/tupianqu/katong/");
        htmlAccessor.getHtml("http://www.baidu.com");
    }
}
