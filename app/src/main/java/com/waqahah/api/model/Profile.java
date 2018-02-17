package com.waqahah.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Profile implements Serializable {

    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("about")
    @Expose
    private String about;

    @SerializedName("username2")
    @Expose
    private String username;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }


    public String getUsername() {
        return username;
    }

    public void setusername(String username) {
        this.avatar = username;
    }

}