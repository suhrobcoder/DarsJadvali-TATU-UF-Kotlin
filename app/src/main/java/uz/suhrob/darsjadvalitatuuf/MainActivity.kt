package uz.suhrob.darsjadvalitatuuf

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import uz.suhrob.darsjadvalitatuuf.adapter.MyPagerAdapter
import uz.suhrob.darsjadvalitatuuf.api.ApiHelper
import uz.suhrob.darsjadvalitatuuf.models.Group
import uz.suhrob.darsjadvalitatuuf.models.Schedule
import uz.suhrob.darsjadvalitatuuf.models.WeekDay
import uz.suhrob.darsjadvalitatuuf.storage.SharedPreferencesHelper
import java.util.*

class MainActivity : AppCompatActivity(), DataLoadInterface {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.elevation = 0F
        sharedPreferencesHelper = SharedPreferencesHelper(applicationContext)
        main_tab_layout.setupWithViewPager(main_viewpager)
        main_viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(main_tab_layout))

        main_retry_btn.setOnClickListener {
            loadData()
        }

        loadData()
    }

    override fun groupListLoaded(responseString: String?) {

    }

    override fun scheduleLoaded(group: Group, loadedFromInternet: Boolean) {
        val lists = ArrayList<List<Schedule>>()
        val mondayList = ArrayList<Schedule>()
        val tuesdayList = ArrayList<Schedule>()
        val wednesdayList = ArrayList<Schedule>()
        val thursdayList = ArrayList<Schedule>()
        val fridayList = ArrayList<Schedule>()
        val saturdayList = ArrayList<Schedule>()
        val loadedSchedules = group.schedules
        for (schedule in loadedSchedules) {
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
        val adapter = MyPagerAdapter(lists, supportFragmentManager)
        main_viewpager.adapter = adapter
        main_progressbar.visibility = View.GONE
        sharedPreferencesHelper.setSchedule(group)
        val alarm = ScheduleAlarm()
        if (loadedFromInternet) {
            alarm.cancelAlarm(applicationContext)
            alarm.setAlarm(applicationContext, Gson().toJson(group), Gson().toJson(sharedPreferencesHelper.getSettings()))
        }
        main_schedules_layout.visibility = View.VISIBLE
        supportActionBar?.title = resources.getString(R.string.app_name)+" "+sharedPreferencesHelper.getGroup()
        val calendar = Calendar.getInstance()
        val settings = sharedPreferencesHelper.getSettings()
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
            this.scheduleLoaded(sharedPreferencesHelper.getSchedule(), false)
            return
        }
        if (hasInternetConnection()) {
            if (sharedPreferencesHelper.getGroup().isNotEmpty()) {
                main_progressbar.visibility = View.VISIBLE
                ApiHelper().getSchedule(sharedPreferencesHelper.getGroup(), this)
            } else {
                startActivityForResult(Intent(applicationContext, SelectGroupActivity::class.java), 1)
            }
        } else {
            main_no_internet_layout.visibility = View.VISIBLE
        }
    }

    private fun hasInternetConnection(): Boolean {
        val result: Boolean
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.change_group_menu) {
            startActivityForResult(Intent(applicationContext, SelectGroupActivity::class.java), 1)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            data?.getStringExtra("result")?.let { sharedPreferencesHelper.setGroup(it) }
            loadData()
        }
    }
}
