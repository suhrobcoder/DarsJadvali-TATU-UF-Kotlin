package uz.suhrob.darsjadvalitatuuf.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.suhrob.darsjadvalitatuuf.models.Lesson

@Dao
interface LessonDao {
    @Insert
    suspend fun insertLessons(lessons: List<Lesson>)

    @Query("SELECT * FROM Lesson")
    fun getLessons(): Flow<List<Lesson>>

    @Query("DELETE FROM Lesson")
    suspend fun clear()
}