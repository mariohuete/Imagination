package com.mariohuete.imagination.models;


/**
 *
 * Created by Mario Huete Jiménez on 07/05/15.
 */
public class Album {
    //ATTRIBUTES------------------------------------------------------------------------------------
    private double id;
    private double artistId;
    private String title;
    private String type;
    private String picture;

    //METHODS---------------------------------------------------------------------------------------
    public Album(double id, double artistId, String title, String type, String picture) {
        this.id = id;
        this.artistId = artistId;
        this.title = title;
        this.type = type;
        this.picture = picture;
    }

    public void setId(double id) {
        this.id = id;
    }

    public double getId() {
        return this.id;
    }

    public void setArtistId(double artistId) {
        this.artistId = artistId;
    }

    public double getArtistId() {
        return this.artistId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPicture() {
        return this.picture;
    }
}