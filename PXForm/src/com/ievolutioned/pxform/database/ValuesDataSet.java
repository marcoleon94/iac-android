package com.ievolutioned.pxform.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
  */
public class ValuesDataSet extends DBDataBase<Values> {
    public static final String TABLE_NAME = "FormValues";

	public static final String COLUMN_ID         ="_id";
	public static final String COLUMN_KEY        ="KeyValue";
	public static final String COLUMN_RESULT     ="ResultValue";
	public static final String COLUMN_KEY_PARENT ="KeyParent";
	public static final String COLUMN_LEVEL      ="Level";
	public static final String COLUMN_FORM_ID    ="FormID";

    public ValuesDataSet(Context context) {
        super(context);
    }


    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected Values createCursorT(Cursor c) {
        return new Values(c);
    }

    @Override
    protected Values createEmptyT() {
        return new Values();
    }

    /**
     * Select all values of a form
     * @param formID {@link com.ievolutioned.pxform.database.Forms Parent form ID}
     * @param level If the form is a child the level allow to have a repeated <b>parentKey</b>
     * @param parentKey The value represent the form and value parent of this form else is an empty string
     * @return A list of all values found
     */
    public List<Values> selectByFormIDLevelParentKey(final long formID,
                                                     final int level,
                                                     final String parentKey){
        SQLiteDatabase db = null;
        List<Values> list = new ArrayList<Values>();
        Values pop;

        try {
            db = getReadableDatabase();
            Cursor c = //db.rawQuery("SELECT * FROM " + getTableName() + " WHERE name='?'", new String[]{name});
            db.query(
                    getTableName()
                    , null
                    , COLUMN_FORM_ID + "=?"
                            + " AND " + COLUMN_LEVEL + "=?"
                            + " AND " + COLUMN_KEY_PARENT + "=?"
                    , new String[] {
                            String.valueOf(formID)
                            , String.valueOf(level)
                            , String.valueOf(parentKey) }
                    , null
                    , null
                    , null
            );

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

    public List<Values> selectByFormID(final long formID) {
        SQLiteDatabase db = null;
        List<Values> list = new ArrayList<Values>();
        Values pop;

        try {
            db = getReadableDatabase();
            Cursor c = //db.rawQuery("SELECT * FROM " + getTableName() + " WHERE name='?'", new String[]{name});
                    db.query(
                            getTableName()
                            , null
                            , COLUMN_FORM_ID + "=?"
                            , new String[]{
                                    String.valueOf(formID)}
                            , null
                            , null
                            , null
                    );

            if (c.moveToFirst()) {
                do {
                    pop = createCursorT(c);
                    list.add(pop);
                } while (c.moveToNext());
            }

            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();

            db = null;
        }

        return list;
    }

    public long insert(long formId, int level, String key, String keyParent){
        ContentValues values = new ContentValues();
        values.put(COLUMN_FORM_ID, formId);
        values.put(COLUMN_LEVEL, level);
        values.put(COLUMN_KEY, key);
        values.put(COLUMN_KEY_PARENT, keyParent);
        return insert(values);
    }

    public boolean updateValue(Values item){
        SQLiteDatabase db = null;

        try{
            db = this.getWritableDatabase();

            //int i = db.update(getTableName(), fields, "_id=?",
            //        new String[] { String.valueOf(id) });

            //Cursor cursor =
                    //db.query(
                    //"UPDATE"
                    //getTableName(), new String[]{"MAX(_Id)"}, null, null, null, null, null);

            db.execSQL("UPDATE "
                    + getTableName()
                    + " SET "
                    + COLUMN_RESULT
                    + "='"
                    + item.getValue()
                    + "' WHERE _id="
                    + String.valueOf(item.getId())
            );

            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        } finally {
            if(db != null)
                db.close();

            db = null;
        }
    }


    // is not working ????????????????????
    private boolean update(Values item){
        ContentValues values = new ContentValues();
        values.put(COLUMN_FORM_ID, item.getFormID());
        values.put(COLUMN_LEVEL, item.getLevel());
        values.put(COLUMN_KEY, item.getKey());
        values.put(COLUMN_KEY_PARENT, item.getKeyParent());
        return super.update(item.getId(), values);
    }
}
