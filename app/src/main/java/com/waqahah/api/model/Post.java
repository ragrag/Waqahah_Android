package com.waqahah.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.ocpsoft.prettytime.PrettyTime;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Post {

    @SerializedName("pk")
    @Expose
    private BigInteger pk;
    @SerializedName("like")
    @Expose
    private String like;
    @SerializedName("dislike")
    @Expose
    private String dislike;
    @SerializedName("first_impression")
    @Expose
    private String firstImpression;
    @SerializedName("current_impression")
    @Expose
    private String currentImpression;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("date_created")
    @Expose
    private String dateCreated;

public Post()
{}
    public Post(String like, String dislike, String firstImpression, String currentImpression, String message, String dateCreated) {
        this.like = like;
        this.dislike = dislike;
        this.firstImpression = firstImpression;
        this.currentImpression = currentImpression;
        this.message = message;
        this.dateCreated = dateCreated;
    }

    public BigInteger getPk() {
        return pk;
    }

    public void setPk(BigInteger pk) {
        this.pk = pk;
    }

    public String getLike() {
        if (like != "")
        return like;
        else return "";
    }

    public void setLike(String like) {

        if (like == null)
            this.like="";
        else this.like = like;
    }

    public String getDislike() {
        if (dislike != "")
        return dislike;
        else return "";
    }

    public void setDislike(String dislike) {

        if (dislike == null)
            this.dislike="";

      else  this.dislike = dislike;
    }

    public String getFirstImpression() {
        if(firstImpression != "")
            return firstImpression;
        else return "";
    }

    public void setFirstImpression(String firstImpression) {

        if (firstImpression == null)
            this.firstImpression="";

        else  this.firstImpression = firstImpression;
    }

    public String getCurrentImpression() {
        if (currentImpression != "")
             return currentImpression;

        else return "";
    }

    public void setCurrentImpression(String currentImpression) {

        if (currentImpression == null)
            this.currentImpression="";

        else  this.currentImpression = currentImpression;
    }

    public String getMessage() {
       if (message != "")
            return message;
        else return "";
    }

    public String getAll() {
            return getLike()+getFirstImpression()+getCurrentImpression()+getMessage()+getDislike();

    }

    public void setMessage(String message) {


        if (message == null)
            this.message="";

        else this.message = message;

    }

    public String getDateCreated() throws ParseException {

        String dateStr = this.dateCreated;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ENGLISH);
        Date created_date = (Date)formatter.parse(dateStr);
        PrettyTime p = new PrettyTime();
        return p.format(created_date);

    }


    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

}