package uz.suhrob.darsjadvalitatuuf.models

data class Schedule(val title: String, val teacherName: String, val roomName: String,
                    val weekDay: WeekDay, val order: Int, val lessonType: String)