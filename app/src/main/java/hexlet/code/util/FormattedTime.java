package hexlet.code.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormattedTime {
    public static String format(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return time.format(formatter);
    }
}
