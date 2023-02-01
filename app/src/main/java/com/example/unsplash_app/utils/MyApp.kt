package com.example.unsplash_app.utils

import android.app.Application

// 클래스 자체가 appcompatActivity 가 아닌 경우(adapter class, retrofit class 등)에 context 를 가져와야할 경우
// (매니페스트의 application:name 부분에 만든 app 클래스를 입력해줘야 사용할 수 있음)
class MyApp : Application() {

    companion object {
        lateinit var instance : MyApp
        private set
    }

    // Application 을 상속받은 싱글톤 instance(MyApp)이 생성되는 경우에 사용할 수 있도록 함
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    // activity(appcompatActivity 를 상속받지 않은)에 사용될 때는 MyApp.instance 를 호출하여 사용하면 되고,
    // MyApp.instance 자체가 context 가 된다.
}