package com.ievolutioned.iac.entity;

/**
 * Avatar class, contains a set of attributes that represents an Avatar object on the scope
 * <p/>
 * Created by Daniel on 18/09/2015.
 */
public class Avatar {
    /**
     * URL of image
     */
    private String url;
    /**
     * Standar size
     */
    private Avatar standard;
    /**
     * Thumbnail size
     */
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
