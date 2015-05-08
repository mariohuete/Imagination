package com.mariohuete.imagination.utils;

import com.mariohuete.imagination.models.Data;

import retrofit.Callback;
import retrofit.http.GET;


/**
 *
 * Created by Mario Huete Jiménez on 08/05/15.
 */
public interface RestApi {
    @GET("/data.json")
    void getData(Callback<Data> response);
}
