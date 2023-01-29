package com.example.unsplash_app.retrofit

import android.util.Log
import com.example.unsplash_app.API
import com.example.unsplash_app.Photo
import com.example.unsplash_app.RESPONSE_STATE
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat

class RetrofitManager {

    companion object {
        val instance = RetrofitManager()
    }

    // http 를 call 하는 과정
    // 레트로핏 인터페이스를 가져옴
    private val iRetrofit = RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)

    // 사진 검색 api 호출,
    // searchPhotos 를 호출하는 부분에서 completion 을 발동시킴 -> 이벤트 전달, 데이터를 넘길 경우 completion() 안쪽에 자료형을 쓰면 됨)
    fun searchPhotos(searchTerm: String, completion: (RESPONSE_STATE, ArrayList<Photo>?) -> Unit) {

        // 레트로핏 인터페이스에서 호출할 데이터를 정해놓은 메서드(searchPhotos)
        val call = iRetrofit?.searchPhotos(searchTerm) ?: return

        // 해당 메서드를 실행(enqueue)하여 callback 으로 통신 성공, 실패 여부를 체크 및 데이터를 호출한다.
        call.enqueue(object : retrofit2.Callback<JsonElement> {

            // 응답 성공시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {

                // response.code 를 확인한 다음에 데이터를 넘겨줌(?)
                when (response.code()) {

                    200 -> {

                        response.body()?.let {
                            // 아래 코드들을 통해 형식 변환, 원하는 내용만 추출한 데이터를 담아놓기 위한 arrayList 생성
                            val photoDataList = ArrayList<Photo>()

                            // let 스코프 함수를 사용(it 에 response.body 의 데이터가 다 담겨있음)
                            // asJsonObject 를 통해 body 부분을 jsonObject 로 가져옴
                            val body = it.asJsonObject

                            // body 에 있는 데이터 중 results 라는 배열이 있고, 이것을 getAsJsonArray()를 통해 json 배열로 빼내는 과정
                            val results = body.getAsJsonArray("results")
                            Log.d("resultArray", results.toString())

                            // url 안에 있는 제이슨 데이터 중, total(총 데이터) 부분의 값(value)을 보기 위해
                            // get() 안에 키(key)이름을 써서 asInt 나 asString 을 통해 원하는 값을 확인함
                            val total = body.get("total").asInt
                            Log.d("total", "total : ${total}") // total : 10000

                            // 리사이클러뷰에 데이터를 넣기 전, 넣고 싶은 데이터만 추출하는 과정

                            // results 에는 response.body 부분 중 "result" 라는 jsonArray 가 담겨있음
                            // 이것은 한줄씩 표시(forEach)하여, 원하는 데이터를 제대로 가져왔는지 로그로 확인중..
                            results.forEach { resultsItem ->

                                // array 로 가져온 부분(results)을 resultsItem(한줄씩 표시된 상태)에서 asJsonObject 로 변환
                                val resultsObject = resultsItem.asJsonObject
                                Log.d("resultObject", resultsObject.toString())

                                // 배열이 JsonObject 로 변환된 resultObject 에서 "user" 부분을 가져옴
                                val user = resultsObject.get("user").asJsonObject
                                Log.d("resultId", user.toString())

                                // "user" 안에 있는 "username" 만 추출하여 문자열로 표기 및 로그로 확인
                                val userName = user.get("username").asString
                                Log.d("userName", userName)

                                // 좋아요 개수 데이터 추출과정
                                val likeCount = resultsObject.get("likes").asInt
                                Log.d("likeCount", likeCount.toString())

                                // 썸네일 주소 데이터 추출과정
                                val thumbLinks = resultsObject.get("urls").asJsonObject.get("thumb").asString
                                Log.d("thumbLinks", thumbLinks)

                                // 날짜(사진 업로드) 데이터 추출과정
                                val createdAt = resultsObject.get("created_at").asString
                                Log.d("createdAt", createdAt)

                                // createdAt 부분을 로그를 찍어보면 "2019-11-16T14:27:18Z" 와 같이 데이터가 찍힘
                                // 이를 원하는 형태 "2019년 11월 16일"과 같이 찍기 위해선 2가지 과정이 필요하다.

                                // 1. 파싱 및 형식변환을 위한 변수들을 선언
                                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                val formatter = SimpleDateFormat("yyyy년 MM월 dd일")

                                Log.d("parseData1", parser.parse(createdAt).toString())


                                // 2. 데이터에 형식을 지정
                                // (1) parser 변수에 선언한 대로 createdAt 데이터를 파싱한다. // Sat Nov 16 00:53:02 GMT+09:00 2019
                                // (2) formatter 변수에 지정한 형식대로 보이게 하는 코드 // 2019년 11월 16일
                                val outputDataString = formatter.format(parser.parse(createdAt))
                                Log.d("parseData2", outputDataString)

                                // 주의사항 - (2)만 적용하면 "2019-11-16T14:27:18Z" 의 부분중 어떤 부분이 년,월,일인지 인지하지 못한다.
                                // 그래서, 어떤 부분이 년,월,일인지를 알려주기 위해 (1) 작업이 필요한 것이다.


                                // 어떤 데이터만 표시할지 설정한 데이터클래스에 추출한 데이터들을 알맞게 넣어준다.
                                // 이는 후에 리사이클러뷰를 만들고 데이터를 넣는 과정에서 활용되게 된다.
                                val photoItem = Photo(
                                    thumbnail = thumbLinks,
                                    author = userName,
                                    createdAt = createdAt,
                                    likesCount = likeCount
                                )

                                // 만들어둔 리스트에 형식변환, 원하는 내용만 추출한 데이터를 리스트에 담음
                                photoDataList.add(photoItem)

                            }
                            // response.body() 부분에 데이터가 담겨져 있음
                            completion(RESPONSE_STATE.OK, photoDataList)

                        }


                    }
                }
            }

            // 응답 실패시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d("fail", "$t")
                completion(RESPONSE_STATE.NO, null)
            }

        })
    }


}