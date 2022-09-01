package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.retrofit

import com.google.gson.JsonElement
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.API
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IRetrofit {
    //https://www.unsplash.com/search/photos/?query="$serachTerm"
    //jsonelement가 반환하는 값임
    @GET(API.SEARCH_PHOTO)
    fun searchPhotos(@Query("query") searchTerm: String): Call<JsonElement>

    @GET(API.SEARCH_USERS)
    fun searchUsers(@Query("query") searchTerm: String):Call<JsonElement>
}