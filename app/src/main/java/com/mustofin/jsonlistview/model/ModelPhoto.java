package com.mustofin.jsonlistview.model;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by root on 23/09/16.
 */
public class ModelPhoto extends RealmObject{
    @SerializedName("nama") public String nama;
    @SerializedName("foto") public String foto;
}
