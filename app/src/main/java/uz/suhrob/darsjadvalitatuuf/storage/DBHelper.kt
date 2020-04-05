package uz.suhrob.darsjadvalitatuuf.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import uz.suhrob.darsjadvalitatuuf.models.HomeWork
import uz.suhrob.darsjadvalitatuuf.models.HomeworkNotify
import uz.suhrob.darsjadvalitatuuf.models.Schedule
import uz.suhrob.darsjadvalitatuuf.models.WeekDay

/**
 * Created by User on 17.03.2020.
 */
class DBHelper(context: Context): SQLiteOpenHelper(context, dbName, null, 1) {
    companion object {
        private const val dbName = "schedule.db"

        private const val tbHomeworkName = "homeworks"
        private const val id = "_id"
        private const val title = "title"
        private const val content = "content"
        private const val weekDay = "weekday"
        private const val order = "order1"

        private const val tbNotifyName = "homework_notify"
        private const val homeworkId = "homework_id"
        private const val days = "days"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $tbHomeworkName ($id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$title TEXT, $content TEXT, $weekDay TEXT, $order INTEGER)")
        db?.execSQL("CREATE TABLE IF NOT EXISTS $tbNotifyName ($id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$homeworkId INTEGER, $days INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $tbHomeworkName")
        db?.execSQL("DROP TABLE IF EXISTS $tbNotifyName")
        onCreate(db)
    }

    fun insertHomework(homeWork: HomeWork): Long {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(title, homeWork.title)
        cv.put(content, homeWork.content)
        cv.put(weekDay, homeWork.weekDay.name)
        cv.put(order, homeWork.order)
        return db.insert(tbHomeworkName, null, cv)
    }

    fun updateHomework(homeWork: HomeWork, newContent: String) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(title, homeWork.title)
        cv.put(content, newContent)
        cv.put(weekDay, homeWork.weekDay.name)
        cv.put(order, homeWork.order)
        db.update(tbHomeworkName, cv, "$id=?", arrayOf(homeWork.id.toString()))
    }

    fun getHomeworkWithSchedule(schedule: Schedule): HomeWork? {
        val query = "SELECT * FROM $tbHomeworkName WHERE $weekDay='${schedule.weekDay.name}' AND $order=${schedule.order}"
        val cursor = this.writableDatabase.rawQuery(query, null)
        val homeWork: HomeWork? = if (cursor.moveToFirst()) {
            HomeWork(
                    cursor.getLong(cursor.getColumnIndex(id)),
                    cursor.getString(cursor.getColumnIndex(title)),
                    cursor.getString(cursor.getColumnIndex(content)),
                    WeekDay.valueOf(cursor.getString(cursor.getColumnIndex(weekDay))),
                    cursor.getInt(cursor.getColumnIndex(order)))
        } else {
            null
        }
        cursor.close()
        return homeWork
    }

    fun getHomeworkById(homeworkId: Long?): HomeWork? {
        val query = "SELECT * FROM $tbHomeworkName WHERE $id=$homeworkId"
        val cursor = this.writableDatabase.rawQuery(query, null)
        val homeWork: HomeWork? = if (cursor.moveToFirst()) {
            HomeWork(
                    cursor.getLong(cursor.getColumnIndex(id)),
                    cursor.getString(cursor.getColumnIndex(title)),
                    cursor.getString(cursor.getColumnIndex(content)),
                    WeekDay.valueOf(cursor.getString(cursor.getColumnIndex(weekDay))),
                    cursor.getInt(cursor.getColumnIndex(order)))
        } else {
            null
        }
        cursor.close()
        return homeWork
    }

    fun insertHomeworkNotify(homeworkNotify: HomeworkNotify): Int {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(homeworkId, homeworkNotify.homework_id)
        cv.put(days, homeworkNotify.days)
        return db.insert(tbNotifyName, null, cv).toInt()
    }

    fun getHomeworkNotifyById(notifyId: Int?): HomeworkNotify? {
        val query = "SELECT * FROM $tbNotifyName WHERE $id=$notifyId"
        val cursor = this.writableDatabase.rawQuery(query, null)
        val homeWorkNotify: HomeworkNotify? = if (cursor.moveToFirst()) {
            HomeworkNotify(
                    cursor.getLong(cursor.getColumnIndex(id)),
                    cursor.getLong(cursor.getColumnIndex(homeworkId)),
                    cursor.getInt(cursor.getColumnIndex(days)))
        } else {
            null
        }
        cursor.close()
        return homeWorkNotify
    }

    fun updateNotify(homeworkNotify: HomeworkNotify) {
        if (homeworkNotify.days == 0) {
            deleteNotify(homeworkNotify)
            return
        }
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(homeworkId, homeworkNotify.homework_id)
        cv.put(days, homeworkNotify.days)
        db.update(tbNotifyName, cv, "$id=?", arrayOf(homeworkNotify.id.toString()))
    }

    fun deleteNotify(homeworkNotify: HomeworkNotify) {
        val db = this.writableDatabase
        db.delete(tbNotifyName, "$id=?", arrayOf(homeworkNotify.id.toString()))
    }
}
