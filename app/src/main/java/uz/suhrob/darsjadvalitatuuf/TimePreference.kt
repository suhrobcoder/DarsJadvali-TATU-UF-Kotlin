package uz.suhrob.darsjadvalitatuuf

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import uz.suhrob.darsjadvalitatuuf.storage.SharedPreferencesHelper

class TimePreference(context: Context, attr: AttributeSet): DialogPreference(context, attr) {
    private var sharedPreferencesHelper = SharedPreferencesHelper(context)

    init {
        positiveButtonText = context.resources.getString(R.string.ok)
        negativeButtonText = context.resources.getString(R.string.cancel)
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        sharedPreferencesHelper = SharedPreferencesHelper(context)
        return sharedPreferencesHelper.getHomeworkNotifyTime()
    }

    override fun getSummary(): CharSequence {
        return sharedPreferencesHelper.getHomeworkNotifyTimeString()
    }

}