package uz.suhrob.darsjadvalitatuuf.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.suhrob.darsjadvalitatuuf.data.database.dao.LessonDao
import uz.suhrob.darsjadvalitatuuf.models.Lesson

@Database(
    entities = [Lesson::class],
    version = 1,
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getLessonDao(): LessonDao
}