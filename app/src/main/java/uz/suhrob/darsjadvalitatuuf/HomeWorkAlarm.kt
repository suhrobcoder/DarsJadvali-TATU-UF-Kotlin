package uz.suhrob.darsjadvalitatuuf

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.PowerManager
import androidx.core.app.NotificationCompat

/**
 * Created by User on 17.03.2020.
 */
class HomeWorkAlarm: BroadcastReceiver() {

    private val title = "title"
    private val content = "content"

    override fun onReceive(context: Context?, intent: Intent?) {
//        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
//        val wl = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HomeWork")
//        wl.acquire(600*1000)
//        val titleText = intent?.extras?.getString(title)
//        val contentText = intent?.extras?.getString(content)
//        val builder = NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.ic_notification)
//                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
//                .setContentTitle(titleText)
//                .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
//                .setAutoCancel(true)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//        val pendingIntent = PendingIntent.get // TODO: Finish this
//        builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Bajardim", pendingIntent)
    }
}