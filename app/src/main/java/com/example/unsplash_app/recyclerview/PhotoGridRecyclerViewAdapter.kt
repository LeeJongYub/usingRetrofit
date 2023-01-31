package com.example.unsplash_app.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unsplash_app.data.Photo
import com.example.unsplash_app.R

class PhotoGridRecyclerViewAdapter : RecyclerView.Adapter<PhotoItemViewHolder>() {

    private var photoList = ArrayList<Photo>()

    // 뷰홀더와 레이아웃을 연결하는 부분
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_photo_item, parent, false)

        return PhotoItemViewHolder(view)
    }

    // 실제 화면에 배치될 데이터를 뷰홀더에 넘겨준다.
    override fun onBindViewHolder(holder: PhotoItemViewHolder, position: Int) {
        holder.bindWithView(photoList[position])
    }

    // 포그라운드에 보여줄 목록의 갯수
    override fun getItemCount(): Int {
        return photoList.size
    }

    // 외부에서 어댑터에 데이터 배열을 넣어준다.
    fun submitList(photoList : ArrayList<Photo>) {
        // 어댑터에 선언한 리스트(this.photoList)에 실제 데이터가 담겨있는 리스트(photoList)를 넘겨준다.
        this.photoList = photoList
    }

}