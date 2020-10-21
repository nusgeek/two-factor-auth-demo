package ch.rasc.twofa.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CSVUtil {

    private static final char DEFAULT_SEPARATOR = ',';

    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR);
    }

    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
                sb.append(value).append(separators);
            }
        sb.substring(0, sb.length() - 1);
        sb.append("\n");
        w.append(sb.toString());
    }
}
