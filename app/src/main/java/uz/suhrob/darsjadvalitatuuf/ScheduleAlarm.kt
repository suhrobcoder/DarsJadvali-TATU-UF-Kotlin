package uz.suhrob.darsjadvalitatuuf

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.gson.Gson
import uz.suhrob.darsjadvalitatuuf.models.*
import java.util.*

/**
 * Created by User on 13.03.2020.
 */
class ScheduleAlarm: BroadcastReceiver() {

    private val intentId = 1
    private val groupData = "group_data"
    private val settingsData = "settings_data"
    private val titleData = "title_data"
    private val roomData = "room_data"
    private val teacherData = "teacher_data"

    override fun onReceive(context: Context?, intent: Intent?) {
        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Next Lesson")
        val group = intent?.extras?.getString(groupData)
        val settings = intent?.extras?.getString(settingsData)
        val title = intent?.extras?.getString(titleData)
        val room = intent?.extras?.getString(roomData)
        val teacher = intent?.extras?.getString(teacherData)
        val notificationTitle = "Keyingi dars $title"
        var notificationContent = ""
        if (!room?.trim()?.isEmpty()!!) {
            notificationContent = if (room.contains("/")) {
                "Dars $room xonalarda."
            } else {
                "Dars $room-xonada."
            }
        }
        if (!teacher?.trim()?.isEmpty()!!) {
            notificationContent += " O'qituvchi $teacher"
        }
        val builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("schedule_notification", "Keyingi dars", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
            builder.setChannelId("schedule_notification")
        }
        notificationManager.notify(1, builder.build())
        setAlarm(context, group!!, settings!!)
        wl.release()
    }

    fun setAlarm(context: Context, group: String, settings: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ScheduleAlarm::class.java)
        val scheduleNotify = getNextSchedule(group, settings)
        Log.d("api", Gson().toJson(scheduleNotify))
        val schedule = scheduleNotify.schedule
        intent.putExtra(titleData, schedule?.title)
        intent.putExtra(roomData, schedule?.roomName)
        intent.putExtra(teacherData, schedule?.teacherName)
        intent.putExtra(settingsData, settings)
        intent.putExtra(groupData, group)
        var alarmTime = scheduleNotify.calendar?.timeInMillis
        if (alarmTime!! < Calendar.getInstance().timeInMillis) {
            alarmTime = Calendar.getInstance().timeInMillis+5000
        }
        val pendingIntent = PendingIntent.getBroadcast(context, intentId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
    }

    fun cancelAlarm(context: Context) {
        val intent = Intent(context, ScheduleAlarm::class.java)
        val sender = PendingIntent.getBroadcast(context, intentId, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }

    private fun getNextSchedule(group: String, settings1: String): ScheduleNotify {
        val calendar = Calendar.getInstance()
        val schedules = Gson().fromJson(group, Group::class.java).schedules
        val settings = Gson().fromJson(settings1, Settings::class.java)
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
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-2
        var todayIsSunday = false
        var nextLessonAfterDays = 0
        if (dayOfWeek == -1) {
            dayOfWeek = 0
            todayIsSunday = true
            nextLessonAfterDays = 1
        }
        var nowInMinutes = calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE)
        val lastLessonTime = settings.startTime - 10 + (settings.breakTime+settings.lessonDuration)*(lists[dayOfWeek].size-1)
        if (dayOfWeek == 5) {
            if (nowInMinutes > lastLessonTime) {
                dayOfWeek = 0
                nextLessonAfterDays = 2
                nowInMinutes = 0
            }
        } else if (todayIsSunday || nowInMinutes > lastLessonTime) {
                dayOfWeek++
                nextLessonAfterDays = 1
                nowInMinutes = 0
        }
        val scheduleNotify = ScheduleNotify(null, null)
        Log.d("alarm", "getnextschedule")
        for (schedule in lists[dayOfWeek]) {
            val thisLessonStartTime = settings.startTime - 10 + (settings.breakTime+settings.lessonDuration)*(schedule.order-1)
            Log.d("alarm", "$nowInMinutes : $thisLessonStartTime")
            if (nowInMinutes < thisLessonStartTime) {
                calendar.timeInMillis += nextLessonAfterDays*86400*1000
                calendar.set(Calendar.HOUR_OF_DAY, thisLessonStartTime/60)
                calendar.set(Calendar.MINUTE, thisLessonStartTime%60)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                scheduleNotify.calendar = calendar
                scheduleNotify.schedule = schedule
                break
            }
        }
        return scheduleNotify
    }
}