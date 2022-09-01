package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Photo  (var thumbnail: String?,
                  var author: String?,
                  var createdAt:String,
                  var likesCount:Int?):Serializable {

}