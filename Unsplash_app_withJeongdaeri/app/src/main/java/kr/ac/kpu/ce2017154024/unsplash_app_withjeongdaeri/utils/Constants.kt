package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils

object Constants {
    const val TAG : String = "로그"
}
enum class RESPONSE_STATUS{
    OKAY,
    FAIL,
    NO_CONTENT
}
enum class SEARCH_TYPE {
    PHOTO,
    USER
}
object API{
    const val BASE_URL : String = "https://api.unsplash.com/"
    const val CLIENT_ID : String="rYVaAxhvWULSuQxWmcBRZVSn9b2YUiQHbSYpURcvkEs"
    const val SEARCH_PHOTO : String ="search/photos"
    const val SEARCH_USERS : String ="search/users"
}