package com.ievolutioned.iac.model;

/**
 * Created by Daniel on 22/04/2015.
 */
public abstract class ResponseBase {
    public String msg;
    public Throwable e;

    public ResponseBase(String msg, Throwable e) {
        this.msg = msg;
        this.e = e;
    }
}
