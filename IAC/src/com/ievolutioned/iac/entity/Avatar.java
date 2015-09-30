package com.ievolutioned.iac.entity;

/**
 * Created by Daniel on 18/09/2015.
 */
public class Avatar {
    private String url;
    private Avatar standard;
    private Avatar thumbnail;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Avatar getStandar() {
        return standard;
    }

    public void setStandar(Avatar standard) {
        this.standard = standard;
    }

    public Avatar getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Avatar thumbnail) {
        this.thumbnail = thumbnail;
    }
}
