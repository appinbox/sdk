package com.appinbox.sdk.util;

import android.text.format.DateUtils
import java.util.Date


object DateUtil {
    fun format(date: Date?): String {
        if (date == null) {
            return ""
        }
        val diff: Long = Date().time - date.time
        return when {
            diff < 2 * DateUtils.MINUTE_IN_MILLIS -> {
                "just now"
            }
            diff < DateUtils.HOUR_IN_MILLIS -> {
                (diff / DateUtils.MINUTE_IN_MILLIS).toInt().toString() + " mins"
            }
            diff < 2 * DateUtils.HOUR_IN_MILLIS -> {
                (diff / DateUtils.HOUR_IN_MILLIS).toInt().toString() + " hr"
            }
            diff < DateUtils.DAY_IN_MILLIS -> {
                (diff / DateUtils.HOUR_IN_MILLIS).toInt().toString() + " hrs"
            }
            diff < 2 * DateUtils.DAY_IN_MILLIS -> {
                (diff / DateUtils.DAY_IN_MILLIS).toInt().toString() + " day"
            }
            else -> {
                (diff / DateUtils.DAY_IN_MILLIS).toInt().toString() + " days"
            }
        }
    }
}
