package com.example.unsplash_app.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter.LengthFilter
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unsplash_app.R
import com.example.unsplash_app.RESPONSE_STATUS
import com.example.unsplash_app.shared_preferences.SharedPreferenceManager
import com.example.unsplash_app.data.Photo
import com.example.unsplash_app.data.SearchData
import com.example.unsplash_app.databinding.ActivitySearchCollectionBinding
import com.example.unsplash_app.recyclerview.PhotoGridRecyclerViewAdapter
import com.example.unsplash_app.recyclerview.SearchHistoryRecyclerViewAdapter
import com.example.unsplash_app.retrofit.ISearchHistoryRecyclerView
import com.example.unsplash_app.retrofit.RetrofitManager
import com.example.unsplash_app.toSimpleDateString
import java.util.Date

class SearchCollectionActivity : AppCompatActivity(),
    OnQueryTextListener,
    CompoundButton.OnCheckedChangeListener,
    View.OnClickListener,
    ISearchHistoryRecyclerView {

// onQueryTextListener 를 통해 서치뷰의 입력상태 변화, 키패드의 검색버튼 클릭 여부에 따른 기능을 추가할 수 있다.
// compoundButton.OnCheckedChangeListener 를 통해 switch 토클의 온/오프 여부에 따른 기능을 추가할 수 있다.
// View.OnClickListener 를 통해 뷰의 클릭 여부에 따른 기능을 추가할 수 있다.
// ISearchHistoryRecyclerView 를 통해 검색된 목록에서 검색된 목록 아이템 클릭, 삭제버튼 클릭시 처리할 메소드를 만든 인터페이스를 호출

    private lateinit var binding: ActivitySearchCollectionBinding

    // Intent 하여 해당 액티비티로 받아온 데이터를 담아둘 리스트 선언
    private var photoDataList = ArrayList<Photo>()

    // 만들어둔 어댑터 호출을 위한 선언
    // (1) 검색한 텍스트에 대한 사진 및 정보를 띄우기 위해 만든 어댑터
    private lateinit var photoGridRecyclerViewAdapter: PhotoGridRecyclerViewAdapter

    // (2) 최근 검색한 항목에 대한 정보를 띄우기 위해 만든 어댑터
    private lateinit var searchHistoryRecyclerViewAdapter: SearchHistoryRecyclerViewAdapter

    // 서치뷰 선언
    private lateinit var mySearchView: SearchView

    // 서치뷰에 입력한 editText 를 호출할 mySearchViewEditText 선언
    private lateinit var mySearchViewEditText: EditText

    // 검색기록을 담을 배열
    private var searchHistoryList = ArrayList<SearchData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // class 에 설정해둔 리스너 타입(CompoundButton.OnCheckedChangeListener ~)에 대해 onCreate 메서드에서
        // 서로 연결을 해줘야 실행이 정상적으로 작동한다.
        binding.searchHistoryModeSwitch.setOnCheckedChangeListener(this) // this : CompoundButton.OnCheckedChangeListener
        binding.clearSearchHistoryButton.setOnClickListener(this) // this : View.OnClickListener

        // 번들 데이터를 받음
        val bundle = intent.getBundleExtra("search_data_bundle")

        // 툴바에 표시하기 위해 보냈던 데이터도 받음 (검색한 텍스트)
        val getSearchText = intent.getStringExtra("search_text")

        // 툴바 타이틀에, 검색한 텍스트를 배치한다
        binding.topAppBar.title = getSearchText
        // 툴바 타이틀 색상 변경
        binding.topAppBar.setTitleTextColor(Color.WHITE)

        // 만들어 놓은 툴바를 세팅한다
        setSupportActionBar(binding.topAppBar)

        photoDataList = bundle?.getSerializable("search_data_serialized") as ArrayList<Photo>

        // onCreate 메소드에 저장된 검색기록 가져오기
        searchHistoryList = SharedPreferenceManager.getSearchHistoryList() as ArrayList<SearchData>

        searchHistoryList.forEach {
            Log.d("searchHistoryList", "${it.term}, ${it.timeStamp}")
        }

        // 포토 콜랙션 어댑터 호출
        PhotoCollectionRecyclerviewSetting(photoDataList)

        // 검색 콜랙션 어댑터 호출
        // 데이터를 담으려 만든 함수(searchHistoryRecyclerviewSetting)에 sharedPreferences 로 검색 정보를 담은 리스트(searchHistoryList)를 연결해준다.
        searchHistoryRecyclerviewSetting(searchHistoryList)

    } // onCreate

    // 포토 콜랙션 어댑터 세팅
    private fun PhotoCollectionRecyclerviewSetting(photoDataList: ArrayList<Photo>) {
        // 어댑터 준비
        this.photoGridRecyclerViewAdapter = PhotoGridRecyclerViewAdapter()

        this.photoGridRecyclerViewAdapter.submitList(photoDataList)

        binding.myPhotoRecyclerView.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        binding.myPhotoRecyclerView.adapter = photoGridRecyclerViewAdapter
    }

    // 검색 콜랙션 어댑터 세팅
    private fun searchHistoryRecyclerviewSetting(searchHistoryList: ArrayList<SearchData>) {
        // 어댑터 준비
        searchHistoryRecyclerViewAdapter = SearchHistoryRecyclerViewAdapter(this)
        searchHistoryRecyclerViewAdapter.submitList(searchHistoryList)

        // 레이아웃 매니저 설정
        // reverseLayout 을 true, stackFromEnd 를 해줘야 최근 검색한 기록이 위에 쌓이게 된다.
        val myLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        myLayoutManager.stackFromEnd = true

        // 최근 검색 기록을 표시할 리사이클러뷰
        val recentSearchRecyclerView = binding.recentSearchRecyclerview
        recentSearchRecyclerView.apply {
            adapter = searchHistoryRecyclerViewAdapter

            layoutManager = myLayoutManager
            // ?
            scrollToPosition(searchHistoryRecyclerViewAdapter.itemCount - 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // 만들어놓은 메뉴(top_app_bar_menu)와 onCreateOptionsMenu 의 파라미터 menu 를 연결시켜준다.
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)

        // 서치뷰 적용
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        mySearchView = menu?.findItem(R.id.search_menu_item)?.actionView as SearchView

        mySearchView.apply {
            // 서치뷰에 힌트 텍스트를 부여한다.
            this.queryHint = "검색어를 입력해주세요."

            // 서치뷰 검색어 입력 이벤트와 이것을 사용하는 클래스를 연결해준다. (서치뷰 사용 밑작업)
            this.setOnQueryTextListener(this@SearchCollectionActivity)

            // 서치뷰에 포커스가 잡혔을 때(서치뷰를 클릭하여 텍스트를 입력하도록 열리는 입력란)를 감지하는 리스너
            this.setOnQueryTextFocusChangeListener { _, hasExpand ->
                when (hasExpand) {
                    true -> {
                        // 서치뷰가 열렸을 때(true) - 연한핑크로 설정해놓은 레이아웃을 보여줌
                        binding.searchViewExpand.visibility = View.VISIBLE
                    }
                    false -> {
                        // 서치뷰가 닫혔을 때(false) - 연한핑크로 설정해놓은 레이아웃을 닫음
                        binding.searchViewExpand.visibility = View.INVISIBLE
                    }
                }
            }

            // 선언한 editText (mySearchViewEditText)를 가져온다.
            // 서치뷰 텍스트(androidx.appcompat.R.id.search_src_text)를 editText 에 연결해준다.
            mySearchViewEditText = findViewById(androidx.appcompat.R.id.search_src_text)
        }

        mySearchViewEditText.apply {

            // 서치뷰에 입력될 텍스트의 길이를 12자로 제한함
            this.filters = arrayOf(LengthFilter(12))
            // 서치뷰에 입력될 텍스트의 색상을 '흰색'으로 정의
            this.setTextColor(Color.WHITE)
            // 서치뷰의 힌트텍스트 색상을 '흰색'으로 정의
            this.setHintTextColor(Color.WHITE)

        }

        return true
    }

    // 서치뷰 검색어 입력 이벤트
    // onQueryTextSubmit(검색 버튼이 클릭 되었을 때)
    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.d("query", "$query")

        // (1)글자 지움 (setQuery 로 들어갈 값을 ""로 설정, submit(검색버튼 누름)은 false 로 설정한다)
        mySearchView.setQuery("", false)
        // (2)키보드 창 닫음
        mySearchView.clearFocus()
        // (3)expand 된 서치뷰를 닫힘 처리함
        binding.topAppBar.collapseActionView()

        // 검색 버튼 누를 시 (1), (2)는 설정안해도 자동 적용되는걸 확인함 - 기능 확인을 위해 남겨둘 예정

        // 검색어(query)가 비어있지 않다면(!)
        if (!query.isNullOrEmpty()) {
            // 검색어를 저장 (검색한 텍스트 : query)
            val newSearchData = SearchData(term = query, timeStamp = Date().toSimpleDateString())

            // 검색어를 담는 리스트에 저장형식을 지정한 변수(newSearchData)를 넣어준다.
            searchHistoryList.add(newSearchData)

            // 검색 기록을 저장하기 위해 만든 메소드(storeSearchHistoryList)에 저장할 검색기록 리스트(searchHistoryList)를 저장
            SharedPreferenceManager.storeSearchHistoryList(searchHistoryList)

        }

        return true
    }

    // onQueryTextChange(검색어 입력 상태가 변화되었을 때)
    override fun onQueryTextChange(newText: String?): Boolean {
        val userInputText = newText ?: ""

        if (userInputText.count() == 12) {
            Toast.makeText(this, "검색어는 12자 이하까지 입력 가능합니다.", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    override fun onCheckedChanged(switchToggle: CompoundButton?, isChecked: Boolean) {
        // 사용할 스위치가
        when (switchToggle) {
            // 내가 만든 searchHistoryModeSwitch 라면
            binding.searchHistoryModeSwitch ->
                // 해당 스위치가 "온" (온 true / 오프 false) 상태라면
                if (isChecked == true) {
                    Log.d("searchSwitch", "온")
                } else {
                    Log.d("searchSwitch", "오프")
                }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.clearSearchHistoryButton ->
                Log.d("searchClearButton", "검색기록 삭제 버튼이 클릭되었다.")
        }
    }

    // 검색 목록 삭제 메소드 : ISearchHistoryRecyclerView 인터페이스에서 구현
    override fun onSearchItemDeleteImageClicked(position: Int) {
        Log.d("searchHistoryDelete", position.toString())
        // (1) 해당 순번(position)의 아이템을 삭제
        searchHistoryList.removeAt(position)
        // (2) 삭제된 아이템이 제외된 데이터를, sharedPreferences 에 저장해줘야 함
        SharedPreferenceManager.storeSearchHistoryList(searchHistoryList)
        // (3) 삭제된 아이템이 있다고 어댑터에 알려줘야 함 - 삭제(1)후 저장(2)을 거쳐서 리사이클러뷰를 다시 돌리기(3) 때문에 자동적으로 해당 뷰가 빠지게 됨
        searchHistoryRecyclerViewAdapter.notifyDataSetChanged()
    }

    // 검색 목록 클릭 메소드 : ISearchHistoryRecyclerView 인터페이스에서 구현
    override fun onSearchItemClicked(position: Int) {
        Log.d("searchHistoryClicked", position.toString())
        // 검색 목록에 있는 아이템을 클릭했을 때, 해당 텍스트의 내용을 재검색(api 호출) 하는 식으로 구현할 것임

    }

    // 검색 목록 아이템의 텍스트로 api 호출
    // 검색어(query)를 매개변수로 넣어줌
    private fun searchPhotoApiCall(query: String) {
        RetrofitManager.instance.searchPhotos(
            searchTerm = query,
            completion = { status, photoArrayList ->
                when (status) {
                    RESPONSE_STATUS.OK -> {
                        // 새로 검색됨에 따라,
                        if (photoArrayList != null) {
                            // 기존 검색 결과가 담겨있던 photoDataList 를 clear() 하고,
                            photoDataList.clear()
                            // 새 검색 결과를 photoDataList 에 담아준다.
                            photoDataList = photoArrayList

                            // TODO : API 호출 마무리
//                            photoGridRecyclerViewAdapter.submitList()
                        }
                        val intent = Intent(this, SearchCollectionActivity::class.java)

                        val bundle = Bundle()

                        bundle.putSerializable("search_data_serialized", photoArrayList)

                        intent.putExtra("search_data_bundle", bundle)

                        intent.putExtra("search_text", query)

                        startActivity(intent)
                    }
                    RESPONSE_STATUS.NO -> {
                        Toast.makeText(this, "api 호출 에러, 네트워크 상태를 확인해주세요.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    RESPONSE_STATUS.NO_CONTENTS -> {
                        Toast.makeText(this, "$query 에 대한 검색결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }
}