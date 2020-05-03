package uz.suhrob.darsjadvalitatuuf.ui.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import uz.suhrob.darsjadvalitatuuf.R

class TimePreference(context: Context, attr: AttributeSet): DialogPreference(context, attr) {
    init {
        positiveButtonText = context.resources.getString(R.string.ok)
        negativeButtonText = context.resources.getString(R.string.cancel)
    }
}