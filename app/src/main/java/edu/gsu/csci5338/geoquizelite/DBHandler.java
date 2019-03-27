package edu.gsu.csci5338.geoquizelite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "quizStatus.db";
    public static final String TABLE_NAME = "questions";
    public static final String QUESTION = "question";
    public static final String ANSWERED = "answered";
    public static final String USER_ANSWER = "user_answer";
    public static final String CORRECT = "correct";
    public static final String INCORRECT = "incorrect";
    public static final String CHEATED = "cheated";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
            + QUESTION + " INTEGER, " + ANSWERED + " BIT, " + USER_ANSWER + " BIT, " + CORRECT + " BIT, " +
            INCORRECT + " BIT, " + CHEATED + " BIT" + ")";

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
            cv.put(USER_ANSWER, "NULL");
            cv.put(CORRECT, "NULL");
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

    public void registerQuestionAnswered(Question question, int didAnswer, int userAnswer, int correct, int incorrect, int cheated) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET ANSWERED = " + didAnswer + " where QUESTION = " + question.getQuestion());
        db.execSQL("UPDATE " + TABLE_NAME + " SET USER_ANSWER = " + userAnswer + " where QUESTION = " + question.getQuestion());
        db.execSQL("UPDATE " + TABLE_NAME + " SET CORRECT = " + correct + " where QUESTION = " + question.getQuestion());
        db.execSQL("UPDATE " + TABLE_NAME + " SET INCORRECT = " + incorrect + " where QUESTION = " + question.getQuestion());
        db.execSQL("UPDATE " + TABLE_NAME + " SET CHEATED = " + cheated + " where QUESTION = " + question.getQuestion());
    }

    public void readTable() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                System.out.println(cursor.getInt(0) + " " + cursor.getInt(1) + " " + cursor.getString(2) +
                        " " + cursor.getString(3));
            } while(cursor.moveToNext());
        }

        cursor.close();
    }

    public int questionsAnswered() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            do {
                count = count + cursor.getInt(1);
            } while (cursor.moveToNext());
        }

        return count;
    }

    public int[] getQuizSummary() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int [] summary = new int[3];
        int correct = 0;
        int incorrect = 0;
        int cheated = 0;

        if (cursor.moveToFirst()) {
            do {
                correct = correct + cursor.getInt(3);
                incorrect = incorrect + cursor.getInt(4);
                cheated = cheated + cursor.getInt(5);
            } while (cursor.moveToNext());
        }

        summary[0] = correct;
        summary[1] = incorrect;
        summary[2] = cheated;

        return summary;
    }

    public void reset() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE " + TABLE_NAME);
        db.execSQL(CREATE_TABLE);
        fillTable();
    }
}
