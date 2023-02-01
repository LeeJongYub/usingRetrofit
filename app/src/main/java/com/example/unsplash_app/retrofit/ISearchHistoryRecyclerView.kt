package com.example.unsplash_app.retrofit


// 검색된 목록에서 검색된 목록 아이템 클릭, 삭제버튼 클릭시 처리할 메소드를 만들기 위한 인터페이스
interface ISearchHistoryRecyclerView {

    // 검색된 목록에서 "삭제 이미지(버튼)" 클릭시 처리될 메소드
    fun onSearchItemDeleteImageClicked(position : Int)

    // 검색된 목록 클릭
    fun onSearchItemClicked(position: Int)


}