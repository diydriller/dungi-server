package com.dungi.core.domain.todo.util;

import com.dungi.common.util.TimeUtil;
import com.dungi.core.domain.todo.model.RepeatDay;

import java.util.ArrayList;
import java.util.List;

public class RepeatDayUtil {
    public static List<RepeatDay> fromBinaryString(String days) {
        List<RepeatDay> repeatDayList = new ArrayList<>();
        for (TimeUtil.DAY day : TimeUtil.DAY.values()) {
            int dayNum = day.ordinal();
            if (days.charAt(dayNum) == '1') {
                var repeatDay = new RepeatDay(dayNum);
                repeatDayList.add(repeatDay);
            }
        }
        return repeatDayList;
    }

    public static String toBinaryString(List<RepeatDay> repeatDayList) {
        StringBuilder sb = new StringBuilder("0000000");
        for (RepeatDay repeatDay : repeatDayList) {
            sb.setCharAt(repeatDay.getDay(), '1');
        }
        return sb.toString();
    }
}
