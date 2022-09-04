package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils


import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*

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