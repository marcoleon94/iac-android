package com.ievolutioned.pxform.database;

import android.database.Cursor;
import android.os.Parcel;

public abstract class DBTableBase {
    protected DBTableBase(Parcel p){ }
    protected DBTableBase(Cursor c){ }
    protected DBTableBase(){ }
}