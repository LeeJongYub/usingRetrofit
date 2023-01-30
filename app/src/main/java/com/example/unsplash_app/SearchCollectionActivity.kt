package com.example.unsplash_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unsplash_app.databinding.ActivitySearchCollectionBinding
import com.example.unsplash_app.recyclerview.PhotoGridRecyclerViewAdapter
import com.google.android.material.appbar.AppBarLayout

class SearchCollectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchCollectionBinding

    // Intent 하여 해당 액티비티로 받아온 데이터를 담아둘 리스트 선언
    private var photoDataList = ArrayList<Photo>()

    // 만들어둔 어댑터 호출을 위한 선언
    private lateinit var photoGridRecyclerViewAdapter: PhotoGridRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 번들 데이터를 받음
        val bundle = intent.getBundleExtra("search_data_bundle")

        // 툴바에 표시하기 위해 보냈던 데이터도 받음
        val getSearchText = intent.getStringExtra("search_text")

        photoDataList = bundle?.getSerializable("search_data_serialized") as ArrayList<Photo>

        Log.d("getSearchText", getSearchText.toString())

        this.photoGridRecyclerViewAdapter = PhotoGridRecyclerViewAdapter()

        this.photoGridRecyclerViewAdapter.submitList(photoDataList)

        val selectRV = findViewById<RecyclerView>(R.id.my_photo_recycler_view)

        selectRV.adapter = photoGridRecyclerViewAdapter
        selectRV.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        binding.topAppBar.title = getSearchText
    }
}