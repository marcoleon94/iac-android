package com.ievolutioned.pxform.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pablo on 23/04/2015.
 */
public class Values extends RealmObject{

    @PrimaryKey
    private int    id        ;
    private String key       ;
    private Object result    ;
    private int    key_parent;
    private int    level     ;
    private int    form_id   ;

    public int    getId       () { return id         ; }
    public String getKey      () { return key        ; }
    public Object getValue    () { return result     ; }
    public int    getKeyParent() { return key_parent ; }
    public int    getLevel    () { return level      ; }
    public int    getFormID   () { return form_id    ; }

    public void setId       (int    value) { id         = value; }
    public void setKey      (String value) { key        = value; }
    public void setValue    (Object value) { result     = value; }
    public void setKeyParent(int    value) { key_parent = value; }
    public void setLevel    (int    value) { level      = value; }
    public void setFormID   (int    value) { form_id    = value; }

}
