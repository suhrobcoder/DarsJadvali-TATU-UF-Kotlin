package uz.suhrob.darsjadvalitatuuf.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Lesson(
    val name: String,
    val type: String,
    val teacher: String,
    val room: String,
    val weekDay: WeekDay,
    val order: Int,
    val startTime: String,
    val endTime: String,
) {
    @PrimaryKey var id: Int = 0
}
