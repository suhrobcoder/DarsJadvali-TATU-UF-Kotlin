package uz.suhrob.darsjadvalitatuuf

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.ListPreference
import uz.suhrob.darsjadvalitatuuf.storage.SharedPreferencesHelper

class CustomListPreference(context: Context, attrs: AttributeSet) : ListPreference(context, attrs) {
    init {
        positiveButtonText = context.resources.getString(R.string.ok)
        negativeButtonText = context.resources.getString(R.string.cancel)
    }
}