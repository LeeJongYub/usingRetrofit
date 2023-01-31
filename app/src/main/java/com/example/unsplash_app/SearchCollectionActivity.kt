package com.example.unsplash_app

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter.LengthFilter
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.GridLayoutManager
import com.example.unsplash_app.data.Photo
import com.example.unsplash_app.data.SearchData
import com.example.unsplash_app.databinding.ActivitySearchCollectionBinding
import com.example.unsplash_app.recyclerview.PhotoGridRecyclerViewAdapter
import java.util.Date

class SearchCollectionActivity : AppCompatActivity(), OnQueryTextListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
// onQueryTextListener 를 통해 서치뷰의 입력상태 변화, 키패드의 검색버튼 클릭 여부에 따른 기능을 추가할 수 있다.
// compoundButton.OnCheckedChangeListener 를 통해 switch 토클의 온/오프 여부에 따른 기능을 추가할 수 있다.
// View.OnClickListener 를 통해 뷰의 클릭 여부에 따른 기능을 추가할 수 있다.

    private lateinit var binding: ActivitySearchCollectionBinding

    // Intent 하여 해당 액티비티로 받아온 데이터를 담아둘 리스트 선언
    private var photoDataList = ArrayList<Photo>()

    // 만들어둔 어댑터 호출을 위한 선언
    private lateinit var photoGridRecyclerViewAdapter: PhotoGridRecyclerViewAdapter

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

        // 번들 데이터를 받음
        val bundle = intent.getBundleExtra("search_data_bundle")

        // 툴바에 표시하기 위해 보냈던 데이터도 받음 (검색한 텍스트)
        val getSearchText = intent.getStringExtra("search_text")

        photoDataList = bundle?.getSerializable("search_data_serialized") as ArrayList<Photo>

        Log.d("getSearchText", getSearchText.toString())

        this.photoGridRecyclerViewAdapter = PhotoGridRecyclerViewAdapter()

        this.photoGridRecyclerViewAdapter.submitList(photoDataList)

        binding.myPhotoRecyclerView.adapter = photoGridRecyclerViewAdapter
        binding.myPhotoRecyclerView.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        // 툴바 타이틀에, 검색한 텍스트를 배치한다
        binding.topAppBar.title = getSearchText
        // 툴바 타이틀 색상 변경
        binding.topAppBar.setTitleTextColor(Color.WHITE)

        // 만들어 놓은 툴바를 세팅한다
        setSupportActionBar(binding.topAppBar)

        // class 에 설정해둔 리스너 타입(CompoundButton.OnCheckedChangeListener ~)에 대해 onCreate 메서드에서
        // 서로 연결을 해줘야 실행이 정상적으로 작동한다.
        binding.searchHistoryModeSwitch.setOnCheckedChangeListener(this) // this : CompoundButton.OnCheckedChangeListener
        binding.clearSearchHistoryButton.setOnClickListener(this) // this : View.OnClickListener

        // onCreate 메소드에 저장된 검색기록 가져오기
        searchHistoryList = SharedPreferenceManager.getSearchHistoryList() as ArrayList<SearchData>

        searchHistoryList.forEach{
            Log.d("searchHistoryList", "${it.term}, ${it.timeStamp}")
        }

    } // onCreate

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
            val newSearchData = SearchData(term = query, timeStamp = Date().toDate())

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
        when(switchToggle) {
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
        when(view) {
            binding.clearSearchHistoryButton ->
                Log.d("searchClearButton", "검색기록 삭제 버튼이 클릭되었다.")
        }
    }
}