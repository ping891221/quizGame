/*package com.example.quit
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper (private val context: Context):
            SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION){
                //User table
    companion object{
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "data"
        private const val COLUMN_ID = "id"
        private const val COLUMN_ACCOUNT = "account"
        private const val COLUMN_PASSWORD = "password"
    }

    //Quiz 題目table
    object QuizTable{
        const val TABLE_NAME2 = "questions"
        const val COLUMN_ID2 = "id"
        const val COLUMN_QUESTION = "question"
        const val COLUMN_OPTION1 = "option1"
        const val COLUMN_OPTION2 = "option2"
        const val COLUMN_OPTION3 = "option3"
        const val COLUMN_CORRECT_ANSWER = "correct_answer"
    }
    //創User資料表
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE　TABLE $TABLE_NAME(" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_ACCOUNT TEXT, " +
                "$COLUMN_PASSWORD TEXT)")
        db?.execSQL(createTableQuery)
    //創Quiz資料表

        val createQuizTableQuery = ("CREATE TABLE ${QuizTable.TABLE_NAME2} (" +
                "${QuizTable.COLUMN_ID2} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${QuizTable.COLUMN_QUESTION} TEXT," +
                "${QuizTable.COLUMN_OPTION1} TEXT," +
                "${QuizTable.COLUMN_OPTION2} TEXT," +
                "${QuizTable.COLUMN_OPTION3} TEXT," +
                "${QuizTable.COLUMN_CORRECT_ANSWER} INTEGER)")
        db?.execSQL(createQuizTableQuery)
    }
    //當資料庫版本號改變時的應對
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        // Recreate tables
        onCreate(db)
    }
    //新增帳號密碼?我猜是要用在註冊那
    fun insertUser(account:String, password:String):Long {
        val values = ContentValues().apply{
            put(COLUMN_ACCOUNT, account)
            put(COLUMN_PASSWORD, password)
        }
        val db = writableDatabase
        return db.insert(TABLE_NAME, null, values)
    }
    //檢查這個用戶是否存在 我猜也是用在註冊的
    fun readUser(account:String, password:String):Boolean{
        val db = readableDatabase
        val selection = "$COLUMN_ACCOUNT = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(account, password)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

}*/