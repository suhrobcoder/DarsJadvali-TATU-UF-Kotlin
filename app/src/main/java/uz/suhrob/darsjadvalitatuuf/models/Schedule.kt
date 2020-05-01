package uz.suhrob.darsjadvalitatuuf.models

data class Schedule(var title: String = "", var teacherName: String = "", var roomName: String = "",
                    var weekDay: WeekDay = WeekDay.MONDAY, var order: Int = 1, var lessonType: String = "")