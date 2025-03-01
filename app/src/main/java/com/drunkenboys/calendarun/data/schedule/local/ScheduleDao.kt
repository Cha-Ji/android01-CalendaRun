package com.drunkenboys.calendarun.data.schedule.local

import androidx.room.*
import com.drunkenboys.calendarun.data.schedule.entity.Schedule

@Dao
interface ScheduleDao {

    @Insert
    suspend fun insertSchedule(schedule: Schedule)

    @Query("SELECT * FROM `schedule`")
    suspend fun fetchAllSchedule(): List<Schedule>

    @Query("SELECT * FROM `schedule` WHERE id == :id")
    suspend fun fetchSchedule(id: Int): Schedule

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

}
