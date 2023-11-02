package com.example.cpyipee;

public class Comments {
    String Answer;
    String userId;
    String Date;
    String Name;
    String Image;
    String commentId;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Comments(String answer, String userId, String date, String name, String image, String commentId) {
        Answer = answer;
        this.userId = userId;
        Date = date;
        Name = name;
        Image = image;
        this.commentId = commentId;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }



    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }



    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }



    public Comments() {

    }





}
