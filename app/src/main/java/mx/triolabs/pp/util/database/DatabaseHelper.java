package mx.triolabs.pp.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import mx.triolabs.pp.util.database.models.BaseTableObject;
import mx.triolabs.pp.util.database.models.TestQuestions;

/**
 * Created by hugomedina on 12/7/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;
    private SQLiteDatabase database;

    private static final String DATABASE_NAME = "bluo_database";
    private static final int DATABASE_VERSION = 1;

    private static final String TEST_QUESTIONS_TABLE = "test_questions_table";

    public enum Tables {
        TEST_QUESTIONS_TABLE("test_questions_table");

        private final String text;

        Tables(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public static synchronized DatabaseHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    private BaseTableObject accessTable(Tables table){

        database = sInstance.getWritableDatabase();

        return new TestQuestions(database, table.toString());
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}