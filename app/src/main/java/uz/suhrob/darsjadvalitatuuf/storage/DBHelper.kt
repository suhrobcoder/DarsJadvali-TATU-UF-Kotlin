package uz.suhrob.darsjadvalitatuuf.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import uz.suhrob.darsjadvalitatuuf.models.HomeWork
import uz.suhrob.darsjadvalitatuuf.models.WeekDay

/**
 * Created by User on 17.03.2020.
 */
class DBHelper(context: Context): SQLiteOpenHelper(context, dbName, null, 1) {
    companion object {
        private val dbName = "schedule.db"
        private val tbName = "homeworks"
        private val id = "_id"
        private val title = "title"
        private val weekDay = "weekday"
        private val order = "order"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $tbName ($id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$title TEXT, $weekDay TEXT, $order INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $tbName")
        onCreate(db)
    }

    fun insert(homeWork: HomeWork): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(title, homeWork.title)
        cv.put(weekDay, homeWork.weekDay.name)
        cv.put(order, homeWork.order)
        return db.insert(tbName, null, cv) > -1
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
                        WeekDay.valueOf(cursor.getString(cursor.getColumnIndex(weekDay))),
                        cursor.getInt(cursor.getColumnIndex(order))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

}
