package org.openhab.binding.melcloud.json;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginClientRes {

    @SerializedName("ErrorId")
    @Expose
    private Object errorId;
    @SerializedName("ErrorMessage")
    @Expose
    private Object errorMessage;
    @SerializedName("LoginStatus")
    @Expose
    private Integer loginStatus;
    @SerializedName("UserId")
    @Expose
    private Integer userId;
    @SerializedName("RandomKey")
    @Expose
    private Object randomKey;
    @SerializedName("AppVersionAnnouncement")
    @Expose
    private Object appVersionAnnouncement;
    @SerializedName("LoginData")
    @Expose
    private LoginData loginData;
    @SerializedName("ListPendingInvite")
    @Expose
    private List<Object> listPendingInvite = null;
    @SerializedName("ListOwnershipChangeRequest")
    @Expose
    private List<Object> listOwnershipChangeRequest = null;
    @SerializedName("ListPendingAnnouncement")
    @Expose
    private List<Object> listPendingAnnouncement = null;
    @SerializedName("LoginMinutes")
    @Expose
    private Integer loginMinutes;
    @SerializedName("LoginAttempts")
    @Expose
    private Integer loginAttempts;

    public Object getErrorId() {
        return errorId;
    }

    public void setErrorId(Object errorId) {
        this.errorId = errorId;
    }

    public Object getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(Object errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(Integer loginStatus) {
        this.loginStatus = loginStatus;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Object getRandomKey() {
        return randomKey;
    }

    public void setRandomKey(Object randomKey) {
        this.randomKey = randomKey;
    }

    public Object getAppVersionAnnouncement() {
        return appVersionAnnouncement;
    }

    public void setAppVersionAnnouncement(Object appVersionAnnouncement) {
        this.appVersionAnnouncement = appVersionAnnouncement;
    }

    public LoginData getLoginData() {
        return loginData;
    }

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }

    public List<Object> getListPendingInvite() {
        return listPendingInvite;
    }

    public void setListPendingInvite(List<Object> listPendingInvite) {
        this.listPendingInvite = listPendingInvite;
    }

    public List<Object> getListOwnershipChangeRequest() {
        return listOwnershipChangeRequest;
    }

    public void setListOwnershipChangeRequest(List<Object> listOwnershipChangeRequest) {
        this.listOwnershipChangeRequest = listOwnershipChangeRequest;
    }

    public List<Object> getListPendingAnnouncement() {
        return listPendingAnnouncement;
    }

    public void setListPendingAnnouncement(List<Object> listPendingAnnouncement) {
        this.listPendingAnnouncement = listPendingAnnouncement;
    }

    public Integer getLoginMinutes() {
        return loginMinutes;
    }

    public void setLoginMinutes(Integer loginMinutes) {
        this.loginMinutes = loginMinutes;
    }

    public Integer getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(Integer loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

}