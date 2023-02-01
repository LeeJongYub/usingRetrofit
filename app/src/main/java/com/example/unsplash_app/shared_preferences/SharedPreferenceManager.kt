package com.example.unsplash_app.shared_preferences

import android.content.Context
import android.util.Log
import com.example.unsplash_app.data.SearchData
import com.example.unsplash_app.utils.MyApp
import com.google.gson.Gson

object SharedPreferenceManager {

    // 검색 목록을 저장하기 위한 sharedPreferences key 설정
    private const val SHARED_SEARCH_HISTORY = "shared_search_history"
    private const val KEY_SEARCH_HISTORY = "key_search_history"

    // "검색어 저장 스위치" 활성화 여부에 따른 저장을 위한 sharedPreferences key 설정
    private const val SHARED_SEARCH_HISTORY_MODE = "shared_search_history_mode"
    private const val KEY_SEARCH_HISTORY_MODE = "key_search_history_mode"

    // 검색어 저장 스위치(모드) 설정하기
    // (1) 검색 저장 스위치 온오프 상태 저장하기
    fun setSearchHistoryMode(isActivated : Boolean) {

        val shared = MyApp.instance.getSharedPreferences(SHARED_SEARCH_HISTORY_MODE, Context.MODE_PRIVATE)

        val editor = shared.edit()

        editor.putBoolean(KEY_SEARCH_HISTORY_MODE, isActivated)

        editor.apply()

    }

    // (2) 검색 저장 스위치 온오프 상태 저장된 것을 불러오기
    fun getSearchHistoryMode(): Boolean {

        val shared =
            MyApp.instance.getSharedPreferences(SHARED_SEARCH_HISTORY_MODE, Context.MODE_PRIVATE)

        // 저장되어있던 값 꺼내기 / 값이 없을 경우 디폴트 값을 false 설정
        return shared.getBoolean(KEY_SEARCH_HISTORY_MODE, false)
    }



    // sharedPreferences 를 활용하여
    // (1) 검색목록을 저장
    fun storeSearchHistoryList(searchHistoryList : MutableList<SearchData>) {

        // 매개변수로 있는 배열(MutableList<SearchData>)을 -> 문자열로 변환
        val searchHistoryListString = Gson().toJson(searchHistoryList)

        // 쉐어드 인스턴스 가져오기 (MODE_PRIVATE : 같은 앱에서만 사용가능)
        val shared = MyApp.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)

        // 쉐어드 에디터 가져오기
        val editor = shared.edit()

        editor.putString(KEY_SEARCH_HISTORY, searchHistoryListString)

        // 값을 넣고 이를 apply 나 commit 으로 적용이 가능한데,
        // commit 은 저장성공여부를 boolean 타입으로 저장 / apply 는 반환값이 없는데 IDE 에서는 apply 사용을 권장 - 비동기적으로 반영이 가능
        editor.apply()

    }

    // (2) 검색목록을 호출
    fun getSearchHistoryList() : MutableList<SearchData> {

        // 쉐어드 인스턴스 가져오기
        val shared = MyApp.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)

        // 저장되어있던 값 꺼내기
        val getSearchHistoryListString = shared.getString(KEY_SEARCH_HISTORY, "")!!

        // 저장된 값을 넣어둘 배열 만들기
        var getSearchHistoryList = ArrayList<SearchData>()
        Log.d("getSearchHistoryList1", "$getSearchHistoryList") // []

        // 저장된 값이 비어있지 않다면 (검색목록에 값이 있다면)
        if (getSearchHistoryListString.isNotEmpty()) {
            // 저장된 문자열을 -> 객체 배열로 변경
            getSearchHistoryList = Gson()
                                        .fromJson(getSearchHistoryListString, Array<SearchData>::class.java)
                                        .toMutableList() as ArrayList<SearchData>
        }
        Log.d("getSearchHistoryList2", "$getSearchHistoryList") // [SearchData(term=dog, timeStamp=16:13:35), SearchData(term=dog, timeStamp=16:13:40)]
        return getSearchHistoryList
    }

    // 검색목록 지우기
    fun removeAllSearchHistory() {
        // 쉐어드 인스턴스 가져오기 (MODE_PRIVATE : 같은 앱에서만 사용가능)
        val shared = MyApp.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)

        // 쉐어드 에디터 가져오기
        val editor = shared.edit()

        // 위에 검색 목록을 저장하는 sharedPreference 를 가져와서(설정한 sharedPreferences 명 : SHARED_SEARCH_HISTORY)
        // clear() 메소드를 통해 지울 수 있다.
        editor.clear()

        // apply() 를 통해 변경사항 적용
        editor.apply()
    }

}