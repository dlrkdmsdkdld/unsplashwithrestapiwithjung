package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.recyclerview

interface ISearchHistoryRecylcerview {

    //검색아이템 삭제 버튼 클릭
    fun onSearchItemDeleteClicked(position: Int)

    //검색버튼 클릭
    fun onSearchItemClicked(position: Int)
}