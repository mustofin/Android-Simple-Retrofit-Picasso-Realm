package com.mustofin.jsonlistview;

import com.mustofin.jsonlistview.model.ModelPhoto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by root on 23/09/16.
 */
public interface BaseAPI {
    String baseUrl = "http://demo7104902.mockable.io/";

    @GET("dedek")
    Call<List<ModelPhoto>> getPhotoList();

    class Factory{
        public static BaseAPI create(){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return retrofit.create(BaseAPI.class);
        }
    }
}
