package com.example.unsplash_app.data

import java.io.Serializable

data class Photo(

    var thumbnail : String?,
    val author : String?,
    val createdAt : String?,
    var likesCount : Int?

) : Serializable
