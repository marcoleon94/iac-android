package com.ievolutioned.iac.entity;

/**
 * DepartmentEntity class. Contains a setr of attributes for the user's department
 * Created by Daniel on 05/10/2015.
 */
public class DepartmentEntity {
    /**
     * Id of department
     */
    private int id;
    /**
     * Title of department
     */
    private String title;
    /**
     * Description of department
     */
    private String description;
    /**
     * Date of creation
     */
    private String createdAt;
    /**
     * Date of last update
     */
    private String updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
