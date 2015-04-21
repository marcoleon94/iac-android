package com.ievolutioned.iac.entity;

import com.google.gson.annotations.SerializedName;

/**
 * User entity class. Determines the attributes to consider for a single User in the system
 *
 * Created by Daniel on 21/04/2015.
 */
public class UserEntity {

    private String status;

    @SerializedName("admin_token")
    private String adminToken;

    @SerializedName("admin_email")
    private String adminEmail;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminToken() {
        return adminToken;
    }

    public void setAdminToken(String adminToken) {
        this.adminToken = adminToken;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }
}
