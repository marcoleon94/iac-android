package com.ievolutioned.iac.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Inquest entity class. Contains the main attributes for the result of user services
 * Created by Daniel on 27/04/2015.
 */
public class InquestEntity {

    /*
    {"id":34,"user_id":16,"inquest_id":9,"response":{"gained":"No","newJob":"","salary":"Si","values":"Nada","bossWhy":"mala onda","opinion":"Regular","benefits":"Regular","identity":"Nada","training":"Malo","gainedWhy":"","newSalary":"","salaryWhy":"","teammates":"Bueno","unionized":"Malo","workplace":"Regular","employeeID":"12345678","newCompany":"","opinionWhy":"","trainingOk":"Si","benefitsWhy":"","bossOpinion":"Malo","otherReason":"","trainingWhy":"por que no me enseÃ±aron nada","unionizedWhy":"Â¿Â¿ funcionas,no tengo idea tu si ?????","workPromises":"No","departmentWhy":"","reasonToLeave":"Matrimonio","trainingOkWhy":"","humanResources":"Regular","workInteresting":"Si","dateOfWithdrawal":"04/10/2015","trainingReceived":"Si","departmentOpinion":"Regular","humanResourcesWhy":"","salaryExpectations":"No","trainingReceivedWhy":"","humanResourcesService":"No","workInterestingAnswer":"123 Por ser asi","departmentWithMostRelation":"IngenierÃ­a"},"created_at":"2015-04-27T19:22:09.363Z","updated_at":"2015-04-27T19:22:09.363Z"}
     */
    private long id;
    @SerializedName("user_id")
    private long userId;
    @SerializedName("inquest_id")
    private long inquestId;
    private String response;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("updated_at")
    private Date updatedAt;

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

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
