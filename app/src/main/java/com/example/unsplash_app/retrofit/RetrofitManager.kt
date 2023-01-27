package com.example.unsplash_app.retrofit

import android.util.Log
import com.example.unsplash_app.API
import com.example.unsplash_app.Constants.TAG
import com.example.unsplash_app.RESPONSE_STATE
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response

class RetrofitManager {

    companion object {
        val instance = RetrofitManager()
    }

    // http 를 call 하는 과정
    // 레트로핏 인터페이스를 가져옴
    private val iRetrofit = RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)

    // 사진 검색 api 호출,
    // searchPhotos 를 호출하는 부분에서 completion 을 발동시킴 -> 이벤트 전달, 데이터를 넘길 경우 completion() 안쪽에 자료형을 쓰면 됨)
    fun searchPhotos(searchTerm : String, completion : (RESPONSE_STATE, String) -> Unit) {

        val term = searchTerm ?: ""

        val call = iRetrofit?.searchPhotos(term) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement>{

            // 응답 성공시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d("header", "${response.headers()}")
                Log.d("raw", "${response.raw()}")
                Log.d("body", "${response.body()}")
                Log.d("code", "${response.code()}")

                completion(RESPONSE_STATE.OK, response.body().toString())

            }

            // 응답 실패시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d("fail", "$t")
                completion(RESPONSE_STATE.NO, t.toString())
            }

        })
    }



}