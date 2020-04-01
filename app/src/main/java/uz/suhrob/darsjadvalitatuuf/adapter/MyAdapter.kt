package uz.suhrob.darsjadvalitatuuf.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.schedule_item.view.*
import uz.suhrob.darsjadvalitatuuf.R
import uz.suhrob.darsjadvalitatuuf.models.Schedule
import uz.suhrob.darsjadvalitatuuf.storage.SharedPreferencesHelper
import java.util.*

/**
 * Created by User on 12.03.2020.
 */
class MyAdapter(private val context: Context, private val schedules: List<Schedule>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    val settings = SharedPreferencesHelper(context).getSettings()

    override fun getItemCount(): Int {
        return schedules.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule: Schedule? = schedules.firstOrNull { it.order == position+1 }
        if (schedule != null) {
            holder.bind(schedule)
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val scheduleStartTime: TextView = itemView.item_schedule_start_time
        private val scheduleFinishTime: TextView = itemView.item_schedule_finish_time
        private val subjectName: TextView = itemView.item_subject_name
        private val lessonType: TextView = itemView.item_lesson_type
        private val lessonRoom: TextView = itemView.item_lesson_room
        private val teacherName: TextView = itemView.item_teacher_name
        private val iconLocation: ImageView = itemView.item_icon_location
        private val iconPerson: ImageView = itemView.item_icon_person

        fun bind(schedule: Schedule) {
            val startTime = settings.startTime + (settings.lessonDuration+settings.breakTime)*adapterPosition
            val finishTime = startTime+settings.lessonDuration
            if (!schedule.title.trim().isEmpty()) {
                scheduleStartTime.text = String.format(Locale.getDefault(), (if(startTime/60>9)"%d" else "0%d")+(if(startTime%60>9)":%d" else ":0%d"), startTime/60, startTime%60)
                scheduleFinishTime.text = String.format(Locale.getDefault(), (if(finishTime/60>9)"%d" else "0%d")+(if(finishTime%60>9)":%d" else ":0%d"), finishTime/60, finishTime%60)
                subjectName.text = schedule.title
                if (!schedule.lessonType.trim().isEmpty()) {
                    lessonType.text = schedule.lessonType
                } else {
                    lessonType.visibility = View.GONE
                }
                lessonRoom.text = context.resources.getString(R.string.room_name_text, schedule.roomName)
                teacherName.text = context.resources.getString(R.string.teacher_name_text, schedule.teacherName)
                Log.d("adapter", "xabar:" + schedule.roomName)
                if (schedule.roomName == null || schedule.roomName == "") {
                    lessonRoom.visibility = View.GONE
                    iconLocation.visibility = View.GONE
                }
                if (schedule.teacherName == null || schedule.teacherName == "") {
                    teacherName.visibility = View.GONE
                    iconPerson.visibility = View.GONE
                }
            } else {
                itemView.visibility = View.INVISIBLE
            }
        }
    }
}