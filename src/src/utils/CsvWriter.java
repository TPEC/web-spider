package src.utils;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvWriter<T> {
    private FileWriter fw;

    private String spliter = ",";
    private String crlf = "\n";

    public void setSpliter(String spliter) {
        this.spliter = spliter;
    }

    public void setCrlf(String crlf) {
        this.crlf = crlf;
    }

    public CsvWriter(File file) throws IOException {
        fw = new FileWriter(file);
    }

    public int writeLine(T... objects) throws IOException {
        if (fw == null) {
            return -1;
        }
        if (objects != null && objects.length > 0) {
            for (int i = 0; i < objects.length; i++) {
                T o = objects[i];
                fw.write(o.toString());
                if (i != objects.length - 1) {
                    fw.write(spliter);
                }
            }
            fw.write(crlf);
            fw.flush();
        }
        return -1;
    }

    public void close() throws IOException {
        if (fw != null) {
            fw.close();
        }
    }
}
