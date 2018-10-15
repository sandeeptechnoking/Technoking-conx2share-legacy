package com.conx2share.conx2share.ui.settings;

import com.google.gson.JsonObject;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by heathersnepenger on 3/10/17.
 */

public interface FreshdeskService {

    @POST("/api/v2/tickets")
    Response sendContactSupport(@Header("Authorization") String authorization, @Body JsonObject jsonObject);
}
