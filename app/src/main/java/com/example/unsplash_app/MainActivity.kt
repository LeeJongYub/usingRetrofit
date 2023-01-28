package com.example.unsplash_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.unsplash_app.Constants.TAG
import com.example.unsplash_app.databinding.ActivityMainBinding
import com.example.unsplash_app.retrofit.RetrofitManager
import com.google.gson.JsonElement

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
        // TextWatcherExtensions 에 커스텀 함수(확장함수) - myAddTextChanged 생성함

        // 텍스트가 변경이 되었을 때
        binding.editText1.myAddTextChanged {
            // 한글자라도 입력되었을 때
            if (it.toString().count() > 0) {
                // 버튼 활성화
                binding.searchButton.visibility = View.VISIBLE
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
            // 사진 검색 api 호출
            RetrofitManager.instance.searchPhotos(
                searchTerm = binding.editText1.text.toString(),
                completion = { responseState, responseBody ->

                    when (responseState) {
                        RESPONSE_STATE.OK -> {
                            Toast.makeText(this, "api 호출 성공입니다.", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, responseBody)

                        }
                        RESPONSE_STATE.NO -> {
                            Toast.makeText(this, "api 호출 에러입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                })

        }

    } // onCreate
}