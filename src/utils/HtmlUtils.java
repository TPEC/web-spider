package utils;

import utils.observer.HtmlObserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class HtmlUtils implements Runnable {
    private boolean runFlag;
    private InputStream is;
    private HtmlObserver observer;

    public HtmlUtils() {
        runFlag = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    public void setObserver(HtmlObserver observer) {
        this.observer = observer;
    }

    public void finish() {
        if (runFlag) {
            runFlag = false;
        }
    }

    public void getHtml(final String url) {
        result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
            connection.connect();
            is = connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int BUF_SIZE = 8192;
    private StringBuilder result;

    @Override
    public void run() {
        while (runFlag) {
            if (is != null) {
                int count = 0;
                while (true) {
                    byte[] buf = new byte[BUF_SIZE];
                    try {
                        count = is.read(buf);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (count == -1) {
                        if (observer != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            is = null;
                            observer.onFinish(result.toString());
                            break;
                        }
                    } else if (count == 0) {
                        if (observer != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            observer.onError(new Exception("Error"));
                            break;
                        }
                    } else {
                        result.append(bytel2string(buf, count));
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static char[] bytel2charl(byte[] b, int length) {
        char[] c = new char[length];
        for (int i = 0; i < length; i++) {
            c[i] = (char) b[i];
        }
        return c;
    }

    private static String bytel2string(byte[] b, int length) {
        byte[] b2 = Arrays.copyOfRange(b, 0, length);
        return new String(b2);
    }
}
