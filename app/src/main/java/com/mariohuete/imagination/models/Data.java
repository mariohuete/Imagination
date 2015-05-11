package com.mariohuete.imagination.models;

import java.util.List;


/**
 *
 * Created by Mario Huete Jiménez on 07/05/15.
 */
public class Data {
    //ATTRIBUTES------------------------------------------------------------------------------------
    private List<Artist> artists;
    private List<Album> albums;

    //METHODS---------------------------------------------------------------------------------------
    public Data(List<Artist> artists, List<Album> albums) {
        this.artists = artists;
        this.albums = albums;
    }

    /*public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }*/

    public List<Artist> getArtists() {
        return this.artists;
    }

    /*public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }*/

    public List<Album> getAlbums() {
        return this.albums;
    }
}