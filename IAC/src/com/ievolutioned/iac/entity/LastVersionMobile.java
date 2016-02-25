package com.ievolutioned.iac.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Daniel on 25/02/2016.
 */
public class LastVersionMobile {
    private int id;
    @SerializedName("version_android")
    private String versioAndroid;
    @SerializedName("description_android")
    private String descriptionAndroid;
    @SerializedName("url_android")
    private String urlAndroid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersioAndroid() {
        return versioAndroid;
    }

    public void setVersioAndroid(String versioAndroid) {
        this.versioAndroid = versioAndroid;
    }

    public String getDescriptionAndroid() {
        return descriptionAndroid;
    }

    public void setDescriptionAndroid(String descriptionAndroid) {
        this.descriptionAndroid = descriptionAndroid;
    }

    public String getUrlAndroid() {
        return urlAndroid;
    }

    public void setUrlAndroid(String urlAndroid) {
        this.urlAndroid = urlAndroid;
    }
}
