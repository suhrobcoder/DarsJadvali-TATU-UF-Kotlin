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
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import uz.suhrob.darsjadvalitatuuf.models.HomeworkNotify
import uz.suhrob.darsjadvalitatuuf.storage.DBHelper
import uz.suhrob.darsjadvalitatuuf.storage.SharedPreferencesHelper
import java.util.*

class HomeworkAlarm: BroadcastReceiver() {

    private val notifyId = "notify_id"
    private val deleteNotify = "delete_notify"

    override fun onReceive(context: Context?, intent: Intent?) {
        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:NextLesson")
        wl.acquire(600*1000)
        val dbHelper = DBHelper(context)
        val homeworkDone = intent?.extras?.getBoolean(deleteNotify, false) ?: false
        Toast.makeText(context, homeworkDone.toString(), Toast.LENGTH_SHORT).show()
        val homeworkNotifyId = intent?.extras?.getInt(notifyId) ?: return
        Log.d("alarm_time", homeworkDone.toString())
        if (homeworkDone) {
            dbHelper.deleteNotify(HomeworkNotify(homeworkNotifyId.toLong(),0,0))
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                notificationManager.deleteNotificationChannel(homeworkNotifyId.toString())
//            } else {
//            }
            notificationManager.cancel(homeworkNotifyId)
        } else {
            val homeworkNotify = dbHelper.getHomeworkNotifyById(homeworkNotifyId) ?: return
            val homeWork = dbHelper.getHomeworkById(homeworkNotify.homework_id) ?: return
            val schedule = SharedPreferencesHelper(context).getScheduleByHomework(homeWork) ?: return
            val notificationTitle = schedule.title
            val notificationContent = "${homeworkNotify.days-1} kundan so'ng ${schedule.title} fanidan uyga vazifangiz bor\n${homeWork.content}"
            val builder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                    .setContentTitle(notificationTitle)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setStyle(NotificationCompat.BigTextStyle().bigText(notificationContent))
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val intent1 = Intent(context, HomeworkAlarm::class.java)
            intent1.putExtra(deleteNotify, true)
            intent1.putExtra(notifyId, homeworkNotifyId)
            val pIntent = PendingIntent.getBroadcast(context, homeworkNotifyId, intent1, PendingIntent.FLAG_UPDATE_CURRENT)
            builder.addAction(R.drawable.ic_menu, "Bajardim", pIntent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(homeworkNotifyId.toString(), "Uyga vazifa", NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(notificationChannel)
                builder.setChannelId(homeworkNotifyId.toString())
            }
            notificationManager.notify(homeworkNotifyId, builder.build())
            Toast.makeText(context, "Notification ketdi", Toast.LENGTH_SHORT).show()
            homeworkNotify.days -= 1
            dbHelper.updateNotify(homeworkNotify)
            if (homeworkNotify.days > 0) {
                setAlarm(context, Calendar.getInstance().timeInMillis+86400*1000, homeworkNotifyId)
            }
        }
        wl.release()
    }

    fun setAlarm(context: Context, timeInMillis: Long, homeworkNotifyId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HomeworkAlarm::class.java)
        intent.putExtra(notifyId, homeworkNotifyId);
        val pendingIntent = PendingIntent.getBroadcast(context, homeworkNotifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        Toast.makeText(context, ""+calendar.get(Calendar.DAY_OF_MONTH), Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(context: Context, homeworkNotifyId: Int) {
        val intent = Intent(context, HomeworkAlarm::class.java)
        val sender = PendingIntent.getBroadcast(context, homeworkNotifyId, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }
}