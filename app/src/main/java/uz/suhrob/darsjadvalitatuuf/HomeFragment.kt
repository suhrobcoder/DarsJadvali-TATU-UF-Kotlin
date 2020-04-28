package uz.suhrob.darsjadvalitatuuf

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_home.view.*
import uz.suhrob.darsjadvalitatuuf.adapter.ViewPagerAdapter
import uz.suhrob.darsjadvalitatuuf.models.Group
import uz.suhrob.darsjadvalitatuuf.models.Schedule
import uz.suhrob.darsjadvalitatuuf.models.Settings
import uz.suhrob.darsjadvalitatuuf.models.WeekDay
import uz.suhrob.darsjadvalitatuuf.storage.SharedPreferencesHelper
import uz.suhrob.darsjadvalitatuuf.utils.JSONUtils
import uz.suhrob.darsjadvalitatuuf.utils.NetworkUtils
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment(private val _context: Context, private var themeChanged: Boolean) : Fragment(), DataLoadInterface {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var main_tab_layout: TabLayout
    private lateinit var main_viewpager: ViewPager
    private lateinit var main_retry_btn: Button
    private lateinit var tablayout_bg: View
    private lateinit var main_progressbar: ProgressBar
    private lateinit var main_schedules_layout: LinearLayout
    private lateinit var main_no_internet_layout: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPreferencesHelper = SharedPreferencesHelper(_context)

        main_tab_layout = view.main_tab_layout
        main_viewpager = view.main_viewpager
        main_retry_btn = view.main_retry_btn
        tablayout_bg = view.tablayout_bg
        main_progressbar = view.main_progressbar
        main_schedules_layout = view.main_schedules_layout
        main_no_internet_layout = view.main_no_internet_layout

        main_tab_layout.setupWithViewPager(main_viewpager)
        main_viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(main_tab_layout))

        main_retry_btn.setOnClickListener {
            loadData()
        }

        loadData()
    }

    override fun groupListLoaded(responseString: String?) {

    }

    override fun scheduleLoaded(group: Group, settings: Settings, loadedFromInternet: Boolean) {
        val loadedSchedules = group.schedules
        val lists = divideSchedules(loadedSchedules)
        val adapter = ViewPagerAdapter(_context, lists, fragmentManager!!)
        main_viewpager.adapter = adapter
        tablayout_bg.visibility = View.VISIBLE
        main_tab_layout.visibility = View.VISIBLE
        main_tab_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.tablayout_anim))
        main_progressbar.visibility = View.GONE
        sharedPreferencesHelper.setSchedule(group)
        val alarm = ScheduleAlarm()
        if (loadedFromInternet) {
            sharedPreferencesHelper.setSettings(settings)
            alarm.cancelAlarm(_context)
            alarm.setAlarm(context!!, JSONUtils.scheduleToJson(group), JSONUtils.settingsToJson(settings))
        }
        main_schedules_layout.visibility = View.VISIBLE
        val calendar = Calendar.getInstance()
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-2
        var nowInMinutes = calendar.get(Calendar.HOUR_OF_DAY)*60+calendar.get(Calendar.MINUTE)
        if (dayOfWeek == -1) {
            dayOfWeek = 0
            nowInMinutes = 0
        }
        val lastLessonTime = settings.startTime + (settings.breakTime+settings.lessonDuration)*(lists[dayOfWeek].size)
        main_viewpager.currentItem = dayOfWeek + if (nowInMinutes > lastLessonTime) 1 else 0
    }


    private fun loadData() {
        main_schedules_layout.visibility = View.GONE
        main_no_internet_layout.visibility = View.GONE
        if (sharedPreferencesHelper.scheduleLoaded()) {
            this.scheduleLoaded(sharedPreferencesHelper.getSchedule(), sharedPreferencesHelper.getSettings(),false)
            return
        }
        if (hasInternetConnection()) {
            if (themeChanged) {
                themeChanged = false
                return
            }
            if (sharedPreferencesHelper.getGroup().isNotEmpty()) {
                main_progressbar.visibility = View.VISIBLE
                main_tab_layout.visibility = View.GONE
                tablayout_bg.visibility = View.GONE
                NetworkUtils().getSchedule(sharedPreferencesHelper.getGroup(), this)
            } else {
                startActivityForResult(Intent(context, SelectGroupActivity::class.java), 1)
            }
        } else {
            main_no_internet_layout.visibility = View.VISIBLE
        }
    }

    private fun hasInternetConnection(): Boolean {
        val result: Boolean
        val cm = _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = cm.activeNetwork ?: return false
            val actNw = cm.getNetworkCapabilities(networkCapabilities) ?: return false
            when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val netInfo = cm.activeNetworkInfo
            !(netInfo != null && netInfo.isConnected)
        }
        return result
    }

    private fun divideSchedules(schedules: List<Schedule>): List<List<Schedule>> {
        val lists = ArrayList<List<Schedule>>()
        val mondayList = ArrayList<Schedule>()
        val tuesdayList = ArrayList<Schedule>()
        val wednesdayList = ArrayList<Schedule>()
        val thursdayList = ArrayList<Schedule>()
        val fridayList = ArrayList<Schedule>()
        val saturdayList = ArrayList<Schedule>()
        for (schedule in schedules) {
            when (schedule.weekDay) {
                WeekDay.MONDAY -> mondayList.add(schedule)
                WeekDay.TUESDAY -> tuesdayList.add(schedule)
                WeekDay.WEDNESDAY -> wednesdayList.add(schedule)
                WeekDay.THURSDAY -> thursdayList.add(schedule)
                WeekDay.FRIDAY -> fridayList.add(schedule)
                WeekDay.SATURDAY -> saturdayList.add(schedule)
            }
        }
        lists.add(mondayList)
        lists.add(tuesdayList)
        lists.add(wednesdayList)
        lists.add(thursdayList)
        lists.add(fridayList)
        lists.add(saturdayList)
        return lists
    }
}
