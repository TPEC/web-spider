package src.utils;

import src.utils.observer.HtmlObserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class HtmlAccessor implements Runnable {
    private boolean runFlag;
    private InputStream is;
    private HtmlObserver observer;

    private final Map<String, String> attrs = new HashMap<>();

    private int connectTimeout = 0;
    private int readTimeout = 0;

    private int BUF_SIZE = 8192;
    private StringBuilder result;

    private final List<HtmlQueueItem> htmlQueueItems = new ArrayList<>();

    public void setBUF_SIZE(int BUF_SIZE) {
        if (BUF_SIZE > 0) {
            this.BUF_SIZE = BUF_SIZE;
        }
    }

    public void finish() {
        if (runFlag) {
            runFlag = false;
            htmlQueueItems.clear();
        }
    }

    public void setTimeout(final int connectTimeout, final int readTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public void addAttrs(final String attr, final String value) {
        attrs.put(attr, value);
    }

    public String getHtmlSync(final String url) throws Exception {
        getHtmlCore(url);
        int count;
        while (true) {
            byte[] buf = new byte[BUF_SIZE];
            count = is.read(buf);
            if (count == -1) {
                is.close();
                is = null;
                break;
            } else if (count == 0) {
                is.close();
                throw new Exception("error");
            } else {
                result.append(bytes2String(buf, count));
            }
        }
        return result.toString();
    }

    public void getHtmlAsync(final String url, final HtmlObserver observer) {
        htmlQueueItems.add(new HtmlQueueItem(url, observer));
        if (!runFlag) {
            runFlag = true;
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    private void getHtmlCore(final String url) throws IOException {
        result = new StringBuilder();
        URL realUrl = new URL(url);
        URLConnection connection = realUrl.openConnection();
        for (Map.Entry<String, String> e : attrs.entrySet()) {
            connection.addRequestProperty(e.getKey(), e.getValue());
        }
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        connection.connect();
        is = connection.getInputStream();
    }


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
                        if (observer != null) {
                            observer.onError(e);
                        }
                    }
                    if (count == -1) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            if (observer != null) {
                                observer.onError(e);
                            }
                        }
                        is = null;
                        if (observer != null) {
                            observer.onFinish(result.toString());
                        }
                        break;
                    } else if (count == 0) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            if (observer != null) {
                                observer.onError(e);
                            }
                        }
                        if (observer != null) {
                            observer.onError(new Exception("Error"));
                        }
                        break;
                    } else {
                        result.append(bytes2String(buf, count));
                    }
                }
            } else {
                if (htmlQueueItems.isEmpty()) {
                    finish();
                } else {
                    String url = htmlQueueItems.get(0).url;
                    this.observer = htmlQueueItems.get(0).observer;
                    htmlQueueItems.remove(0);
                    try {
                        getHtmlCore(url);
                        continue;
                    } catch (IOException e) {
                        if (observer != null) {
                            observer.onError(e);
                        }
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

    private static String bytes2String(byte[] b, int length) {
        byte[] b2 = Arrays.copyOfRange(b, 0, length);
        return new String(b2);
    }

    private class HtmlQueueItem {
        String url;
        HtmlObserver observer;

        HtmlQueueItem(final String url, final HtmlObserver observer) {
            this.url = url;
            this.observer = observer;
        }
    }
}
