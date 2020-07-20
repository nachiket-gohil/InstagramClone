package com.nachiket.instagramclone.model;

public class User {

    private String username;
    private String name;
    private String bio;
    private String imageurl;
    private String email;
    private String id;

    public User() { }

    public User(String username, String name, String bio, String imageurl, String email, String id) {
        this.username = username;
        this.name = name;
        this.bio = bio;
        this.imageurl = imageurl;
        this.email = email;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
