package com.appinbox.sdk.util;

import android.text.format.DateUtils;

import java.util.Date;

public class DateUtil {
    public static String format(Date date) {
        long diff = new Date().getTime() - date.getTime();

        String timeString;
        if(diff < 2 * DateUtils.MINUTE_IN_MILLIS){
            timeString = "just now";
        } else if(diff < DateUtils.HOUR_IN_MILLIS) {
            timeString = ((int)(diff/DateUtils.MINUTE_IN_MILLIS))+" mins";
        } else if(diff < 2 * DateUtils.HOUR_IN_MILLIS) {
            timeString = ((int)(diff/DateUtils.HOUR_IN_MILLIS))+" hr";
        } else if(diff < DateUtils.DAY_IN_MILLIS) {
            timeString = ((int)(diff/DateUtils.HOUR_IN_MILLIS))+" hrs";
        } else if(diff < 2*DateUtils.DAY_IN_MILLIS) {
            timeString = ((int)(diff/DateUtils.DAY_IN_MILLIS))+" day";
        } else {
            timeString = ((int)(diff/DateUtils.DAY_IN_MILLIS))+" days";
        }
        return timeString;
    }
}
