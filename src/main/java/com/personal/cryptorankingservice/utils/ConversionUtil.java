package com.personal.cryptorankingservice.utils;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ConversionUtil {
    public static String convertUnixTimeToDate(Long unixTime) {
        final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return Instant.ofEpochSecond(unixTime)
                .atZone(ZoneId.of("GMT"))
                .format(formatter);
    }

    public static double round(double value, int places) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
