package uz.suhrob.darsjadvalitatuuf.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import uz.suhrob.darsjadvalitatuuf.models.Homework
import uz.suhrob.darsjadvalitatuuf.models.HomeworkNotify
import uz.suhrob.darsjadvalitatuuf.models.Schedule
import uz.suhrob.darsjadvalitatuuf.models.WeekDay

class DBHelper(context: Context): SQLiteOpenHelper(context, dbName, null, 1) {
    companion object {
        private const val dbName = "schedule.db"

        // Homework table
        private const val tbHomeworkName = "homeworks"
        private const val id = "_id"
        private const val content = "content"
        private const val weekDay = "weekday" // +Schedule table
        private const val order = "order1" // +Schedule table

        // HomeworkNotify table
        private const val tbNotifyName = "homework_notify"
        private const val homeworkId = "homework_id"
        private const val days = "days"

        // Schedule table
        private const val tbScheduleName = "schedules"
        private const val title = "title"
        private const val teacherName = "teacher_name"
        private const val roomName = "room"
        private const val lessonType = "lesson_type"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $tbHomeworkName ($id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$content TEXT, $weekDay TEXT, $order INTEGER)")
        db?.execSQL("CREATE TABLE IF NOT EXISTS $tbNotifyName ($id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$homeworkId INTEGER, $days INTEGER)")
        db?.execSQL("CREATE TABLE IF NOT EXISTS $tbScheduleName ($title TEXT, $teacherName TEXT, " +
                "$roomName TEXT, $weekDay TEXT, $order INTEGER, $lessonType TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $tbHomeworkName")
        db?.execSQL("DROP TABLE IF EXISTS $tbNotifyName")
        db?.execSQL("DROP TABLE IF EXISTS $tbScheduleName")
        onCreate(db)
    }

    fun insertHomework(homework: Homework): Long {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(content, homework.content)
        cv.put(weekDay, homework.weekDay.name)
        cv.put(order, homework.order)
        Log.d("database_changes", "inserted homework $homework")
        return db.insert(tbHomeworkName, null, cv)
    }

    fun updateHomework(homework: Homework, newContent: String) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(content, newContent)
        cv.put(weekDay, homework.weekDay.name)
        cv.put(order, homework.order)
        Log.d("database_changes", "updated homework $newContent")
        db.update(tbHomeworkName, cv, "$id=?", arrayOf(homework.id.toString()))
    }

    fun deleteHomework(homeworkId: Long?) {
        val db = this.writableDatabase
        Log.d("database_changes", "deleted homework ${homeworkId.toString()}")
        db.delete(tbHomeworkName, "$id=?", arrayOf(homeworkId.toString()))
    }

    fun getHomeworkWithSchedule(schedule: Schedule): Homework? {
        val query = "SELECT * FROM $tbHomeworkName WHERE $weekDay='${schedule.weekDay.name}' AND $order=${schedule.order}"
        val cursor = this.writableDatabase.rawQuery(query, null)
        val homework: Homework? = if (cursor.moveToFirst()) {
            Homework(
                    cursor.getLong(cursor.getColumnIndex(id)),
                    cursor.getString(cursor.getColumnIndex(content)),
                    WeekDay.valueOf(cursor.getString(cursor.getColumnIndex(weekDay))),
                    cursor.getInt(cursor.getColumnIndex(order)))
        } else {
            null
        }
        cursor.close()
        return homework
    }

    fun getHomeworkById(homeworkId: Long?): Homework? {
        val query = "SELECT * FROM $tbHomeworkName WHERE $id=$homeworkId"
        val cursor = this.writableDatabase.rawQuery(query, null)
        val homework: Homework? = if (cursor.moveToFirst()) {
            Homework(
                    cursor.getLong(cursor.getColumnIndex(id)),
                    cursor.getString(cursor.getColumnIndex(content)),
                    WeekDay.valueOf(cursor.getString(cursor.getColumnIndex(weekDay))),
                    cursor.getInt(cursor.getColumnIndex(order)))
        } else {
            null
        }
        cursor.close()
        return homework
    }

    fun insertHomeworkNotify(homeworkNotify: HomeworkNotify): Int {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(homeworkId, homeworkNotify.homework_id)
        cv.put(days, homeworkNotify.days)
        Log.d("database_changes", "inserted homeworknotify $homeworkNotify")
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
        if (homeworkNotify.days <= 0) {
            deleteNotify(homeworkNotify)
            return
        }
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(homeworkId, homeworkNotify.homework_id)
        cv.put(days, homeworkNotify.days)
        Log.d("database_changes", "updated homeworknotify $homeworkNotify")
        db.update(tbNotifyName, cv, "$id=?", arrayOf(homeworkNotify.id.toString()))
    }

    fun deleteNotify(homeworkNotify: HomeworkNotify) {
        val db = this.writableDatabase
        Log.d("database_changes", "deleted homework $homeworkNotify")
        db.delete(tbNotifyName, "$id=?", arrayOf(homeworkNotify.id.toString()))
    }

    fun insertSchedules(schedules: List<Schedule>) {
        clearSchedules()
        val db = this.writableDatabase
        for (schedule in schedules) {
            if (schedule.title.isNotEmpty()) {
                val cv = ContentValues()
                cv.put(title, schedule.title)
                cv.put(teacherName, schedule.teacherName)
                cv.put(roomName, schedule.roomName)
                cv.put(weekDay, schedule.weekDay.name)
                cv.put(order, schedule.order)
                cv.put(lessonType, schedule.lessonType)
                db.insert(tbScheduleName, null, cv)
            }
        }
    }

    fun getSchedules(): List<Schedule> {
        val schedules = ArrayList<Schedule>()
        val cursor = this.writableDatabase.rawQuery("SELECT * FROM $tbScheduleName", null)
        if (cursor.moveToFirst()) {
            do {
                schedules.add(Schedule(
                        cursor.getString(cursor.getColumnIndex(title)),
                        cursor.getString(cursor.getColumnIndex(teacherName)) ?: "",
                        cursor.getString(cursor.getColumnIndex(roomName)) ?: "",
                        WeekDay.valueOf(cursor.getString(cursor.getColumnIndex(weekDay)) ?: ""),
                        cursor.getInt(cursor.getColumnIndex(order)),
                        cursor.getString(cursor.getColumnIndex(lessonType)) ?: ""
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return schedules
    }

    private fun clearSchedules() {
        this.writableDatabase.execSQL("DELETE FROM $tbScheduleName")
    }
}
