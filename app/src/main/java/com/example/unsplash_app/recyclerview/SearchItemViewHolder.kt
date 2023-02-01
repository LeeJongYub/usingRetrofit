package com.example.unsplash_app.recyclerview

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.recyclerview.widget.RecyclerView
import com.example.unsplash_app.R
import com.example.unsplash_app.data.SearchData
import com.example.unsplash_app.retrofit.ISearchHistoryRecyclerView

class SearchItemViewHolder(itemView : View,
                           interfaceSearchHistoryRecyclerView: ISearchHistoryRecyclerView)
// 커스텀 뷰홀더 클래스를 따로 만들어 관리 중이기 때문에 뷰홀더 클래스에 생성한 인터페이스를 사용함을 알려주기 위한 연결작업 필요
                           : RecyclerView.ViewHolder(itemView), View.OnClickListener {
// 각 뷰에 대한 클릭처리를 해야하기 때문에 View.OnClickListener 를 클래스의 반환 타입으로 가져온다

    private var viewHolderISearchHistoryRecyclerView : ISearchHistoryRecyclerView? = null

    // 뷰 가져오기
    private val recentSearchText = itemView.findViewById<TextView>(R.id.recent_search_text_item)
    private val recentSearchDate = itemView.findViewById<TextView>(R.id.recent_search_date_item)
    private val recentSearchDeleteImage = itemView.findViewById<ImageView>(R.id.delete_recent_search_image_item)
    // 목록의 어떤 부분을 누르더라도 클릭이 되었다고 처리하기 위해 레이아웃에 id 를 설정하여 뷰를 가져온다.
    private val constraintSearchLayout = itemView.findViewById<ConstraintLayout>(R.id.constraint_search_item)

    // 클래스의 반환타입으로 설정한 View.OnClickListener 를 onClick 메소드에서 사용할 뷰와 연결시켜줘야 사용가능하다
    init {
        // 리스너 연결 (리스너 연결을 하지않으면 메소드가 실행되지 않으며, 이 경우의 this 는 View.OnClickListener 이다)
        recentSearchDeleteImage.setOnClickListener(this)
        constraintSearchLayout.setOnClickListener(this)
        // 인터페이스 사용을 뷰홀더의 viewHolderISearchHistoryRecyclerView 에 연결하여 뷰홀더에 알려준다.
        viewHolderISearchHistoryRecyclerView = interfaceSearchHistoryRecyclerView
    }


    // 데이터와 뷰를 묶는다
    fun bindWithView(searchItem : SearchData) {

        recentSearchText.text = searchItem.term
        recentSearchDate.text = searchItem.timeStamp


    }

    override fun onClick(view: View?) {
        when(view) {
            recentSearchDeleteImage -> {
                // 생성자로 받은 iSearchHistoryRecyclerView 를 넣은 뷰홀더의 viewHolderISearchHistoryRecyclerView 에서 인터페이스 기능을 사용함
                // 뷰홀더에선 adapterPosition 을 통해 해당 아이템의 순번(position)을 알 수 있음
                viewHolderISearchHistoryRecyclerView?.onSearchItemDeleteImageClicked(adapterPosition)
            }
            constraintSearchLayout -> {
                viewHolderISearchHistoryRecyclerView?.onSearchItemClicked(adapterPosition)
            }
        }
    }

}