package uz.suhrob.darsjadvalitatuuf.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import uz.suhrob.darsjadvalitatuuf.*
import uz.suhrob.darsjadvalitatuuf.receivers.ScheduleAlarm
import uz.suhrob.darsjadvalitatuuf.storage.SharedPreferencesHelper
import uz.suhrob.darsjadvalitatuuf.ui.settings.CustomListPrefDialogCompat
import uz.suhrob.darsjadvalitatuuf.ui.settings.CustomListPreference
import uz.suhrob.darsjadvalitatuuf.ui.settings.TimeDialogPrefCompat
import uz.suhrob.darsjadvalitatuuf.ui.settings.TimePreference

class SettingsFragment(private val _context: Context, private val restartActivity: RestartActivity) : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val enableNotifications = "enable_notifications"
    private val homeworkNotificationCustom = "homework_notify_custom"
    private val enableDarkTheme = "enable_dark_theme"
    private val timePreferenceKey = "pref_time_picker"
    private val sharedPreferencesHelper = SharedPreferencesHelper(_context)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key.equals(enableNotifications)) {
            findPreference<SwitchPreference>(enableNotifications)
            val scheduleAlarm = ScheduleAlarm()
            if (sharedPreferences != null && sharedPreferences.getBoolean(key, true)) {
                scheduleAlarm.setAlarm(_context, sharedPreferencesHelper.getScheduleString(), sharedPreferencesHelper.getSettingsString())
            } else {
                scheduleAlarm.cancelAlarm(_context)
            }
        } else if (key.equals(homeworkNotificationCustom)) {
            val homeworkNotificationPref = findPreference<ListPreference>(homeworkNotificationCustom)
            sharedPreferencesHelper.setHomeworkNotify(preferenceScreen.sharedPreferences.getString(homeworkNotificationCustom, "1")?.toInt())
            homeworkNotificationPref?.summary = String.format(
                    _context.resources.getString(R.string.homework_notification_summary),
                    preferenceScreen.sharedPreferences.getString(homeworkNotificationCustom, "1"))
        } else if (key.equals(enableDarkTheme)) {
            findPreference<SwitchPreference>(enableDarkTheme)
            sharedPreferencesHelper.setDarkThemeEnabled(preferenceScreen.sharedPreferences.getBoolean(enableDarkTheme, false))
            sharedPreferencesHelper.setThemeChanged()
            restartActivity.restartActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        val homeworkNotificationCustomPref = findPreference<ListPreference>(homeworkNotificationCustom)
        homeworkNotificationCustomPref?.summary = "${sharedPreferencesHelper.getNotifyDaysBefore()} kun oldin"
        val timePreference = findPreference<TimePreference>(timePreferenceKey)
        timePreference?.summary = sharedPreferencesHelper.getHomeworkNotifyTimeString()
        if (!sharedPreferencesHelper.scheduleLoaded()) {
            findPreference<SwitchPreference>(enableNotifications)?.isEnabled = false
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        when (preference) {
            is CustomListPreference -> {
                val customListPreference = preference as? CustomListPreference
                if (customListPreference != null) {
                    val dialogFragment = CustomListPrefDialogCompat.newInstance(_context, customListPreference.key, sharedPreferencesHelper.getNotifyDaysBefore())
                    dialogFragment.setTargetFragment(this, 0)
                    dialogFragment.positiveResult = {
                        customListPreference.summary = "${dialogFragment.days} kun oldin"
                        sharedPreferencesHelper.setNotifyDaysBefore(dialogFragment.days)
                    }
                    if (fragmentManager != null) {
                        dialogFragment.show(requireFragmentManager(), null)
                    }
                }
            }
            is TimePreference -> {
                val timePickerDialog = preference as? TimePreference
                if (timePickerDialog != null) {
                    val dialogFragment = TimeDialogPrefCompat.newInstance(timePickerDialog.key, sharedPreferencesHelper.getHomeworkNotifyTime())
                    dialogFragment.setTargetFragment(this, 0)
                    dialogFragment.positiveResult = {
                        timePickerDialog.summary = formattedTime(dialogFragment.hour, dialogFragment.minute)
                        sharedPreferencesHelper.setHomeworkNotifyTime(dialogFragment.hour, dialogFragment.minute)
                    }
                    dialogFragment.show(requireFragmentManager(), null)
                }
            }
            else -> super.onDisplayPreferenceDialog(preference)
        }
    }
    
    private fun formattedTime(hour: Int, minute: Int): String {
        return (if (hour > 9) "$hour" else "0$hour") + (if (minute > 9) ":$minute" else ":0$minute")
    }
}