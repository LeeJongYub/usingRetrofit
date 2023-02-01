package com.example.unsplash_app.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unsplash_app.R
import com.example.unsplash_app.data.SearchData
import com.example.unsplash_app.retrofit.ISearchHistoryRecyclerView

// 클래스의 생성자로 interfaceSearchHistoryRecyclerView : ISearchHistoryRecyclerView(생성한 인터페이스)를 넣어서
// 해당 인터페이스의 기능을 어댑터에 알려줄 수 있고, 사용될 액티비티에 호출하여 사용할 수 있다.
class SearchHistoryRecyclerViewAdapter(interfaceSearchHistoryRecyclerView : ISearchHistoryRecyclerView) : RecyclerView.Adapter<SearchItemViewHolder>() {

    private var getSearchHistoryList : ArrayList<SearchData> = ArrayList()

    // 설정한 인터페이스를 사용하기 위한 변수
    private var adapterSearchHistoryRecyclerView : ISearchHistoryRecyclerView? = null

    // init{}은 클래스의 생성자보다 늦게 호출이 된다.
    init {
        // 어댑터의 adapterSearchHistoryRecyclerView 와 인터페이스를 호출한 생성자 interfaceSearchHistoryRecyclerView 를 연결하는 작업
        // 해당 프로젝트에선 커스텀 뷰홀더 클래스를 따로 만들어놨기 때문에, 해당 뷰홀더 클래스에도 연결 작업을 해야함
        adapterSearchHistoryRecyclerView = interfaceSearchHistoryRecyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_search_item, parent, false)

        return SearchItemViewHolder(view, adapterSearchHistoryRecyclerView!!)
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        // 각 position 에 맞게 데이터가 담겨있는 리스트(getSearchHistoryList[position])를 뷰홀더에 설정해둔
        // 함수를 통해 데이터와 뷰를 묶게 된다.

        holder.bindWithView(getSearchHistoryList[position])
    }

    override fun getItemCount(): Int {
        return getSearchHistoryList.size
    }

    // 외부에서 어댑터에서 데이터 배열을 넣어준다.
    fun submitList(putSearchHistoryList : ArrayList<SearchData>) {
        this.getSearchHistoryList = putSearchHistoryList
    }

}