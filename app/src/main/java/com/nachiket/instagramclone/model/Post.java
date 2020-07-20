package com.nachiket.instagramclone.model;

public class Post {

    private String postId;
    private String publisher;
    private String imgUrl;
    private String description;

    public Post() {
    }

    public Post(String postId, String publisher, String imgUrl, String description) {
        this.postId = postId;
        this.publisher = publisher;
        this.imgUrl = imgUrl;
        this.description = description;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
