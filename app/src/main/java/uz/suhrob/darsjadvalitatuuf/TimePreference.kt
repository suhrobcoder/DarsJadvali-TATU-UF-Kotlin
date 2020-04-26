package uz.suhrob.darsjadvalitatuuf

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import uz.suhrob.darsjadvalitatuuf.storage.SharedPreferencesHelper

class TimePreference(context: Context, attr: AttributeSet): DialogPreference(context, attr) {
    init {
        positiveButtonText = context.resources.getString(R.string.ok)
        negativeButtonText = context.resources.getString(R.string.cancel)
    }
}