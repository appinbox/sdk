package com.appinbox.sdk.repo.dao

import androidx.room.*
import java.util.*

@Entity(tableName = "message")
@TypeConverters(Converters::class)
data class Message (
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "body")
    val body: String,
    @ColumnInfo(name = "sent_at")
    val sentAt: Date,
    @ColumnInfo(name = "read_at")
    val readAt: Date?
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}
