package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.R
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.app
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.model.Photo

class PhotoGridRecyclerViewAdapter :RecyclerView.Adapter<PhotoItemViewHolder>(){
    private var photoList = ArrayList<Photo>()
    //뷰홀더와 레이아웃연결
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemViewHolder {
        val photoItemViewHolder = PhotoItemViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_photo_item,parent,false))
        return  photoItemViewHolder
    }

    override fun onBindViewHolder(holder: PhotoItemViewHolder, position: Int) {
        holder.bindWithView(this.photoList[position])
    }

    override fun getItemCount(): Int {
        return this.photoList.size
    }
    //외부에서 어뎁터에 데이터 넣음
    fun submitList(photoList1:ArrayList<Photo>){
        this.photoList=photoList1
    }


}