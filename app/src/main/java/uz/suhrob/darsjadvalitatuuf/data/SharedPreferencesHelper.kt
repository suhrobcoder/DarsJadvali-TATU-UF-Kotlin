package uz.suhrob.darsjadvalitatuuf.data

import android.content.Context
import uz.suhrob.darsjadvalitatuuf.models.Group
import uz.suhrob.darsjadvalitatuuf.models.Homework
import uz.suhrob.darsjadvalitatuuf.models.Schedule
import uz.suhrob.darsjadvalitatuuf.models.Settings
import uz.suhrob.darsjadvalitatuuf.utils.JSONUtils

class SharedPreferencesHelper(private val context: Context) {

    private val fileName = "schedule"
    private val preferences by lazy { context.getSharedPreferences(fileName, Context.MODE_PRIVATE) }
    private val prefEditor by lazy { context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit() }

    fun getSettings(): Settings {
        return JSONUtils.getSettingsFromJSON(preferences.getString("settings", ""))!!
    }

    fun setSettings(settings: Settings) {
        prefEditor.putString("settings", JSONUtils.settingsToJson(settings)).apply()
    }

    fun getSettingsString(): String {
        return preferences.getString("settings", "") ?: ""
    }

    fun getGroup(): String {
        return preferences.getString("group", "")!!
    }

    fun setGroup(group: String) {
        prefEditor.putString("group", group).apply()
    }

    fun scheduleLoaded(): Boolean {
        return preferences.getBoolean("loaded", false)
    }

    fun setScheduleLoaded(isLoaded: Boolean) {
        prefEditor.putBoolean("loaded", isLoaded).apply()
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
        return JSONUtils.scheduleToJson(getSchedule())
    }

    fun getHomeworkNotify(): Int {
        return preferences.getInt("homework_notify", 1)
    }

    fun setHomeworkNotify(n: Int?) {
        if (n != null) {
            prefEditor.putInt("homework_notify", n).apply()
        }
    }

    fun setHomeworkNotifyTime(hour: Int, minute: Int) {
        prefEditor.putInt("homework_notify_time_hour", hour).apply()
        prefEditor.putInt("homework_notify_time_minute", minute).apply()
    }

    fun getHomeworkNotifyTimeString(): String {
        val hour = preferences.getInt("homework_notify_time_hour", 15)
        val minute = preferences.getInt("homework_notify_time_minute", 0)
        return (if (hour > 9) "$hour" else "0$hour") + (if (minute > 9) ":$minute" else ":0$minute")
    }

    fun getHomeworkNotifyTime(): Int {
        return preferences.getInt("homework_notify_time_hour", 15)*60+preferences.getInt("homework_notify_time_minute", 0)
    }

    fun darkThemeEnabled(): Boolean {
        return preferences.getBoolean("enable_dark_theme", false)
    }

    fun setDarkThemeEnabled(enabled: Boolean) {
        prefEditor.putBoolean("enable_dark_theme", enabled).apply()
    }

    fun setThemeChanged() {
        prefEditor.putBoolean("theme_changed", true).apply()
    }

    fun isThemeChanged(): Boolean {
        val isChanged = preferences.getBoolean("theme_changed", false)
        if (isChanged) {
            prefEditor.putBoolean("theme_changed", false).apply()
            return true
        }
        return false
    }

    fun setNotifyDaysBefore(days: Int) {
        prefEditor.putInt("notify_days", days).apply()
    }

    fun getNotifyDaysBefore() = preferences.getInt("notify_days", 1)
}