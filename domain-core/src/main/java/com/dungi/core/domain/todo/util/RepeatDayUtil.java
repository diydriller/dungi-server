package com.dungi.core.domain.todo.util;

import com.dungi.core.domain.todo.model.RepeatDay;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class RepeatDayUtil {
    public static List<RepeatDay> fromBinaryString(String days) {
        List<RepeatDay> repeatDayList = new ArrayList<>();
        for (var dayOfWeek : DayOfWeek.values()) {
            int dayIdx = dayOfWeek.getValue() - 1;
            if (days.charAt(dayIdx) == '1') {
                var repeatDay = new RepeatDay(dayOfWeek);
                repeatDayList.add(repeatDay);
            }
        }
        return repeatDayList;
    }

    public static String toBinaryString(List<RepeatDay> repeatDayList) {
        StringBuilder sb = new StringBuilder("0000000");
        for (var repeatDay : repeatDayList) {
            int dayIdx = repeatDay.getDayOfWeek().getValue() - 1;
            sb.setCharAt(dayIdx, '1');
        }
        return sb.toString();
    }
}
