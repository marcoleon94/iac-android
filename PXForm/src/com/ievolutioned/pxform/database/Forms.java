package com.ievolutioned.pxform.database;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 */
public class Forms extends DBTableBase implements Parcelable {
    private long   id  ;
    private String path;
    private String name;

    public long   getId  ()            { return id  ; }
    public String getPath()            { return path; }
    public String getName()            { return name; }

	public void   setId  (long   value){ id   = value; }
	public void   setPath(String value){ path = value; }
	public void   setName(String value){ name = value; }

    public Forms(){

    }

    /**
     * Parcelable helper class
     */
    public static final Parcelable.Creator<Forms> CREATOR = new Parcelable.Creator<Forms>(){
        public Forms createFromParcel(Parcel in){return new Forms(in);}
        public Forms[] newArray(int size){return new Forms[size];}
    };

    public Forms(Cursor c){
		id   = c.getLong(0);
        name = c.getString(1);
		path = c.getString(2);
    }

    public Forms(Parcel p){
		id   = p.readLong();
		path = p.readString();
		name = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel w, int flags) {
		w.writeLong  (id  );
		w.writeString(path);
		w.writeString(name);
    }
}
