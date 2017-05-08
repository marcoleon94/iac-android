package com.ievolutioned.iac.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Site for profile info.
 * Example
 * private String site":{
 * private String id":1,
 * private String name":"Name",
 * private String address1":"17 y 34",
 * private String address2":"Colonia Ampliación Morelos",
 * private String zip":"25217",
 * private String city":"Coahuila",
 * private String state":"Saltillo",
 * private String country":"Mexico",
 * private String lat":"25.401499",
 * private String lng":"-100.937883",
 * private String created_at":"2015-02-26T12:55:27.232-06:00",
 * private String updated_at":"2015-03-10T14:21:53.777-06:00",
 * private String typeSite":"",
 * private String idAssistant":626
 * }
 * <p>
 * Created by Daniel on 10/04/2017.
 */
public class Site {
    private long id;
    private String name;
    private String address1;
    private String address2;
    private String zip;
    private String city;
    private String state;
    private String country;
    private String lat;
    private String lng;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("type_site")
    private String typeSite;
    @SerializedName("id_assistant")
    private long idAssistant;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String created_at) {
        this.createdAt = created_at;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updated_at) {
        this.updatedAt = updated_at;
    }

    public String getTypeSite() {
        return typeSite;
    }

    public void setTypeSite(String typeSite) {
        this.typeSite = typeSite;
    }

    public long getIdAssistant() {
        return idAssistant;
    }

    public void setIdAssistant(long idAssistant) {
        this.idAssistant = idAssistant;
    }
}
