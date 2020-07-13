package uz.suhrob.darsjadvalitatuuf.utils

import org.json.JSONException
import org.json.JSONObject
import uz.suhrob.darsjadvalitatuuf.models.Group
import uz.suhrob.darsjadvalitatuuf.models.Schedule
import uz.suhrob.darsjadvalitatuuf.models.Settings
import uz.suhrob.darsjadvalitatuuf.models.WeekDay

class JSONUtils {
    companion object {
        private const val keyGroupName = "name"
        private const val keySchedules = "schedules"
        private const val keyTitle = "title"
        private const val keyTeacher = "teacherName"
        private const val keyRoom = "roomName"
        private const val keyWeekDay = "weekDay"
        private const val keyOrder = "order"
        private const val keyLesson = "lessonType"
        private const val keyStartTime = "startTime"
        private const val keyLessonDuration = "lessonDuration"
        private const val keyBreakTime = "breakTime"

        fun getGroupFromJSON(json: String?): Group? {
            if (json == null) {
                return null
            }
            var group: Group? = null
            try {
                val groupJSONObject = JSONObject(json)
                val groupName = groupJSONObject.getString(keyGroupName)
                val schedulesJSONArray = groupJSONObject.getJSONArray(keySchedules)
                val schedules = ArrayList<Schedule>()
                for (i in 0 until schedulesJSONArray.length()) {
                    val scheduleJSON = schedulesJSONArray.getJSONObject(i)
                    val title = scheduleJSON.getString(keyTitle)
                    val teacher = if (scheduleJSON.has(keyTeacher)) scheduleJSON.getString(keyTeacher) else ""
                    val room = if (scheduleJSON.has(keyRoom)) scheduleJSON.getString(keyRoom) else ""
                    val weekDay = scheduleJSON.getString(keyWeekDay)
                    val order = scheduleJSON.getInt(keyOrder)
                    val lesson = if (scheduleJSON.has(keyLesson)) scheduleJSON.getString(keyLesson) else ""
                    schedules.add(Schedule(title, teacher, room, WeekDay.valueOf(weekDay), order, lesson))
                }
                group = Group(groupName, schedules)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return group
        }

        fun scheduleToJson(group: Group): String {
            val name = "\"$keyGroupName\":\"${group.name}\""
            var schedules = ""
            for (schedule in group.schedules) {
                schedules += "{\"$keyTitle\":\"${schedule.title}\"," +
                        "\"$keyTeacher\":\"${schedule.teacherName}\"," +
                        "\"$keyRoom\":\"${schedule.roomName}\"," +
                        "\"$keyWeekDay\":\"${schedule.weekDay.name}\"," +
                        "\"$keyOrder\":${schedule.order}," +
                        "\"$keyLesson\":\"${schedule.lessonType}\"}"
            }
            schedules = schedules.replace("}{", "},{")
            return "{$name," +
                    "\"$keySchedules\":[$schedules]}"
        }

        fun getSettingsFromJSON(json: String?): Settings? {
            if (json == null) {
                return null
            }
            var settings: Settings? = null
            try {
                val settingsJSONObject = JSONObject(json)
                val startTime = settingsJSONObject.getInt(keyStartTime)
                val lessonDuration = settingsJSONObject.getInt(keyLessonDuration)
                val breakTime = settingsJSONObject.getInt(keyBreakTime)
                settings = Settings(startTime, lessonDuration, breakTime)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return settings
        }

        fun settingsToJson(settings: Settings): String {
            return "{\"$keyStartTime\": ${settings.startTime}, " +
                    "\"$keyLessonDuration\": ${settings.lessonDuration}, " +
                    "\"$keyBreakTime\": ${settings.breakTime}}"
        }
    }
}