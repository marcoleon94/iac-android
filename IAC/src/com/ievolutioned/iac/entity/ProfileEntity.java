package com.ievolutioned.iac.entity;

/**
 * Profile entity class. Contains a set of attributes for the user profile
 * <p/>
 * Created by Daniel on 18/09/2015.
 */
public class ProfileEntity {
    private String id;
    private String name;
    private String email;
    private String department;
    private String position;
    private String divp;
    private String site;
    private String type;
    private String dateAdmission;
    private String holidays;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDivp() {
        return divp;
    }

    public void setDivp(String divp) {
        this.divp = divp;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateAdmission() {
        return dateAdmission;
    }

    public void setDateAdmission(String dateAdmission) {
        this.dateAdmission = dateAdmission;
    }

    public String getHolidays() {
        return holidays;
    }

    public void setHolidays(String holidays) {
        this.holidays = holidays;
    }
}
