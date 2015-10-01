package com.ievolutioned.iac.entity;

import com.google.gson.annotations.SerializedName;

/**
 * User entity class. Determines the attributes to consider for a single User in the system
 * <p/>
 * Created by Daniel on 21/04/2015.
 */
public class UserEntity {

    /**
     * IAC ID
     */
    @SerializedName("amin_iac_id")
    private String iacId;

    /**
     * STATUS
     */
    private String status;

    /**
     * Admin Token
     */
    @SerializedName("admin_token")
    private String adminToken;

    /**
     * Email of user
     */
    @SerializedName("admin_email")
    private String adminEmail;

    /**
     * Rol of user
     */
    @SerializedName("admin_rol")
    private String adminRol;

    public String getIacId() {
        return iacId;
    }

    public void setIacId(String iacId) {
        this.iacId = iacId;
    }

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

    public String getAdminRol() {
        return adminRol;
    }

    public void setAdminRol(String adminRol) {
        this.adminRol = adminRol;
    }
}
