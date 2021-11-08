package com.drunkenboys.calendarun.ui.saveschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.di.CalendarId
import com.drunkenboys.calendarun.di.ScheduleId
import com.drunkenboys.calendarun.ui.saveschedule.model.BehaviorType
import com.drunkenboys.calendarun.ui.saveschedule.model.DateType
import com.drunkenboys.calendarun.util.SingleLiveEvent
import com.drunkenboys.calendarun.util.getOrThrow
import com.drunkenboys.ckscalendar.data.ScheduleColorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SaveScheduleViewModel @Inject constructor(
    @CalendarId private val calendarId: Int,
    @ScheduleId private val scheduleId: Int,
    private val calendarDataSource: CalendarLocalDataSource,
    private val scheduleDataSource: ScheduleLocalDataSource
) : ViewModel() {

    private lateinit var behaviorType: BehaviorType

    val title = MutableLiveData("")

    val startDate = MutableLiveData(Date())

    val endDate = MutableLiveData(Date())

    val memo = MutableLiveData("")

    private val _calendarName = MutableLiveData("test")
    val calendarName: LiveData<String> = _calendarName

    val notificationType = MutableLiveData(Schedule.NotificationType.TEN_MINUTES_AGO)

    private val _tagColor = MutableLiveData(ScheduleColorType.RED.color)
    val tagColor: LiveData<Int> = _tagColor

    private val _saveScheduleEvent = SingleLiveEvent<Schedule>()
    val saveScheduleEvent: LiveData<Schedule> = _saveScheduleEvent

    private val _pickDateTimeEvent = SingleLiveEvent<DateType>()
    val pickDateTimeEvent: LiveData<DateType> = _pickDateTimeEvent

    private val _pickNotificationTypeEvent = SingleLiveEvent<Unit>()
    val pickNotificationTypeEvent: LiveData<Unit> = _pickNotificationTypeEvent

    private val _isPickTagColorPopupVisible = MutableLiveData(false)
    val isPickTagColorPopupVisible: LiveData<Boolean> = _isPickTagColorPopupVisible

    fun init(behaviorType: BehaviorType) {
        initCalendarName()
        this.behaviorType = behaviorType

        if (behaviorType == BehaviorType.UPDATE) initData()
    }

    private fun initCalendarName() {
        viewModelScope.launch {
            _calendarName.value = calendarDataSource.fetchCalendar(calendarId).name
        }
    }

    private fun initData() {
        viewModelScope.launch {
            val schedule = scheduleDataSource.fetchSchedule(scheduleId)

            title.value = schedule.name
            startDate.value = schedule.startDate
            endDate.value = schedule.endDate
            memo.value = schedule.memo
            notificationType.value = schedule.notificationType
            _tagColor.value = schedule.color
        }
    }

    fun emitPickDateTimeEvent(dateType: DateType) {
        _pickDateTimeEvent.value = dateType
    }

    fun emitPickNotificationTypeEvent() {
        _pickNotificationTypeEvent.value = Unit
    }

    fun Date.toFormatString(): String {
        val calendar = Calendar.getInstance()
        calendar.time = this

        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "오전" else "오후"
        val dateFormat = SimpleDateFormat("MM월 dd일 $amPm hh:mm", Locale.getDefault())

        return dateFormat.format(this)
    }

    fun saveSchedule() {
        if (isInvalidInput()) return

        viewModelScope.launch {
            val schedule = createScheduleInstance()

            when (behaviorType) {
                BehaviorType.INSERT -> scheduleDataSource.insertSchedule(schedule)
                BehaviorType.UPDATE -> scheduleDataSource.updateSchedule(schedule)
            }
            _saveScheduleEvent.value = schedule
        }
    }

    private fun isInvalidInput(): Boolean {
        if (!this::behaviorType.isInitialized) return true
        if (title.value.isNullOrEmpty()) return true
        startDate.value ?: return true
        endDate.value ?: return true
        if (calendarName.value.isNullOrEmpty()) return true

        return false
    }

    private fun createScheduleInstance() = Schedule(
        id = scheduleId,
        calendarId = calendarId,
        name = title.getOrThrow(),
        startDate = startDate.getOrThrow(),
        endDate = endDate.getOrThrow(),
        notificationType = notificationType.getOrThrow(),
        memo = memo.getOrThrow(),
        color = tagColor.getOrThrow()
    )

    fun deleteSchedule() {
        if (scheduleId < 0) return

        viewModelScope.launch {
            val deleteSchedule = scheduleDataSource.fetchSchedule(scheduleId)
            scheduleDataSource.deleteSchedule(deleteSchedule)

            // TODO: 2021-11-08 delete event 발행
        }
    }

    fun togglePickTagColorPopup() {
        _isPickTagColorPopupVisible.value = !_isPickTagColorPopupVisible.getOrThrow()
    }

    fun pickTagColor(tagColor: Int) {
        _tagColor.value = tagColor
        _isPickTagColorPopupVisible.value = false
    }
}
