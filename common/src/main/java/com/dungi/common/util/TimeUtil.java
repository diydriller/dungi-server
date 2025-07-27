package com.dungi.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.temporal.ChronoField.DAY_OF_WEEK;

public class TimeUtil {

    public enum DAY {
        MON, TUE, WED, THU, FRI , SAT, SUN
    };

    public static final Long SSE_DURATION = 3600000L;

    public static String localDateTimeToTimeStr(LocalDateTime time){
        return String.join("/",
                String.valueOf(time.getMonthValue()),
                String.valueOf(time.getDayOfMonth()),
                String.valueOf(time.getHour()),
                String.valueOf(time.getMinute())
        );
    }

    public static LocalDateTime timeStrToLocalDateTime(String time){
        String[] timeArr = time.split("/");
        int year = Integer.parseInt(timeArr[0]);
        int month = Integer.parseInt(timeArr[1]);
        int day = Integer.parseInt(timeArr[2]);
        int hour = Integer.parseInt(timeArr[3]);
        int minutes = Integer.parseInt(timeArr[4]);
        return LocalDateTime.of(year, month, day, hour, minutes);
    }

    public static LocalDateTime timeStrToTodayLocalDateTime(String time){
        String[] timeArr = time.split("/");
        int hour = Integer.parseInt(timeArr[0]);
        int minutes = Integer.parseInt(timeArr[1]);
        return LocalDateTime.of(
                LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonthValue(),
                LocalDateTime.now().getDayOfMonth(),
                hour,
                minutes
        );
    }

    public static LocalDateTime startOfWeek(){
        LocalDate today = LocalDate.now();
        int day = today.get(DAY_OF_WEEK);
        return today.minusDays(day - 1).atStartOfDay();
    }

    public static LocalDateTime endOfWeek(){
        LocalDate today = LocalDate.now();
        int day = today.get(DAY_OF_WEEK);
        return today.plusDays(7 - day).atTime(LocalTime.MAX);
    }
}
