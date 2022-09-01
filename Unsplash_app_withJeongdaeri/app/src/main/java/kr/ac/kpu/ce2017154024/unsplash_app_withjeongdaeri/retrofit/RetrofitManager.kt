package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.retrofit

import android.util.Log
import com.google.gson.JsonElement
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.model.Photo
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.API
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.Constants.TAG
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.RESPONSE_STATUS

import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat

class RetrofitManager {
    companion object{
        val instance = RetrofitManager()
    }
    //HTTP 콜 만들기
    //레트로핏 클라이언트에 인터페이스를 넣어서 가져오기
    private val iRetrofit : IRetrofit? = RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
    //끝났을때 결과를 string -> unit
    //사진 검색 api 호출
    fun searchPhotos(searchTerm:String?,completion:(RESPONSE_STATUS, ArrayList<Photo>?) -> Unit){
        val term = searchTerm ?: ""
        //val call = iRetrofit?.searchPhotos(searchTerm = term) ?: return 과 같은 뜻임
        val call = iRetrofit?.searchPhotos(searchTerm = term).let {
            it
        }?: return

        call.enqueue(object  : retrofit2.Callback<JsonElement>{
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG,"Retrofitmanager - onResponse() called /reponse: ${response.raw()}")

                when(response.code()){
                    200 ->{
                        response.body()?.let {
                            var parsedPhotoDataArray = ArrayList<Photo>()
                            val body = it.asJsonObject
                            val results = body.getAsJsonArray("results")
                            val total = body.get("total").asInt
                            Log.d(TAG,"RetrofitManager - onResponse() called / total: $total")
                            if(total==0){//데이터가 없으면 nocontent로 보냄
                                completion(RESPONSE_STATUS.NO_CONTENT,null)
                            }else{
                            results.forEach { resultItem->
                                val resultItmeObject = resultItem.asJsonObject

                                val user = resultItmeObject.get("user").asJsonObject
                                val username :String =user.get("username").asString
                                val likeCounts:Int = resultItmeObject.get("likes").asInt
                                val thumbnailLink = resultItmeObject.get("urls").asJsonObject.get("thumb").asString
                                val createdAt = resultItmeObject.get("created_at").asString
                                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                val formatter = SimpleDateFormat("yyyy년\nMM월 dd일")
                                val outputDateString = formatter.format(parser.parse(createdAt))
                                val photoItem = Photo(author = username, likesCount = likeCounts, thumbnail =thumbnailLink, createdAt = outputDateString)
                                parsedPhotoDataArray.add(photoItem)
                            }
                            completion(RESPONSE_STATUS.OKAY,parsedPhotoDataArray)
                            }
                        }
                    }
                }





            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG,"Retrofitmanager - onFailure() called /t:$t")
                completion(RESPONSE_STATUS.FAIL,null)
            }

        })
    }

}