package com.example.unsplash_app.retrofit

import com.example.unsplash_app.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IRetrofit {

    @GET(API.SEARCH_PHOTOS)
    fun searchPhotos(@Query("query") searchTerm: String) : Call<JsonElement>

    @GET(API.SEARCH_USERS)
    fun searchUsers(@Query("query") searchTerm: String) : Call<JsonElement>

}