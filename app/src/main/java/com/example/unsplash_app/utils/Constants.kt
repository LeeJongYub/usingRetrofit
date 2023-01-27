package com.example.unsplash_app

object Constants {

    const val TAG = "로그"
}

enum class SEARCH_TYPE {
    PHOTO,
    USER
}

enum class RESPONSE_STATE {
    OK,
    NO
}

object API {
    const val BASE_URL = "https://api.unsplash.com/"

    const val CLIENT_ID = "kysjC-5GQtnY7Vw0edDmsj9L6tVjD9t2ycoGcplJA-0"

    const val SEARCH_PHOTOS = "search/photos"

    const val SEARCH_USERS = "search/users"
}