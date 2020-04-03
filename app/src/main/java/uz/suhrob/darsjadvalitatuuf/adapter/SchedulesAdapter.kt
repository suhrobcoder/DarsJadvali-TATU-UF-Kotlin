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
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.schedule_item.view.*
import uz.suhrob.darsjadvalitatuuf.R
import uz.suhrob.darsjadvalitatuuf.models.HomeWork
import uz.suhrob.darsjadvalitatuuf.models.Schedule
import uz.suhrob.darsjadvalitatuuf.storage.DBHelper
import uz.suhrob.darsjadvalitatuuf.storage.SharedPreferencesHelper
import java.util.*

/**
 * Created by User on 12.03.2020.
 */
class SchedulesAdapter(private val context: Context, private val schedules: List<Schedule>) : RecyclerView.Adapter<SchedulesAdapter.ViewHolder>() {

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
        private val homeworkContent: TextView = itemView.homework_content
        private val homeWorkPanelAddBtn: ImageView = itemView.home_work_panel_or_add_btn
        private val homeworkEditBtn: ImageView = itemView.homework_edit_btn
        private val homeworkParentLayout: ConstraintLayout = itemView.homework_parent_layout

        private var homeWorksOpened = false
        private val dbHelper: DBHelper = DBHelper(context)

        fun bind(schedule: Schedule) {
            val startTime = settings.startTime + (settings.lessonDuration+settings.breakTime)*adapterPosition
            val finishTime = startTime+settings.lessonDuration
            if (schedule.title.trim().isNotEmpty()) {
                scheduleStartTime.text = String.format(Locale.getDefault(), (if(startTime/60>9)"%d" else "0%d")+(if(startTime%60>9)":%d" else ":0%d"), startTime/60, startTime%60)
                scheduleFinishTime.text = String.format(Locale.getDefault(), (if(finishTime/60>9)"%d" else "0%d")+(if(finishTime%60>9)":%d" else ":0%d"), finishTime/60, finishTime%60)
                subjectName.text = schedule.title
                if (schedule.lessonType.trim().isNotEmpty()) {
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

                val homeWork = dbHelper.getWithSchedule(schedule)
                if (homeWork != null) {
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
                    homeworkContent.text = homeWork.content
                    homeworkEditBtn.setOnClickListener {
                        addEditHomeworkDialog(homeWork, homeWork.content, schedule)
                    }
                } else {
                    homeWorkPanelAddBtn.setImageResource(R.drawable.ic_add)
                    homeWorkPanelAddBtn.setOnClickListener {
                        addEditHomeworkDialog(null, "", schedule)
                    }
                }
            } else {
                itemView.visibility = View.INVISIBLE
            }
        }

        private fun addEditHomeworkDialog(homeWork: HomeWork?, oldContent: String, schedule: Schedule) {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.add_homework_dialog)
            dialog.setTitle("Uyga vazifa qo'shish")
            dialog.setCancelable(false)
            val okBtn = dialog.findViewById<Button>(R.id.add_homework_ok)
            val cancelBtn = dialog.findViewById<Button>(R.id.add_homework_cancel)
            val titleText = dialog.findViewById<TextView>(R.id.add_homework_title)
            val homeworkContent = dialog.findViewById<TextView>(R.id.add_homework_content)
            if (oldContent.isNotEmpty()) {
                homeworkContent.text = oldContent
            }
            titleText.text = String.format(context.resources.getString(R.string.add_homework_dialog_title), schedule.title)
            okBtn.setOnClickListener {
                val content = homeworkContent.text.toString()
                if (homeWork != null) {
                    DBHelper(context).update(homeWork, content)
                } else {
                    DBHelper(context).insert(HomeWork(0, content, content, schedule.weekDay, schedule.order))
                }
                notifyItemChanged(adapterPosition)
                dialog.dismiss()
            }
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}