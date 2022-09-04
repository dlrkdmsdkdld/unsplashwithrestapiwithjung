package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.R
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.model.SearchData

class searchLogRecyclerViewAdapater(searchHistoryRecylcerview: ISearchHistoryRecylcerview): RecyclerView.Adapter<searchLogItemViewHolder>(){
    private var searchLogArray =ArrayList<SearchData>()
    private var iSearchHistoryRecylcerview:ISearchHistoryRecylcerview?=null
    init {
        this.iSearchHistoryRecylcerview = searchHistoryRecylcerview
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): searchLogItemViewHolder {
        val itemViewHolder = searchLogItemViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_search_item,parent,false),this.iSearchHistoryRecylcerview!! )
          return itemViewHolder
    }

    override fun onBindViewHolder(holder: searchLogItemViewHolder, position: Int) {
        holder.bindWithView(this.searchLogArray[position])
    }

    override fun getItemCount(): Int {
        return this.searchLogArray.size
    }
    fun submit(Log:ArrayList<SearchData>){
        this.searchLogArray=Log


    }
}

