package com.chenzc.codeflow.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class DateUtil {
    public static long getLastDayOfMonthTimestamp() {
        LocalDate currentDate = LocalDate.now();
        LocalDate lastDayOfMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth());
        // 计算自 Unix 纪元以来的天数
        return ChronoUnit.DAYS.between(LocalDate.ofEpochDay(0), lastDayOfMonth);
    }
}
