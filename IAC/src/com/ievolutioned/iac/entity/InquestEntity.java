package com.ievolutioned.iac.entity;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * Inquest entity class. Contains the main attributes for the result of user services
 * Created by Daniel on 27/04/2015.
 */
public class InquestEntity {
    /**
     * Inquest entity ID
     */
    private long id;
    /**
     * User ID
     */
    @SerializedName("user_id")
    private long userId;
    /**
     * Inquest ID
     */
    @SerializedName("inquest_id")
    private long inquestId;
    /**
     * Response from service
     */
    private JsonElement response;
    /**
     * Date created at
     */
    @SerializedName("created_at")
    private String createdAt;
    /**
     * Date updated at
     */
    @SerializedName("updated_at")
    private String updatedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getInquestId() {
        return inquestId;
    }

    public void setInquestId(long inquestId) {
        this.inquestId = inquestId;
    }

    public JsonElement getResponse() {
        return response;
    }

    public void setResponse(JsonElement response) {
        this.response = response;
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

    public void setUpdatedAt(String
                                     updatedAt) {
        this.updatedAt = updatedAt;
    }
}
