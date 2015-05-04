package com.ievolutioned.pxform.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 */
public abstract class DBDataBase <T extends DBTableBase> extends SQLiteAssetHelper{
    private static final String DATABASE_NAME = "iac_pxform.db";
    private static final int DATABASE_VERSION = 1;

    public DBDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public abstract String getTableName();
    protected abstract T createCursorT(Cursor c);
    protected abstract T createEmptyT();

    /**
     * Count all values in a table
     * @return an integer with all count rows
     */
    public int countAll(){
        int id = 0;
        SQLiteDatabase db = null;

        try{
            db = this.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + getTableName(), null);

            if(c.moveToFirst())
                id = c.getInt(0);

            c.close();
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        } finally {
            if(db != null)
                db.close();

            db = null;
        }

        return id;
    }

    public boolean delete(long id){
        SQLiteDatabase db = null;
        final String qy = "DELETE FROM " + getTableName() + " WHERE _id = " + String.valueOf(id);

        try {
            db = getWritableDatabase();
            db.execSQL(qy);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(db != null)
                db.close();

            db = null;
        }
        return true;
    }

    /**
     * Delete all values from the table
     * @return true if success else false
     */
    public boolean deleteAll(){
        SQLiteDatabase db = null;
        final String qy = "DELETE FROM " + getTableName();

        try {
            db = getWritableDatabase();
            db.execSQL(qy);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(db != null)
                db.close();

            db = null;
        }
        return true;
    }

    protected long insert(ContentValues fields){
        long id = 0;
        SQLiteDatabase db = null;

        try{
            db = this.getWritableDatabase();
            db.insert(getTableName(), null, fields);

            Cursor cursor = db.query(getTableName(), new String[]{"MAX(_Id)"}, null, null, null, null, null);

            if(cursor.moveToFirst())
                id = cursor.getLong(0);

            cursor.close();
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        } finally {
            if(db != null)
                db.close();

            db = null;
        }
        return id;
    }

    protected boolean update(long id, ContentValues fields){
        SQLiteDatabase db = null;

        try{
            db = this.getWritableDatabase();

            int i = db.update(getTableName(), fields, "_id=?",
                    new String[] { String.valueOf(id) });

            return i > 0;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        } finally {
            if(db != null)
                db.close();

            db = null;
        }
    }

    /**
     * Select a value from an ID
     * @param id The unique ID of the value
     * @return the found value
     */
    public T selectByID(long id){
        SQLiteDatabase db = null;
        T pop = createEmptyT();

        try {
            db = getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM " + getTableName() + " WHERE _id=" + String.valueOf(id), null);

            if(c.moveToFirst())
                pop = createCursorT(c);

            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(db != null)
                db.close();

            db = null;
        }

        return pop;
    }

    /**
     * Select all values in the table
     * @return a list of all values found
     */
    public List<T> selectAll(){
        SQLiteDatabase db = null;
        List<T> list = new ArrayList<T>();
        T pop;

        try {
            db = getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM " + getTableName(), null);

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



    ///========================================================
    ///========================================================

    @SuppressLint("SdCardPath")
    private static String DB_FILEPATH = "/data/data/com.ievolutioned.iac/databases/" + DATABASE_NAME;

    /**
     * Copies the database file at the specified location over the current
     * internal application database.
     * */
    public boolean importDatabase() throws IOException {

        //String dbPath = //Environment.getExternalStorageDirectory().toString() + "/com.mobilepaymentspecialists/";

        //File oldDb = Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS
        //      + "/com.mobilepaymentspecialists.db")
        //      ;

        //Environment.DIRECTORY_DOWNLOADS

        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        close();
        File oldDb = new File("/storage/sdcard0/com.ievolutioned.iac.db");
        File newDb = new File(DB_FILEPATH);
        if (newDb.exists()) {
            FileUtils ffb = new FileUtils();
            ffb.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            getWritableDatabase().close();
            return true;
        }
        return false;
    }

    class FileUtils {
        /**
         * Creates the specified <code>toFile</code> as a byte for byte copy of the
         * <code>fromFile</code>. If <code>toFile</code> already exists, then it
         * will be replaced with a copy of <code>fromFile</code>. The name and path
         * of <code>toFile</code> will be that of <code>toFile</code>.<br/>
         * <br/>
         * <i> Note: <code>fromFile</code> and <code>toFile</code> will be closed by
         * this function.</i>
         *
         * @param fromFile
         *            - FileInputStream for the file to copy from.
         * @param toFile
         *            - FileInputStream for the file to copy to.
         */
        public void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
            FileChannel fromChannel = null;
            FileChannel toChannel = null;
            try {
                fromChannel = fromFile.getChannel();
                toChannel = toFile.getChannel();
                fromChannel.transferTo(0, fromChannel.size(), toChannel);
            } finally {
                try {
                    if (fromChannel != null) {
                        fromChannel.close();
                    }
                } finally {
                    if (toChannel != null) {
                        toChannel.close();
                    }
                }
            }
        }
    }
}
