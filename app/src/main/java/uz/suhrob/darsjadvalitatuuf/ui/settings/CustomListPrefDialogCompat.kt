package uz.suhrob.darsjadvalitatuuf.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.preference.PreferenceDialogFragmentCompat
import uz.suhrob.darsjadvalitatuuf.R

class CustomListPrefDialogCompat : PreferenceDialogFragmentCompat() {

    lateinit var positiveResult: () -> Unit
    var days: Int = 0
    lateinit var context1: Context

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            positiveResult()
        }
    }

    companion object {
        fun newInstance(context: Context, key: String, days: Int): CustomListPrefDialogCompat {
            val fragment = CustomListPrefDialogCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            fragment.days = days
            fragment.context1 = context
            return fragment
        }
    }

    override fun onBindDialogView(view: View?) {
        val daysBefore1 = view?.findViewById<RadioButton>(R.id.days_before_1)
        val daysBefore2 = view?.findViewById<RadioButton>(R.id.days_before_2)
        val daysBefore3 = view?.findViewById<RadioButton>(R.id.days_before_3)
        view?.findViewById<RadioGroup>(R.id.days_before_group)
        when (this.days) {
            1 -> daysBefore1?.isChecked = true
            2 -> daysBefore2?.isChecked = true
            3 -> daysBefore3?.isChecked = true
            else -> daysBefore1?.isChecked = true
        }
        daysBefore1?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                this.days = 1
            }
        }
        daysBefore2?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                this.days = 2
            }
        }
        daysBefore3?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                this.days = 3
            }
        }
    }
}