package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_photo_collection.*
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.model.Photo
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.recyclerview.PhotoGridRecyclerViewAdapter
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.Constants.TAG

class photoCollectionActivity:AppCompatActivity() {
    var photoList = ArrayList<Photo>()
    //어답터
    private lateinit var photoGridRecyclerViewAdapter: PhotoGridRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_collection)
        Log.d(TAG,"photoCollectionActivity - onCreate() called")

        val bundle=intent.getBundleExtra("array_bundle")
        val searchTerm = intent.getStringExtra("search_term")

        photoList = bundle?.getSerializable("photo_array_list") as ArrayList<Photo>
        Log.d(TAG,"photoCollectionActivity - onCreate() called / searchTerm:$searchTerm,photoList.count:${photoList.count()}")

        topAppBar.title=searchTerm
        this.photoGridRecyclerViewAdapter= PhotoGridRecyclerViewAdapter()
        this.photoGridRecyclerViewAdapter.submitList(photoList)
        my_photo_recycler_view.layoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)
        my_photo_recycler_view.adapter=this.photoGridRecyclerViewAdapter


    }
}