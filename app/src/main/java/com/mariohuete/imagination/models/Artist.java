package com.mariohuete.imagination.models;

import java.io.Serializable;


/**
 *
 * Created by Mario Huete Jiménez on 07/05/15.
 */
public class Artist implements Serializable{
    //ATTRIBUTES------------------------------------------------------------------------------------
    private double id;
    private String genres;
    private String picture;
    private String name;
    private String description;

    //METHODS---------------------------------------------------------------------------------------
    public Artist(double id, String genres, String picture, String name, String description) {
        this.id = id;
        this.genres = genres;
        this.picture = picture;
        this.name = name;
        this.description = description;
    }

    public void setId(double id) {
        this.id = id;
    }

    public double getId() {
        return this.id;
    }

    /*public void setGenres(String genres) {
        this.genres = genres;
    }*/

    public String getGenres() {
        return this.genres;
    }

    /*public void setPicture(String picture) {
        this.picture = picture;
    }*/

    public String getPicture() {
        return this.picture;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /*public void setDescription(String description) {
        this.description = description;
    }*/

    public String getDescription() {
        return this.description;
    }
}