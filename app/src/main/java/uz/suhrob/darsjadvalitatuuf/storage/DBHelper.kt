package uz.suhrob.darsjadvalitatuuf.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import uz.suhrob.darsjadvalitatuuf.models.HomeWork
import uz.suhrob.darsjadvalitatuuf.models.Schedule
import uz.suhrob.darsjadvalitatuuf.models.WeekDay

/**
 * Created by User on 17.03.2020.
 */
class DBHelper(context: Context): SQLiteOpenHelper(context, dbName, null, 1) {
    companion object {
        private const val dbName = "schedule.db"
        private const val tbName = "homeworks"
        private const val id = "_id"
        private const val title = "title"
        private const val content = "content"
        private const val weekDay = "weekday"
        private const val order = "order1"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $tbName ($id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$title TEXT, $content TEXT, $weekDay TEXT, $order INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $tbName")
        onCreate(db)
    }

    fun insert(homeWork: HomeWork): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(title, homeWork.title)
        cv.put(content, homeWork.content)
        cv.put(weekDay, homeWork.weekDay.name)
        cv.put(order, homeWork.order)
        return db.insert(tbName, null, cv) > -1
    }

    fun update(homeWork: HomeWork, newContent: String) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(title, homeWork.title)
        cv.put(content, newContent)
        cv.put(weekDay, homeWork.weekDay.name)
        cv.put(order, homeWork.order)
        db.update(tbName, cv, "$id=?", arrayOf(homeWork.id.toString()))
    }

    fun getAll(): List<HomeWork> {
        val db = this.writableDatabase
        val list = ArrayList<HomeWork>()
        val query = "SELECT * FROM $tbName"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                list.add(HomeWork(
                        cursor.getInt(cursor.getColumnIndex(id)),
                        cursor.getString(cursor.getColumnIndex(title)),
                        cursor.getString(cursor.getColumnIndex(content)),
                        WeekDay.valueOf(cursor.getString(cursor.getColumnIndex(weekDay))),
                        cursor.getInt(cursor.getColumnIndex(order))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getWithSchedule(schedule: Schedule): HomeWork? {
        val query = "SELECT * FROM $tbName WHERE $weekDay='${schedule.weekDay.name}' AND $order=${schedule.order}"
        val cursor = this.writableDatabase.rawQuery(query, null)
        val homeWork: HomeWork? = if (cursor.moveToFirst()) {
            HomeWork(
                    cursor.getInt(cursor.getColumnIndex(id)),
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

}
