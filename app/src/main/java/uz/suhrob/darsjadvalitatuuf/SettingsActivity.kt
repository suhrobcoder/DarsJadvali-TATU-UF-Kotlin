package uz.suhrob.darsjadvalitatuuf

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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

        private val enableNotifications = "enable_notifications"
        private val homeworkNotification = "homework_notify"

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key.equals(enableNotifications)) {
                val enableNotifyPref = findPreference<SwitchPreference>(enableNotifications)
                val scheduleAlarm = ScheduleAlarm()
                val sharedPreferencesHelper = SharedPreferencesHelper(context!!)
                if (sharedPreferences!!.getBoolean(enableNotifications, false)) {
                    scheduleAlarm.setAlarm(context!!, sharedPreferencesHelper.getScheduleString(), sharedPreferencesHelper.getSettingsString())
                } else {
                    scheduleAlarm.cancelAlarm(context!!)
                }
            } else if (key.equals(homeworkNotification)) {
                val homeworkNotificationPref = findPreference<ListPreference>(homeworkNotification)
                homeworkNotificationPref?.summary = String.format(
                        context!!.resources.getString(R.string.homework_notification_summary),
                        preferenceScreen.sharedPreferences.getString(homeworkNotification, "1"))
            }
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

            val enableNotifyPref = findPreference<SwitchPreference>(enableNotifications)
            val homeworkNotificationPref = findPreference<ListPreference>(homeworkNotification)
            homeworkNotificationPref?.summary = String.format(
                    context!!.resources.getString(R.string.homework_notification_summary),
                    preferenceScreen.sharedPreferences.getString(homeworkNotification, "1"))
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }
    }
}