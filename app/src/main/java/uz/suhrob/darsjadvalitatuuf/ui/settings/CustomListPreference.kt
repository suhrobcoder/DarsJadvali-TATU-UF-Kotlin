package uz.suhrob.darsjadvalitatuuf.ui.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference
import uz.suhrob.darsjadvalitatuuf.R

class CustomListPreference(context: Context, attrs: AttributeSet) : ListPreference(context, attrs) {
    init {
        positiveButtonText = context.resources.getString(R.string.ok)
        negativeButtonText = context.resources.getString(R.string.cancel)
    }
}