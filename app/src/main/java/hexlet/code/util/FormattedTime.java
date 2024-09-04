package hexlet.code.util;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class FormattedTime {
    public static String get(Timestamp time) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dtf.format(time.toLocalDateTime());
    }
}
