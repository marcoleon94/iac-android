package com.ievolutioned.pxform.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class FormsDataSet extends DBDataBase<Forms> {
    public static final String TABLE_NAME = "Forms";

    public static final String COLUMN_ID   = "_id";
    public static final String COLUMN_PATH = "Path";
    public static final String COLUMN_NAME = "Name";

    public FormsDataSet(Context context) {
        super(context);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected Forms createCursorT(Cursor c) {
        return new Forms(c);
    }

    @Override
    protected Forms createEmptyT() {
        return new Forms();
    }

    public List<Forms> selectByName(String name){
        SQLiteDatabase db = null;
        List<Forms> list = new ArrayList<Forms>();
        Forms pop;

        try {
            db = getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM " + getTableName() + " WHERE name=?", new String[]{ name });

            if(c.moveToFirst()){
                do{
                    pop = createCursorT(c);
                    list.add(pop);
                }while(c.moveToNext());
            }

            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(db != null)
                db.close();

            db = null;
        }

        return list;
    }

    public long insert(String path, String name){
        ContentValues c = new ContentValues();

        c.put(COLUMN_PATH, path);
        c.put(COLUMN_NAME, name);

        return super.insert(c);
    }
}
