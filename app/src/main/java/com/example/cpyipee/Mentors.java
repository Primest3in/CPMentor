package com.example.cpyipee;

public class Mentors {
    String DownloadUrl, Email, Mentor, FullName, UserID, UserName;
    public Mentors() {

    }
    public String getMentor() {
        return Mentor;
    }
    public void setMentor(String mentor) {
        this.Mentor = mentor;
    }
    public String getDownloadUrl() {
        return DownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.DownloadUrl = downloadUrl;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        this.FullName = fullName;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        this.UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public Mentors(String downloadUrl, String email, String fullName, String mentor, String userID, String userName) {
        this.DownloadUrl = downloadUrl;
        this.Email = email;
        this.FullName = fullName;
        this.UserID = userID;
        this.UserName = userName;
        this.Mentor = mentor;
    }
}
