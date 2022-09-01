package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_photo_item.view.*
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.R
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.app
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.model.Photo

class PhotoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    //뷰들을 가져온다.
    private val photoIamgeView = itemView.photo_image
    private val photoCreatedAtText = itemView.created_at_textView
    private val photoLikesCountText = itemView.like_count_text


    //데이터와 뷰를 묶는다.
    fun bindWithView(photoItem: Photo){

        photoCreatedAtText.text=photoItem.createdAt
        photoLikesCountText.text=photoItem.likesCount.toString()
        //이미지를 설정한다 글라이드를 통해서
        Glide.with(app.instance)
            .load(photoItem.thumbnail)
            .placeholder(R.drawable.ic_baseline_insert_photo_24)
            .into(photoIamgeView)
    }
}