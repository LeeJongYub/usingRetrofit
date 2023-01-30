package com.example.unsplash_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.unsplash_app.Constants.TAG
import com.example.unsplash_app.databinding.ActivityMainBinding
import com.example.unsplash_app.retrofit.RetrofitManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Constants 클래스에 설정해놓은 enum class 의 변수들을 통해 초기 클릭을 지정,
    // 클릭이 변하였다는걸 알려줘야하는 부분에 enum class 의 변수명을 써서 값을 넣음
    private var searchType: SEARCH_TYPE = SEARCH_TYPE.PHOTO

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio1 -> {
                        textField.hint = "사진검색"
                        textField.startIconDrawable = resources.getDrawable(
                            R.drawable.ic_baseline_photo_library_24,
                            resources.newTheme()
                        )
                        this@MainActivity.searchType = SEARCH_TYPE.PHOTO
                    }
                    R.id.radio2 -> {
                        textField.hint = "사용자검색"
                        textField.startIconDrawable = resources.getDrawable(
                            R.drawable.ic_baseline_user_24,
                            resources.newTheme()
                        )
                        this@MainActivity.searchType = SEARCH_TYPE.USER
                    }
                }
            }
        }

        // 확장함수를 통해서 사용하고 싶은 메서드(afterTextChanged) 기능만 사용하려고 함
        // Extensions 에 커스텀 함수(확장함수) - myAddTextChanged 생성함

        // 텍스트가 변경이 되었을 때
        binding.editText1.myAddTextChanged {
            // 한글자라도 입력되었을 때
            if (it.toString().count() > 0) {
                // 버튼 활성화
                binding.searchButton.visibility = View.VISIBLE
                // 타이핑할 때, 검색 버튼이 키패드에 가려 보이지 않는 현상이 있었음
                // 이를 방지하기 위해 타이핑시 scrollView 를 scrollTo 를 통해 강제적으로 위로 올림
                binding.mainScrollview.scrollTo(0, 200)
                binding.textField.helperText = ""
            } else {
                // 버튼 비활성화
                binding.searchButton.visibility = View.INVISIBLE
                binding.textField.helperText = "검색어를 입력해주세요."
            }

            if (it.toString().count() == 12) {
                Toast.makeText(this, "입력 가능한 최대 글자수는 12글자입니다.", Toast.LENGTH_SHORT).show()
            }

            Log.d(TAG, binding.editText1.text.toString())
        }



        // 원래 사용하는 textWatcher 관련 코드

//        binding.editText1.addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                TODO("Not yet implemented")
//            }
//
//        })


        // 검색 버튼 클릭시
        binding.searchButton.setOnClickListener {

            val searchText = binding.editText1.text.toString()

            // 사진 검색 api 호출
            RetrofitManager.instance.searchPhotos(
                searchTerm = binding.editText1.text.toString(),
                completion = { responseState, responseBody ->

                    when (responseState) {
                        RESPONSE_STATUS.OK -> {
                            Toast.makeText(this, "api 호출 성공입니다.", Toast.LENGTH_SHORT).show()

                            // 검색하여 데이터가 잘 받아와지는 것 까지 확인했으니,
                            // 버튼 클릭시 다음 화면으로 넘어가며 데이터를 넘겨주려고 한다.
                            val intent = Intent(this, SearchCollectionActivity::class.java)

                            // 변수를 선언하여 번들 형태로 데이터를 직렬화해준다.
                            val bundle = Bundle()

                            bundle.putSerializable("search_data_serialized", responseBody)

                            // 직렬화된 데이터를 Intent 때 넘겨줄 수 있도록 putExtra 에 넣어준다.
                            intent.putExtra("search_data_bundle", bundle)

                            // 툴바를 만들어 검색한 텍스트가 무엇인지 표시하려고 하므로, 검색한 내용도 putExtra 로 넘겨준다.
                            intent.putExtra("search_text", searchText)

                            // Intent 시 넘겨줄 데이터를 다 설정했으므로 startActivity 를 실행하여 인탠트한다.
                            startActivity(intent)

                        }
                        RESPONSE_STATUS.NO -> {
                            Toast.makeText(this, "api 호출 에러입니다.", Toast.LENGTH_SHORT).show()
                        }
                        RESPONSE_STATUS.NO_CONTENTS -> {
                            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // 검색 버튼 누른후 화면전환 되었을 때, 검색화면에서의 editText 를 비워주기 위함
                    binding.editText1.setText("")

                })

        }

    } // onCreate
}