package uz.suhrob.darsjadvalitatuuf.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.google.gson.Gson
import uz.suhrob.darsjadvalitatuuf.models.Group
import uz.suhrob.darsjadvalitatuuf.models.Schedule

/**
 * Created by User on 12.03.2020.
 */
class MyPagerAdapter(private val schedules: List<List<Schedule>>, fm: FragmentManager): FragmentStatePagerAdapter(fm) {

    private val weekDays = arrayOf("Dushanba", "Seshanba", "Chorshanba", "Payshanba", "Juma", "Shanba")

    override fun getItem(position: Int): Fragment {
        val group = Group("", schedules[position])
        return MyFragment.newInstance(Gson().toJson(group))
    }

    override fun getCount(): Int {
        return schedules.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return weekDays[position]
    }
}