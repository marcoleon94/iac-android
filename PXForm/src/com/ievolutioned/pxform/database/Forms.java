package com.ievolutioned.pxform.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pablo on 23/04/2015.
 */
public class Forms extends RealmObject{

    @PrimaryKey
    private int    id  ;
    private String path;
    private String name;

    public int    getId  ()            { return id  ; }
    public String getPath()            { return path; }
    public String getName()            { return name; }

	public void   setId  (int    value){ id   = value; }
	public void   setPath(String value){ path = value; }
	public void   setName(String value){ name = value; }
}
