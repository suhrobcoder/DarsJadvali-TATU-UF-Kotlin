package uz.suhrob.darsjadvalitatuuf.models

/**
 * Created by User on 11.03.2020.
 */
data class Schedule(val title: String, val teacherName: String, val roomName: String,
                    val weekDay: WeekDay, val order: Int, val lessonType: String)