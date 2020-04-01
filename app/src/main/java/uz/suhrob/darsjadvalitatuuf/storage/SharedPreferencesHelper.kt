package uz.suhrob.darsjadvalitatuuf.storage

import android.content.Context
import com.google.gson.Gson
import uz.suhrob.darsjadvalitatuuf.models.Group
import uz.suhrob.darsjadvalitatuuf.models.Settings

/**
 * Created by User on 12.03.2020.
 */
class SharedPreferencesHelper(private val context: Context) {

    private val fileName = "schedule"

    fun getSettings(): Settings {
        val preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return Settings(
                preferences.getInt("start_time", 510),
                preferences.getInt("lesson_duration", 80),
                preferences.getInt("break", 10),
                preferences.getInt("big_break", 10)
        )
    }

    fun getGroup(): String {
        val preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return preferences.getString("group", "")!!
    }

    fun setGroup(group: String) {
        val editor = context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit()
        editor.putString("group", group)
        editor.apply()
    }

    fun setSchedule(group: Group) {
        val editor = context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit()
        editor.putString("schedule", Gson().toJson(group))
        editor.apply()
    }

    fun scheduleLoaded(): Boolean {
        val preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return preferences.getString("schedule", "")?.isNotEmpty()!! && getSchedule().name == getGroup()
    }

    fun getSchedule(): Group {
        val preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return Gson().fromJson(preferences.getString("schedule", ""), Group::class.java)
    }
}