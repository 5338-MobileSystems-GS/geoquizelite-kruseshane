package edu.gsu.csci5338.geoquizelite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static java.security.AccessController.getContext;

public class DBHandler extends SQLiteOpenHelper {
    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "quizStatus.db";
    public static final String TABLE_NAME = "questions";
    public static final String QUESTION = "question";
    public static final String ANSWERED = "answered";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
            + QUESTION + " INTEGER, " + ANSWERED + " BIT)";

    public DBHandler(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }


    public void fillTable() {
        ContentValues cv = new ContentValues();

        QuestionBank qBank = new QuestionBank();
        qBank.populateBank();

        for (Question q : qBank.getBank()) {
            cv.put(QUESTION, q.getQuestion());
            cv.put(ANSWERED, 0);
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(TABLE_NAME, null, cv);
        }

    }

    public int getRowsInTable() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCount= db.rawQuery("select count(*) from " + TABLE_NAME, null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        return count;
    }

    public boolean findCurrentQuiz() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCount = db.rawQuery("select count(*) from " + TABLE_NAME + " where answered = '" + 1 + "'", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean doesTableExist() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + TABLE_NAME + "'", null);
        if(cursor.getCount()>0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public void registerQuestionAnswered(Question question, int bit) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET ANSWERED = " + bit + " where QUESTION = " + question.getQuestion());
    }
}
