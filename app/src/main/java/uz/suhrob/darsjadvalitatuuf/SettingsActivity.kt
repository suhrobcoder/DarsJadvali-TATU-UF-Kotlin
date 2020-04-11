package uz.suhrob.darsjadvalitatuuf

import android.content.Intent
import android.content.RestrictionEntry
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import uz.suhrob.darsjadvalitatuuf.storage.SharedPreferencesHelper

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (SharedPreferencesHelper(applicationContext).darkThemeEnabled()) {
            setTheme(R.style.PreferenceDarkTheme)
        } else {
            setTheme((R.style.PreferenceTheme))
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment(object : RestartActivity {
                    override fun restartActivity() {
                        startActivity(Intent(applicationContext, SettingsActivity::class.java))
                        finish()
                    }
                }))
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment(private val restartActivity: RestartActivity) : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

        private val enableNotifications = "enable_notifications"
        private val homeworkNotification = "homework_notify"
        private val enableDarkTheme = "enable_dark_theme"

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key.equals(enableNotifications)) {
                val enableNotifyPref = findPreference<SwitchPreference>(enableNotifications)
                val scheduleAlarm = ScheduleAlarm()
                val sharedPreferencesHelper = SharedPreferencesHelper(context!!)
                if (sharedPreferences != null && sharedPreferences.getBoolean(key, true)) {
                    scheduleAlarm.setAlarm(context!!, sharedPreferencesHelper.getScheduleString(), sharedPreferencesHelper.getSettingsString())
                } else {
                    scheduleAlarm.cancelAlarm(context!!)
                }
            } else if (key.equals(homeworkNotification)) {
                val homeworkNotificationPref = findPreference<ListPreference>(homeworkNotification)
                SharedPreferencesHelper(context!!).setHomeworkNotify(preferenceScreen.sharedPreferences.getString(homeworkNotification, "1")?.toInt())
                homeworkNotificationPref?.summary = String.format(
                        context!!.resources.getString(R.string.homework_notification_summary),
                        preferenceScreen.sharedPreferences.getString(homeworkNotification, "1"))
            } else if (key.equals(enableDarkTheme)) {
                val enableDarkThemePref = findPreference<SwitchPreference>(enableDarkTheme)
                SharedPreferencesHelper(context!!).setDarkThemeEnabled(preferenceScreen.sharedPreferences.getBoolean(enableDarkTheme, false))
                restartActivity.restartActivity()
            }
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

            val homeworkNotificationPref = findPreference<ListPreference>(homeworkNotification)
            homeworkNotificationPref?.summary = String.format(
                    context!!.resources.getString(R.string.homework_notification_summary),
                    preferenceScreen.sharedPreferences.getString(homeworkNotification, "1"))
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onDisplayPreferenceDialog(preference: Preference?) {
            val timePickerDialog = preference as? TimePreference
            if (timePickerDialog != null) {
                val dialogFragment = TimeDialogPrefCompat.newInstance(timePickerDialog.key, context?.let { SharedPreferencesHelper(it).getHomeworkNotifyTime() })
                dialogFragment.setTargetFragment(this, 0)
                dialogFragment.positiveResult = {
                    timePickerDialog.summary = "${dialogFragment.hour}:${dialogFragment.minute}"
                }
                if (fragmentManager != null) dialogFragment.show(fragmentManager!!, null)
            } else {
                super.onDisplayPreferenceDialog(preference)
            }
        }
    }

    interface RestartActivity {
        fun restartActivity()
    }
}