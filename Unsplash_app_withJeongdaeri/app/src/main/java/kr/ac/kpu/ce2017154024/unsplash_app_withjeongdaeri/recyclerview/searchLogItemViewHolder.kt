package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_search_item.view.*
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.model.SearchData

class searchLogItemViewHolder(itemView: View,searchRecylcerviewInterface: ISearchHistoryRecylcerview):RecyclerView.ViewHolder(itemView),
    View.OnClickListener{
    //클릭할때는 생성자로 링킹처리해줘야함

    private var mySearchRecyclerViewInterface:ISearchHistoryRecylcerview
    private val timestamp = itemView.search_time_textView
    private val Log = itemView.Search_name_textView
    private val deletebtn = itemView.delete_search_btn
    private val constraintSearchItem = itemView.constraint_search_item
    init {

        //리스너 연결
        deletebtn.setOnClickListener(this)
        constraintSearchItem.setOnClickListener(this)
        this.mySearchRecyclerViewInterface=searchRecylcerviewInterface
    }
    fun bindWithView(data:SearchData){
        timestamp.text=data.timeStamp
        Log.text=data.term

    }

    override fun onClick(p0: View?) {
        when(p0){
            deletebtn ->{
                this.mySearchRecyclerViewInterface.onSearchItemDeleteClicked(adapterPosition)
            }
            constraintSearchItem ->{
                this.mySearchRecyclerViewInterface.onSearchItemClicked(adapterPosition)
            }
        }
    }

}
