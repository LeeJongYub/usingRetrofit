package com.example.unsplash_app

import java.io.Serializable

data class Photo(

    var thumbnail : String?,
    val author : String?,
    val createdAt : String?,
    var likesCount : Int?

) : Serializable
