package com.example.unsplash_app

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class SearchCollectionActivity : AppCompatActivity() {

    // Intent 하여 해당 액티비티로 받아온 데이터를 담아둘 리스트 선언
    private var photoDataList = ArrayList<Photo>()

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_search_collection)

        // 번들 데이터를 받음
        val bundle = intent.getBundleExtra("search_data")

        // 툴바에 표시하기 위해 보냈던 데이터도 받음
        val getSearchText = intent.getStringExtra("search_text")

        photoDataList = bundle?.getSerializable("search_data_serialized") as ArrayList<Photo>

    }
}