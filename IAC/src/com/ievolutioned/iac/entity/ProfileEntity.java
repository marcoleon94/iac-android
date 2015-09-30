package com.ievolutioned.iac.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Profile entity class. Contains a set of attributes for the user profile
 * <p/>
 * Created by Daniel on 18/09/2015.
 */
public class ProfileEntity {
    private long id;
    private String name;
    @SerializedName("last_name")
    private String lastName;
    private String email;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    private String role;
    @SerializedName("token_access")
    private String tokenAccess;
    @SerializedName("password_introduce_token")
    private String passwordIntroduceToken;
    @SerializedName("password_introduce_sent_at")
    private String passwordIntroduceSentAt;
    @SerializedName("iac_id")
    private String iacId;
    private String divp;
    @SerializedName("site_id")
    private long siteId;
    private String type;
    private String department;
    private String position;
    @SerializedName("last_salary")
    private String lastSalary;
    @SerializedName("date_of_admission")
    private String dateAdmission;
    @SerializedName("date_of_birth")
    private String dateBirth;
    private boolean status;
    @SerializedName("status_forward")
    private boolean statusForward;
    private String holidays;
    private Avatar avatar;
    @SerializedName("avatar_cloudinary")
    private String avatarCloudinary;
    @SerializedName("terms_and_conditions")
    private boolean termsAndConditions;
    @SerializedName("date_accept")
    private String dateAccept;
    @SerializedName("department_id")
    private String departmentId;
    @SerializedName("employee_type_id")
    private long employeeTypeId;

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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTokenAccess() {
        return tokenAccess;
    }

    public void setTokenAccess(String tokenAccess) {
        this.tokenAccess = tokenAccess;
    }

    public String getPasswordIntroduceToken() {
        return passwordIntroduceToken;
    }

    public void setPasswordIntroduceToken(String passwordIntroduceToken) {
        this.passwordIntroduceToken = passwordIntroduceToken;
    }

    public String getPasswordIntroduceSentAt() {
        return passwordIntroduceSentAt;
    }

    public void setPasswordIntroduceSentAt(String passwordIntroduceSentAt) {
        this.passwordIntroduceSentAt = passwordIntroduceSentAt;
    }

    public String getIacId() {
        return iacId;
    }

    public void setIacId(String iacId) {
        this.iacId = iacId;
    }

    public String getDivp() {
        return divp;
    }

    public void setDivp(String divp) {
        this.divp = divp;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getLastSalary() {
        return lastSalary;
    }

    public void setLastSalary(String lastSalary) {
        this.lastSalary = lastSalary;
    }

    public String getDateAdmission() {
        return dateAdmission;
    }

    public void setDateAdmission(String dateAdmission) {
        this.dateAdmission = dateAdmission;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isStatusForward() {
        return statusForward;
    }

    public void setStatusForward(boolean statusForward) {
        this.statusForward = statusForward;
    }

    public String getHolidays() {
        return holidays;
    }

    public void setHolidays(String holidays) {
        this.holidays = holidays;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public String getAvatarCloudinary() {
        return avatarCloudinary;
    }

    public void setAvatarCloudinary(String avatarCloudinary) {
        this.avatarCloudinary = avatarCloudinary;
    }

    public boolean isTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(boolean termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public String getDateAccept() {
        return dateAccept;
    }

    public void setDateAccept(String dateAccept) {
        this.dateAccept = dateAccept;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public long getEmployeeTypeId() {
        return employeeTypeId;
    }

    public void setEmployeeTypeId(long employeeTypeId) {
        this.employeeTypeId = employeeTypeId;
    }
}
