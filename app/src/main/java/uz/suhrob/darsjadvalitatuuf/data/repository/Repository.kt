package uz.suhrob.darsjadvalitatuuf.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import uz.suhrob.darsjadvalitatuuf.data.database.dao.LessonDao
import uz.suhrob.darsjadvalitatuuf.data.pref.AppPref
import uz.suhrob.darsjadvalitatuuf.models.Group

class Repository(
    private val lessonDao: LessonDao,
    private val appPref: AppPref,
) {
    fun getGroupFlow(): Flow<Group?> =
        appPref.groupFlow.combine(lessonDao.getLessons()) { group, lessons ->
            if (group == null) null
            else Group(group, lessons)
        }

    suspend fun saveGroup(group: Group) {
        lessonDao.clear()
        lessonDao.insertLessons(group.lessons)
        appPref.saveGroupName(group.name)
    }
}