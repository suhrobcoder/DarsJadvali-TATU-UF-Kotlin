package uz.suhrob.darsjadvalitatuuf.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import uz.suhrob.darsjadvalitatuuf.R
import uz.suhrob.darsjadvalitatuuf.models.Group
import uz.suhrob.darsjadvalitatuuf.models.Schedule
import uz.suhrob.darsjadvalitatuuf.data.SharedPreferencesHelper
import uz.suhrob.darsjadvalitatuuf.utils.JSONUtils

class ViewPagerAdapter(val context: Context, private val schedules: List<List<Schedule>>, fm: FragmentManager): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val weekDays = context.resources.getStringArray(R.array.week_days)

    override fun getItem(position: Int): Fragment {
        val group = Group(SharedPreferencesHelper(context).getGroup(), schedules[position])
        return ScheduleFragment.newInstance(JSONUtils.scheduleToJson(group))
    }

    override fun getCount(): Int {
        return schedules.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return weekDays[position]
    }
}