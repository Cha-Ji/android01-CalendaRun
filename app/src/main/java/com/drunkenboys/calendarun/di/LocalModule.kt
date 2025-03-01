package com.drunkenboys.calendarun.di

import android.content.Context
import androidx.room.Room
import com.drunkenboys.calendarun.data.room.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object LocalModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context) = Room.databaseBuilder(
        appContext,
        Database::class.java,
        "AppDatabase.db"
    ).build()

    @Provides
    fun provideCalendarDao(database: Database) = database.calendarDao()

    @Provides
    fun provideCheckPointDao(database: Database) = database.checkPointDao()

    @Provides
    fun provideScheduleDao(database: Database) = database.scheduleDao()
}
