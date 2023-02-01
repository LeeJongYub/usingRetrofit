package com.example.unsplash_app.retrofit

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.unsplash_app.API
import com.example.unsplash_app.Constants.TAG
import com.example.unsplash_app.utils.MyApp
import com.example.unsplash_app.isJsonArray
import com.example.unsplash_app.isJsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 레트로핏 클라이언트 선언
    private var retrofitClient: Retrofit? = null

    // 레트로핏 클라이언트 가져오기
    fun getClient(baseUrl: String): Retrofit? {

        // 로깅 인터셉터 추가
        // (그냥 로그를 찍어 데이터를 확인하는 것은 디버깅 및 호출된 데이터의 구조 이해에 용이함,
        // 하지만 헤더, 상태 코드 및 기타 세부 정보 등 "전체적인 데이터"를 제공하진 않음 -> so, 로깅 인터셉터를 사용하는 것
        // "api 요청시 필요한 선행 작업"이 있다면 인터셉터로 처리함)

        // 1. okHttp 인스턴스 생성 (로깅 인터셉터 사용을 위한)
        val loggingClient = OkHttpClient.Builder()

        // 2. 로깅 인스턴스 생성 (로그를 찍기 위한)
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d(TAG, "$message")

                when {
                    message.isJsonObject() -> Log.d(TAG, JSONObject(message).toString())
                    message.isJsonArray() -> Log.d(TAG, JSONArray(message).toString())
                    else -> {
                        try {
                            Log.d(TAG, JSONObject(message).toString(4)) // indentSpaces : 4줄 들여쓰기
                            Log.d(TAG, JSONArray(message).toString(4))
                        } catch (e: Exception) {
                            Log.d(TAG, message)
                        }

                    }
                }
            }

        })

        // 로깅 인터셉터의 레벨 설정할 수 있다
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        // NONE : 로깅하지 않음
        // BASIC : 요청 및 응답라인, 상태코드, 메세지크기
        // HEADER : 요청 및 응답라인, 헤더 및 해당값
        // BODY : 요청 및 응답라인, 헤더 및 해당값, 요청 및 응답본문

        // okHttp 인스턴스(로깅 인터셉터용)에 인터셉터를 추가한다
        loggingClient.addInterceptor(loggingInterceptor)

        // 인증키를 넣기 위한 파라미터(?) 인터셉터 설정
        val baseParameterInterceptor: Interceptor = (object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                // 기존 요청
                val originalRequest = chain.request()

                // 쿼리 파라미터 추가 (인증키 url 을 넣기 위한)
                // url 주소에 ?client_id="~~~" 이 과정을 추가하는 것
                val addUrl = originalRequest
                    .url
                    .newBuilder()
                    .addQueryParameter("client_id", API.CLIENT_ID)
                    .build()

                val finalRequest = originalRequest
                    .newBuilder()
                    .url(addUrl)
                    .method(originalRequest.method, originalRequest.body)
                    .build()

//                return chain.proceed(finalRequest)

                // response.code ex) 200, 401, 404 등을 확인하기 위해 response 변수를 만들어
                val response = chain.proceed(finalRequest)

                // response.code 가 에러일 때(200이 아닐 때) 토스트 메세지를 띄우기 위한 코드
                if (response.code != 200) {
                    // 메인 스레드를 사용하지 않는 곳에 ui 작업을 하려하면 오류가 발생한다.
                    // 이를 방지하기 위해 Handelr(Looper.getMainLooper()의 post 바디 블럭({}) 안에 ui 작업 코드를 넣어준다.)
                    Handler(Looper.getMainLooper()).post {
                    Toast.makeText(MyApp.instance, "${response.code} 에러입니다.", Toast.LENGTH_LONG).show()
                    }
                }

                return response
            }

        })

        // okHttp 인스턴스(로깅 인터셉터용)에 인증키를 넣기위해 설정한 인터셉터를 설정한다.
        loggingClient.addInterceptor(baseParameterInterceptor)

        // 레트로핏 클라이언트가 없다면
        if (retrofitClient == null) {
            // 레트로핏 빌더를 통해 인스턴스를 생성한다
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                // 위에서 설정한 로깅 인터셉터용 클라이언트를 추가한다
                .client(loggingClient.build())
                .build()

        }
        return retrofitClient
    }

}