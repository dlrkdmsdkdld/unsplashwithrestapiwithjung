package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.retrofit

import android.os.Looper
import android.util.Log
import android.os.Handler
import android.widget.Toast
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.app
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.API
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.Constants.TAG
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.isJsonArray
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.isJsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Tag
import java.util.concurrent.TimeUnit

//싱글톤
object RetrofitClient {
    //레트로핏 클라이언트 선언
    private var retrofitClient: Retrofit? = null
    //레트로핏 클라이언트 가져오기
    fun getClient(baseurl:String):Retrofit?{
        Log.d(TAG,"RetorfitClient - getClient() called" )
        //okhttp 인스턴스 생성
        val client = OkHttpClient.Builder()
            //로그를 찍기 위해 로깅 인터셉터 설정
        val loggingInterceptor = HttpLoggingInterceptor(object :HttpLoggingInterceptor.Logger{
            override fun log(message: String) {
                Log.d(TAG,"RetrofitClient - log() called / message: $message")
                when{
                    message.isJsonObject() -> Log.d(TAG,JSONObject(message).toString(4))

                    message.isJsonArray() ->Log.d(TAG,JSONObject(message).toString(4))
                    else ->{
                        try {
                            Log.d(TAG,JSONObject(message).toString(4))
                        }catch (e:Exception){
                            Log.d(TAG,message)
                        }
                    }

                }
            }

        })
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        //위에서 설정한 로깅 인터셉터를 okhttp 클라이언트에 추가한다.
        client.addInterceptor(loggingInterceptor)
        //기본 파라미터 인터셉터 설정
        val baseParameterInterceptor : Interceptor = (object  : Interceptor{
            override fun intercept(chain: Interceptor.Chain): Response {
                Log.d(TAG,"RetrofitClient - Interceptor called ")
                val originalRequest = chain.request()
                //쿼리 파라미터 추가하기
                //클라이언트 아이디를 넣엇음 ?client_id = 값
                val addedUrl = originalRequest.url.newBuilder().addQueryParameter("client_id", API.CLIENT_ID).build()
                val finalRequest = originalRequest.newBuilder()
                                        .url(addedUrl)
                    .method(originalRequest.method,originalRequest.body)
                    .build()
//                return chain.proceed(finalRequest)
                val response = chain.proceed(finalRequest)
                if(response.code !=200){
                    Handler(Looper.getMainLooper()).post{
                        Toast.makeText(app.instance, "${response.code} 에러입니다", Toast.LENGTH_SHORT).show()
                    }
                }
                return response
            }

        })
        //위에서 설정한 기본인터셉터를 okhttp 클라이언트에 추가한다.
        client.addInterceptor(baseParameterInterceptor)
        //커넷션 타임아웃
        client.connectTimeout(10,TimeUnit.SECONDS)
        client.readTimeout(10,TimeUnit.SECONDS)
        client.writeTimeout(10,TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true) // 실패했을때 다시시도



        if(retrofitClient == null){
            //레트로핏 빌더
            retrofitClient=Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                //위에서 설정한 클라이언트로 레트로핏 클라이언트를 설정한다.
                .client(client.build())
                .build()
        }
        return retrofitClient
    }


}