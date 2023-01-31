package com.example.unsplash_app.recyclerview

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.unsplash_app.MyApp
import com.example.unsplash_app.data.Photo
import com.example.unsplash_app.R

class PhotoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // 뷰를 가져온다
    private val photoImageView = itemView.findViewById<ImageView>(R.id.photo_image)
    private val photoCreatedAtText = itemView.findViewById<TextView>(R.id.created_at_text)
    private val photoLikesCountText = itemView.findViewById<TextView>(R.id.likes_count_text)
    private val likesClickImage = itemView.findViewById<ImageView>(R.id.likes_click_image)
    private val addClickImage = itemView.findViewById<ImageView>(R.id.add_click_image)

    // 데이터와 뷰를 묶는다
    fun bindWithView(photoItem : Photo) {

        // 설정한 텍스트뷰와 photoItem(Photo 데이터클래스)에 업로드 날짜 데이터를 매칭시켜준다.
        photoCreatedAtText.text = photoItem.createdAt
        // 설정한 텍스트뷰와 photoItem(Photo 데이터클래스)에 좋아요개수 데이터를 매칭시켜준다.
        photoLikesCountText.text = photoItem.likesCount.toString()

        // 글라이드 라이브러리를 사용하여 이미지를 설정한다.
        Glide.with(MyApp.instance)
            .load(photoItem.thumbnail)
            .placeholder(R.drawable.ic_baseline_photo_library_24)
            .into(photoImageView)

    }
}