package com.jong.msaboard.common.constants;

import java.time.format.DateTimeFormatter;

public final class DateTimeFormatters {

    public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern(DateTimePatterns.TIME);
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern(DateTimePatterns.DATE);
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern(DateTimePatterns.DATE_TIME);

}
