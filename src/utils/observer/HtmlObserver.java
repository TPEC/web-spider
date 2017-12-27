package utils.observer;

public interface HtmlObserver {
    void onFinish(String data);

    void onError(Exception e);
}
