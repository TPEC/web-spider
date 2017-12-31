import src.utils.CsvWriter;
import src.utils.HtmlAccessor;
import src.utils.HtmlUtils;
import src.utils.command.CommandObserver;
import src.utils.command.CommandUtil;
import src.utils.observer.SimpleTag;
import src.utils.observer.UrlFilter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 沿着<a>标签递归爬
 */
public class IoSpider {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private static String startPageUrl = "https://www.baidu.com";
    private static long maxPages = 0;
    private static String filePath = "";
    private static UrlFilter urlFilter;

    public static void main(String[] args) throws IOException {
        CommandUtil commandUtil = new CommandUtil();
        commandUtil.register("u", String.class, (CommandObserver<String>) s -> startPageUrl = s, "start page url");
        commandUtil.register("o", String.class, (CommandObserver<String>) s -> filePath = s, "output file path");
        commandUtil.register("f", String.class, (CommandObserver<String>) s -> urlFilter = url -> url.contains(s), "url filter");
        commandUtil.register("m", Long.class, (CommandObserver<Long>) l -> maxPages = l, "max pages");
        commandUtil.registerError(s -> {
            System.out.println(s);
            commandUtil.printUsage();
        });

        if (!commandUtil.doCmd(args)) {
            return;
        }

        HtmlAccessor htmlAccessor = new HtmlAccessor();
        htmlAccessor.addAttrs("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
        htmlAccessor.setTimeout(10000, 10000);

        long startTime = System.currentTimeMillis();

        List<String> pageUrls = new ArrayList<>();
        List<String> viewedUrls = new ArrayList<>();

        if (!startPageUrl.startsWith("https://") && !startPageUrl.startsWith("http://")) {
            startPageUrl = "http://" + startPageUrl;
        }
        pageUrls.add(startPageUrl);

        CsvWriter<String> csvWriter = null;
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            csvWriter = new CsvWriter<>(file);
//            csvWriter.setCrlf("\r\n");
            csvWriter.setSpliter("\t");
        }

        while (pageUrls.size() > 0) {
            String url = pageUrls.get(0);
            viewedUrls.add(url);
            if (maxPages > 0 && viewedUrls.size() >= maxPages) {
                break;
            }
            String html = "";
            System.out.println(url);
            long time = System.currentTimeMillis();
            try {
                html = htmlAccessor.getHtmlSync(url);
            } catch (Exception e) {
//                e.printStackTrace();
            }
            pageUrls.remove(0);
            System.out.print(String.valueOf(viewedUrls.size()));
            System.out.print("\t" + String.valueOf(pageUrls.size()));
            System.out.print("\t" + String.valueOf(System.currentTimeMillis() - time) + "ms");
            System.out.print("\t" + String.valueOf((System.currentTimeMillis() - startTime) / 1000) + "s\n");
            if (html != null && !html.isEmpty()) {
                List<SimpleTag> tags = HtmlUtils.getSimpleTags(html, "a");
                for (SimpleTag t : tags) {
                    String u = t.getAttr("href");
                    if (u != null && !u.isEmpty()) {
                        if (HtmlUtils.isAnUrl(u)) {
                            String nextUrl = HtmlUtils.getAbsoluteUrl(u, url);
                            if (!viewedUrls.contains(nextUrl) && (urlFilter == null || urlFilter.filter(nextUrl))) {
                                if (csvWriter != null) {
                                    csvWriter.writeLine(dateFormat.format(new Date()),
                                            nextUrl,
                                            t.getContent());
                                }
                                pageUrls.add(nextUrl);
                            }
                        }
                    }
                }
            }
        }
    }
}
