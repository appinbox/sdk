package com.appinbox.sdk.repo.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.appinbox.sdk.util.SingletonHolder

@Database(entities = [Message::class], version = 1)
abstract class SdkDatabase: RoomDatabase(){
    abstract fun messageDao(): MessageDao
    companion object :SingletonHolder<SdkDatabase, Context>({
        Room.databaseBuilder(it.applicationContext,
            SdkDatabase::class.java, "app-inbox.db")
            .build()
    })
}