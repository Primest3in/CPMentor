package com.example.cpyipee;

public class Post {
    String AskedBy, Date, DownloadUrl,Publisher, Topic, postId, postTexts, whoImg;

    public Post() {

    }


    public String getAskedBy() {
        return AskedBy;
    }

    public void setAskedBy(String askedBy) {
        AskedBy = askedBy;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDownloadUrl() {
        return DownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        DownloadUrl = downloadUrl;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }

    public String getTopic() {
        return Topic;
    }

    public void setTopic(String topic) {
        Topic = topic;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostTexts() {
        return postTexts;
    }

    public void setPostTexts(String postTexts) {
        this.postTexts = postTexts;
    }

    public Post(String askedBy, String date, String downloadUrl, String publisher, String topic, String postId, String postTexts, String whoImg) {
        this.AskedBy = askedBy;
        this.Date = date;
        this.DownloadUrl = downloadUrl;
        this.Publisher = publisher;
        this.Topic = topic;
        this.postId = postId;
        this.postTexts = postTexts;
        this.whoImg = whoImg;
    }

    public String getWhoImg() {
        return whoImg;
    }

    public void setWhoImg(String whoImg) {
        this.whoImg = whoImg;
    }
}
