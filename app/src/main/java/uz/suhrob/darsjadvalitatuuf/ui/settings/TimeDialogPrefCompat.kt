package uz.suhrob.darsjadvalitatuuf.ui.settings

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.preference.PreferenceDialogFragmentCompat
import uz.suhrob.darsjadvalitatuuf.R
import uz.suhrob.darsjadvalitatuuf.data.SharedPreferencesHelper

class TimeDialogPrefCompat: PreferenceDialogFragmentCompat() {

    lateinit var positiveResult: ()->Unit
    private var timePicker: TimePicker? = null
    var hour = 0
    var minute = 0

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            positiveResult()
        }
    }

    companion object {
        fun newInstance(key: String, time: Int?): TimeDialogPrefCompat {
            val fragment = TimeDialogPrefCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            if (time != null) {
                fragment.hour = time / 60
                fragment.minute = time % 60
            }
            return fragment
        }
    }

    override fun onBindDialogView(view: View?) {
        timePicker = view?.findViewById(R.id.dialog_preference_time_picker)
        timePicker?.setIs24HourView(true)
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val time = sharedPreferencesHelper.getHomeworkNotifyTime()
        if (Build.VERSION.SDK_INT >= 23) {
            timePicker?.hour = time / 60
            timePicker?.minute = time % 60
        } else {
            timePicker?.currentHour = time / 60
            timePicker?.currentMinute = time % 60
        }
        timePicker?.setOnTimeChangedListener { _, hourOfDay, minute1 ->
            hour = hourOfDay
            minute = minute1
        }
    }
}