package com.ievolutioned.pxform.database;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 */
public class Values extends DBTableBase implements Parcelable {
    private long   id        ;
    private String key       ;
    private String result    ;
    private int    key_parent;
    private int    level     ;
    private long   form_id   ;

    public long   getId       () { return id         ; }
    public String getKey      () { return key        ; }
    public String getValue    () { return result     ; }
    public int    getKeyParent() { return key_parent ; }
    public int    getLevel    () { return level      ; }
    public long   getFormID   () { return form_id    ; }

    public void setId       (long   value) { id         = value; }
    public void setKey      (String value) { key        = value; }
    public void setValue    (String value) { result     = value; }
    public void setKeyParent(int    value) { key_parent = value; }
    public void setLevel    (int    value) { level      = value; }
    public void setFormID   (long   value) { form_id    = value; }

    public Values(){

    }

    /**
     * Parcelable helper class
     */
    public static final Parcelable.Creator<Values> CREATOR = new Parcelable.Creator<Values>(){
        public Values createFromParcel(Parcel in){return new Values(in);}
        public Values[] newArray(int size){return new Values[size];}
    };

    public Values(Cursor c){
        id         = c.getLong   (0);
        key        = c.getString (1);
        result     = c.getString (2);
        key_parent = c.getInt    (3);
        level      = c.getInt    (4);
        form_id    = c.getLong   (5);
    }

    public Values(Parcel p){
        id         = p.readLong   ();
        key        = p.readString ();
        result     = p.readString ();
        key_parent = p.readInt    ();
        level      = p.readInt    ();
        form_id    = p.readLong   ();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel w, int flags) {
        w.writeLong  (id        );
        w.writeString(key       );
        w.writeString(result    );
        w.writeInt   (key_parent);
        w.writeInt   (level     );
        w.writeLong  (form_id   );
    }
}
