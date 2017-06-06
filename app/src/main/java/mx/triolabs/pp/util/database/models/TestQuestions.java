package mx.triolabs.pp.util.database.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by hugomedina on 12/7/16.
 */

public class TestQuestions extends BaseTableObject<TestQuestions> {

    private static final String TEST_QUESTION_ID = "test_question_id";

    private String id;

    public TestQuestions(SQLiteDatabase database, String tableName) {
        super(database, tableName);
    }

    public TestQuestions(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<TestQuestions> getAll() {

        String[] cols = new String[] {TEST_QUESTION_ID};
        Cursor mCursor = database.query(true, tableName,cols,null
                , null, null, null, null, null);
        if (mCursor != null) {
            while (mCursor.moveToNext()){

                TestQuestions item = new TestQuestions();
                item.setId(mCursor.getString(mCursor.getColumnIndex(TEST_QUESTION_ID)));

                list.add(item);
            }

            mCursor.close();
        }

        return list;
    }

    @Override
    long insert(TestQuestions newObject) {

        ContentValues values = new ContentValues();
        values.put(TEST_QUESTION_ID, newObject.getId());

        return database.insert(tableName, null, values);

    }

    @Override
    public TestQuestions selectOne(String id) {
        return null;
    }
}
