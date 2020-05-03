package uz.suhrob.darsjadvalitatuuf.adapter

import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.schedule_item.view.*
import uz.suhrob.darsjadvalitatuuf.receivers.HomeworkAlarm
import uz.suhrob.darsjadvalitatuuf.R
import uz.suhrob.darsjadvalitatuuf.models.Homework
import uz.suhrob.darsjadvalitatuuf.models.HomeworkNotify
import uz.suhrob.darsjadvalitatuuf.models.Schedule
import uz.suhrob.darsjadvalitatuuf.storage.DBHelper
import uz.suhrob.darsjadvalitatuuf.storage.SharedPreferencesHelper
import java.util.*

class SchedulesAdapter(private val context: Context, private val schedules: List<Schedule>?) : RecyclerView.Adapter<SchedulesAdapter.ViewHolder>() {

    val settings = SharedPreferencesHelper(context).getSettings()

    override fun getItemCount() = schedules?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule: Schedule? = schedules?.firstOrNull { it.order == position+1 }
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
        private val homeworkContent: TextView = itemView.homework_content
        private val homeWorkPanelAddBtn: ImageView = itemView.home_work_panel_or_add_btn
        private val homeworkEditBtn: ImageView = itemView.homework_edit_btn
        private val homeworkParentLayout: ConstraintLayout = itemView.homework_parent_layout

        private var homeWorksOpened = false
        private val dbHelper: DBHelper = DBHelper(context)

        fun bind(schedule: Schedule) {
            val startTime = settings.getStartTime(adapterPosition+1)
            val finishTime = startTime+settings.lessonDuration
            if (schedule.title.trim().isNotEmpty()) {
                scheduleStartTime.text = formattedTime(startTime)
                scheduleFinishTime.text = formattedTime(finishTime)
                subjectName.text = schedule.title
                if (schedule.lessonType.trim().isNotEmpty()) {
                    lessonType.text = schedule.lessonType
                } else {
                    lessonType.visibility = View.GONE
                }
                lessonRoom.text = context.resources.getString(R.string.room_name_text, schedule.roomName)
                teacherName.text = context.resources.getString(R.string.teacher_name_text, schedule.teacherName)
                if (schedule.roomName.isEmpty()) {
                    lessonRoom.visibility = View.GONE
                    iconLocation.visibility = View.GONE
                }
                if (schedule.teacherName.isEmpty()) {
                    teacherName.visibility = View.GONE
                    iconPerson.visibility = View.GONE
                }

                val homework = dbHelper.getHomeworkWithSchedule(schedule)
                if (homework != null) {
                    homeWorkPanelAddBtn.setImageResource(R.drawable.ic_arrow)
                    homeWorkPanelAddBtn.setOnClickListener {
                        if (homeWorksOpened) {
                            homeWorksOpened = false
                            homeWorkPanelAddBtn.animate().rotationBy(180F).rotation(0F).duration = 500
                            homeworkParentLayout.visibility = View.GONE
                        } else {
                            homeWorksOpened = true
                            homeWorkPanelAddBtn.animate().rotationBy(0F).rotation(180F).duration = 500
                            homeworkParentLayout.visibility = View.VISIBLE
                        }
                    }
                    homeworkContent.text = homework.content
                    homeworkEditBtn.setOnClickListener {
                        addEditHomeworkDialog(homework, schedule, homework.content)
                    }
                } else {
                    homeWorkPanelAddBtn.setImageResource(R.drawable.ic_add)
                    homeWorkPanelAddBtn.setOnClickListener {
                        addEditHomeworkDialog(null, schedule)
                    }
                }
            } else {
                itemView.visibility = View.INVISIBLE
            }
        }

        private fun addEditHomeworkDialog(homework: Homework?, schedule: Schedule, oldContent: String = "") {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.add_homework_dialog)
            dialog.setTitle(context.resources.getString(R.string.add_homework))
            dialog.setCancelable(true)
            val okBtn = dialog.findViewById<Button>(R.id.add_homework_ok)
            val cancelBtn = dialog.findViewById<Button>(R.id.add_homework_cancel)
            val titleText = dialog.findViewById<TextView>(R.id.add_homework_title)
            val homeworkContent = dialog.findViewById<TextView>(R.id.add_homework_content)
            if (oldContent.isNotEmpty()) {
                homeworkContent.text = oldContent
            }
            val calendar = Calendar.getInstance()
            val scheduleDay = schedule.weekDay.ordinal
            val today = calendar.get(Calendar.DAY_OF_WEEK)-2
            var deltaDays = scheduleDay-today
            if (today >= scheduleDay) {
                deltaDays += 7
            }
            calendar.timeInMillis += deltaDays*86400*1000
            titleText.text = String.format(context.resources.getString(R.string.add_homework_dialog_title), schedule.title, calendar.get(Calendar.DAY_OF_MONTH), if (calendar.get(Calendar.MONTH)+1>9) "${calendar.get(Calendar.MONTH)+1}" else "0${calendar.get(Calendar.MONTH)+1}")
            okBtn.setOnClickListener {
                val content = homeworkContent.text.toString()
                if (homework != null) {
                    if (content.isNotEmpty()) {
                        DBHelper(context).updateHomework(homework, content)
                    } else {
                        DBHelper(context).deleteHomework(homework.id)
                    }
                } else {
                    val homeworkId = dbHelper.insertHomework(Homework(0, content, schedule.weekDay, schedule.order))
                    if (homeworkId > 0) {
                        val homeworkNotify = HomeworkNotify(0, homeworkId, SharedPreferencesHelper(context).getHomeworkNotify())
                        calendar.timeInMillis = Calendar.getInstance().timeInMillis
                        val notifyTime = SharedPreferencesHelper(context).getHomeworkNotifyTime()
                        val notifyHour = notifyTime / 60
                        val notifyMinute = notifyTime % 60
                        var notifyBeforeDays = SharedPreferencesHelper(context).getHomeworkNotify()
                        if (deltaDays >= notifyBeforeDays) {
                            deltaDays -= notifyBeforeDays
                        } else {
                            notifyBeforeDays = deltaDays
                            deltaDays = 0
                        }
                        calendar.timeInMillis += (deltaDays)*86400*1000
                        while (calendar.timeInMillis < Calendar.getInstance().timeInMillis) {
                            calendar.timeInMillis += 86400*1000
                            notifyBeforeDays--
                        }
                        homeworkNotify.days = notifyBeforeDays
                        val notifyId = dbHelper.insertHomeworkNotify(homeworkNotify)
                        calendar.set(Calendar.MINUTE, notifyMinute)
                        calendar.set(Calendar.HOUR_OF_DAY, notifyHour)
                        Log.d("alarm_time", calendar.toString())
                        HomeworkAlarm().setAlarm(context, calendar.timeInMillis, notifyId)
                    }
                }
                notifyItemChanged(adapterPosition)
                dialog.dismiss()
            }
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        private fun formattedTime(time: Int): String {
            val hour = time / 60
            val minute = time % 60
            return if (hour > 9) {
                "$hour"
            } else {
                "0$hour"
            } + if (minute > 9) {
                ":$minute"
            } else {
                ":0$minute"
            }
        }
    }
}