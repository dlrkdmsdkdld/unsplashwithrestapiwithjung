package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils


import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.Constants.TAG
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.CharSequence as CharSequence

//문자열이 제이슨 형태인지, 제이슨 배열 형태인지
fun String?.isJsonObject():Boolean {
    if (this?.startsWith("{" ) == true &&this.endsWith("}")){
        return  true
    }else{
        return false
    }
    //return this?.startsWith("{" ) == true &&this.endsWith("}")
}
fun String?.isJsonArray():Boolean{
    if (this?.startsWith("[" ) == true &&this.endsWith("]")){
        return  true
    }else{
        return false
    }
}
fun Date.toSimpleString() : String{
    val format = SimpleDateFormat("HH:mm:ss")
    return format.format(this)
}

// 에딧 텍스트에 대한 익스텐션
fun EditText.onMyTextChanged(completion: (Editable?) -> Unit){
    this.addTextChangedListener(object: TextWatcher {

        override fun afterTextChanged(editable: Editable?) {
            completion(editable)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

    })
}
/*
@ExperimentalCoroutinesApi
fun EditText.textChangesToFlow(): Flow<CharSequence?> {

    // flow 콜백 받기
    return callbackFlow<CharSequence?> {

        val listener = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun afterTextChanged(p0: Editable?) {
                Unit
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d(TAG, "onTextChanged() / textChangesToFlow() 에 달려있는 텍스트 와쳐 / text : $text")
                // 값 내보내기
                offer(text)
            }
        }
        // 위에서 설정한 리스너 달아주기
        addTextChangedListener(listener)

        // 콜백이 사라질때 실행됨
        awaitClose {
            Log.d(TAG, "textChangesToFlow() awaitClose 실행")
            removeTextChangedListener(listener)
        }

    }.onStart {
        Log.d(TAG, "textChangesToFlow() / onStart 발동")
        // Rx 에서 onNext 와 동일
        // emit 으로 이벤트를 전달
        emit(text)
    }

}

 */