package uz.suhrob.darsjadvalitatuuf.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import uz.suhrob.darsjadvalitatuuf.models.Group
import uz.suhrob.darsjadvalitatuuf.models.Homework
import uz.suhrob.darsjadvalitatuuf.models.Schedule
import uz.suhrob.darsjadvalitatuuf.models.Settings

class SharedPreferencesHelper(private val context: Context) {

    private val fileName = "schedule"
    private val preferences : SharedPreferences

    init {
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    fun getSettings(): Settings {
        return Settings(
                preferences.getInt("start_time", 510),
                preferences.getInt("lesson_duration", 80),
                preferences.getInt("break", 10),
                preferences.getInt("big_break", 10)
        )
    }

    fun getSettingsString(): String {
        return Gson().toJson(getSettings())
    }

    fun getGroup(): String {
        return preferences.getString("group", "")!!
    }

    fun setGroup(group: String) {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit()
                .putString("group", group)
                .apply()
    }

    fun scheduleLoaded(): Boolean {
        return preferences.getBoolean("loaded", false)
    }

    fun setScheduleLoaded(isLoaded: Boolean) {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit()
                .putBoolean("loaded", isLoaded)
                .apply()
    }

    fun setSchedule(group: Group) {
        DBHelper(context).insertSchedules(group.schedules)
        setScheduleLoaded(true)
    }

    fun getSchedule(): Group {
        return Group(getGroup(), DBHelper(context).getSchedules())
    }

    fun getScheduleByHomework(homework: Homework?): Schedule? {
        if (homework == null) {
            return null
        }
        val schedules = getSchedule().schedules
        for (schedule in schedules) {
            if (schedule.weekDay == homework.weekDay && schedule.order == homework.order) {
                return schedule
            }
        }
        return null
    }

    fun getScheduleString(): String {
        return Gson().toJson(getSchedule())
    }

    fun getHomeworkNotify(): Int {
        return preferences.getInt("homework_notify", 1)
    }

    fun setHomeworkNotify(n: Int?) {
        if (n != null) {
            context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit()
                    .putInt("homework_notify", n)
                    .apply()
        }
    }

    fun setHomeworkNotifyTime(hour: Int, minute: Int) {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit()
                .putInt("homework_notify_time_hour", hour)
                .apply()
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit()
                .putInt("homework_notify_time_minute", minute)
                .apply()
    }

    fun getHomeworkNotifyTime(): Int {
        return preferences.getInt("homework_notify_time_hour", 15)*60+preferences.getInt("homework_notify_time_minute", 0)
    }
}