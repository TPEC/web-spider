package utils.observer;

public class FunctionHtmlObserver implements HtmlObserver {


    @Override
    public void onFinish(String data) {

    }

    @Override
    public void onError(Exception e) {

    }

    private static String removeAnnotation(String html) {
        if (html.charAt(html.length() - 1) == '/') {
            html += " ";
        }
        StringBuilder r = new StringBuilder();
        int index;
        while ((index = html.indexOf("<!")) >= 0) {
            int j = 0;
            for (int i = index + 1; i < html.length(); i++) {
                char c = html.charAt(i);
                if (c == '<') {
                    if(html.charAt(i+1)=='/'){

                    }else {
                        j++;
                    }
                }
            }
        }
        return r.toString();
    }
}
