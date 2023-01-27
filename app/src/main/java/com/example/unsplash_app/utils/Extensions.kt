package com.example.unsplash_app

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

// editText 에 대한 커스텀함수를 만든 것
// 사용 시에는 사용할 activity, fragment 에 myAddTextChanged 를 호출하여 사용할 수 있다.

fun EditText.myAddTextChanged(completion: (Editable?) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(editable: Editable?) {
            completion(editable)
        }

    })
}

// 로깅 인터셉터 관련 확장함수를 만든 것

// 1. 데이터가 jsonObject 형태인지
fun String?.isJsonObject(): Boolean {
    return this?.startsWith("{") == true && this.endsWith("}")
}

// 2. 데이터가 jsonArray 형태인지
fun String?.isJsonArray() : Boolean {
    return this?.startsWith("[") == true && this.endsWith("]")
}
