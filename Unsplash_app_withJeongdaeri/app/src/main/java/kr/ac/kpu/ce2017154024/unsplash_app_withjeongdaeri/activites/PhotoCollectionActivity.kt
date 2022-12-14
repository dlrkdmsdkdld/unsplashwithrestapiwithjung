package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.activites

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.*
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_photo_collection.*
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.R
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.model.Photo
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.model.SearchData
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.recyclerview.ISearchHistoryRecylcerview
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.recyclerview.PhotoGridRecyclerViewAdapter
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.recyclerview.searchLogRecyclerViewAdapater
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.retrofit.RetrofitManager
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.Constants.TAG
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.toSimpleString
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.RESPONSE_STATUS
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.SharedPrefMnager
import java.util.*
import java.util.Arrays.toString
import java.util.Objects.toString
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class photoCollectionActivity:AppCompatActivity()
,SearchView.OnQueryTextListener
,CompoundButton.OnCheckedChangeListener
,View.OnClickListener,
ISearchHistoryRecylcerview{
    var photoList = ArrayList<Photo>()
    //?????????
    private lateinit var photoGridRecyclerViewAdapter: PhotoGridRecyclerViewAdapter
    //?????????
    private lateinit var mySearchView: SearchView
    //????????? ?????? ?????????
    private lateinit var mySearchViewEditText: EditText
    //??????????????????
    private var searchHistoryList=ArrayList<SearchData>()

    private lateinit var searchLogViewAdapter: searchLogRecyclerViewAdapater

    //???????????? ??????????????? ?????? compositeDisposable
    private var myCompositDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_collection)
        Log.d(TAG,"photoCollectionActivity - onCreate() called")

        val bundle=intent.getBundleExtra("array_bundle")
        val searchTerm = intent.getStringExtra("search_term")

        //???????????? ??????????????? , ?????????????????? ??????????????? ??????
        search_history_mode_switch.setOnCheckedChangeListener(this)
        clear_search_history_buttton.setOnClickListener(this)


        photoList = bundle?.getSerializable("photo_array_list") as ArrayList<Photo>
        Log.d(TAG,"photoCollectionActivity - onCreate() called / searchTerm:$searchTerm,photoList.count:${photoList.count()}")
        search_history_mode_switch.isChecked=SharedPrefMnager.checkSearchHistoryMode()
        topAppBar.title=searchTerm
        //?????????????????? ?????? ???????????? ????????? ?????? ????????????.
        setSupportActionBar(topAppBar)



        this.photoGridRecyclerViewAdapter= PhotoGridRecyclerViewAdapter()
        this.photoGridRecyclerViewAdapter.submitList(photoList)
        my_photo_recycler_view.layoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)
        my_photo_recycler_view.adapter=this.photoGridRecyclerViewAdapter
        //????????? ???????????? ????????????
        this.searchHistoryList = SharedPrefMnager.getSearchHistoryList() as ArrayList<SearchData>

        this.searchHistoryList.forEach {

            Log.d(TAG,"????????? ???????????? it.term : ${it.term} ,it.timestamp: ${it.timeStamp}")
        }
        handleSearchViewUI()
        Log.d(TAG,"????????? ???????????? it.term : ${searchHistoryList}")
        searchViewSetting(searchHistoryList)
        if (searchTerm!!.isNotEmpty()){
            val term = searchTerm?.let {
                it
            }?: ""
            this.insettSearchTermHistory(term)
        }

    }

    override fun onDestroy() {
        //??????????????? ??????
        this.myCompositDisposable.clear()
        super.onDestroy()
    }
    private fun searchViewSetting(searchList:ArrayList<SearchData>){
        this.searchLogViewAdapter= searchLogRecyclerViewAdapater(this)
        this.searchLogViewAdapter.submit(searchHistoryList)
        val lm=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        my_search_recycler_view.apply {
            layoutManager = lm
            my_search_recycler_view.scrollToPosition(searchLogViewAdapter.itemCount-1)
            adapter=searchLogViewAdapter
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG,"photoCollectionActivity - onCreateOptionsMenu() called")
        val inflater = menuInflater
        //????????? ?????? ??????????????? ???????????? ????????? topmenu??? ??????
        inflater.inflate(R.menu.top_app_bar_menu,menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        this.mySearchView = menu?.findItem(R.id.search_menu_item)?.actionView as SearchView

        //???????????? ?????????????????? ??????
        this.mySearchView.apply {
            this.queryHint="???????????? ??????????????????"

            this.setOnQueryTextFocusChangeListener { _, hasExpaned ->
                when(hasExpaned){
                    true -> {
                        Log.d(TAG,"????????? ??????")
//                        linear_searchview.visibility= View.VISIBLE
                        handleSearchViewUI()
                    }
                    false ->{
                        Log.d(TAG,"????????? ??????")
                        linear_searchview.visibility= View.INVISIBLE
                    }
                }
            this.setOnQueryTextListener(this@photoCollectionActivity)
            }

            //?????? ????????? ?????????????????? ????????????
            mySearchViewEditText =this.findViewById(androidx.appcompat.R.id.search_src_text)
            //rx???????????? ?????? ??????????????? ??????????????? ?????? ????????? ????????????????????? ??????????????? ?????? ????????? ??????
            val editTextChangeObservable =mySearchViewEditText.textChanges()
            //????????? ?????????????????? 1????????? onNext ???????????? ????????? ???????????????
            val searchEditTextSubscription : Disposable = editTextChangeObservable.debounce(1000,TimeUnit.MILLISECONDS)
                //IO ??????????????? ????????????.
                .subscribeOn(Schedulers.io())
                    //????????? ?????? ????????? ???????????????
                .subscribeBy(
                    onNext = {
                        Log.d(TAG,"RX   onNExt: $it")
                        //TODO:: ??????????????? ????????? ???????????? api ??????
                        if(it.isNotEmpty()){
                            SearchPhotoAPICall(it.toString())
                        }
                    },
                    onComplete = {
                        Log.d(TAG,"RX   onComplete")

                    },
                    onError = {
                        Log.d(TAG,"RX   onError")

                    }
                )
            //compositeDisposalbe ??? ?????????
           myCompositDisposable.add(searchEditTextSubscription)

        }
        this.mySearchViewEditText.apply {
            this.filters=arrayOf(InputFilter.LengthFilter(12))
            this.setTextColor(Color.WHITE)
            this.setHintTextColor(Color.WHITE)
        }
        return true
    }
    //????????? ?????? ?????? ?????????
    //??????????????? ??????????????????
    override fun onQueryTextSubmit(p0: String?): Boolean {
        Log.d(TAG,"PhotoCollectionAcititvy - onQueryTextSubmit() called / query:$p0")
        if (!p0.isNullOrEmpty()){
            this.topAppBar.title=p0
            //??????????????? api ??????????????????
            //????????? ??????
            this.insettSearchTermHistory(p0)
            this.SearchPhotoAPICall(p0)

        }

        this.mySearchView.setQuery("",false)
        this.mySearchView.clearFocus()
        this.topAppBar.collapseActionView()




        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        Log.d(TAG,"PhotoCollectionAcititvy - onQueryTextChange() called / query:$p0")
        val userInputText = p0 ?:""
        if (userInputText.count()==12){
            Toast.makeText(this,"???????????? 12??? ????????? ?????????????????????",Toast.LENGTH_SHORT).show()
        }
//        if(userInputText.length in 1..12){
//            SearchPhotoAPICall(userInputText)
//        }
        return true
    }

    override fun onCheckedChanged(switch: CompoundButton?, isChecked: Boolean) {
        when(switch){
            search_history_mode_switch ->{
                if (isChecked==true){
                    Log.d(TAG,"????????? ???????????? ???")
                    SharedPrefMnager.setSearchHistoryMode(isActivated = isChecked)
                }else{
                    SharedPrefMnager.setSearchHistoryMode(isActivated = false)
                    Log.d(TAG,"????????? ???????????? ???")
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when(view){
            clear_search_history_buttton ->{
                Log.d(TAG,"??????????????? ??????????????? ???????????????")
                SharedPrefMnager.clearSearchHistoryList()
                this.searchHistoryList.clear()
                handleSearchViewUI()
            }
        }
    }

    override fun onSearchItemDeleteClicked(position: Int) {
    Log.d(TAG,"position = $position")
        //???????????? ??????
        this.searchHistoryList.removeAt(position)
        //????????? ????????? ????????????
        SharedPrefMnager.storeSearchHistoryList(this.searchHistoryList)
        //????????? ?????? ?????????
        this.searchLogViewAdapter.notifyDataSetChanged()
        handleSearchViewUI()

        }

    override fun onSearchItemClicked(position: Int) {
        val queryString = this.searchHistoryList[position].term
        SearchPhotoAPICall(queryString)
        topAppBar.title=queryString
        this.insettSearchTermHistory(searchTerm = queryString)
        this.topAppBar.collapseActionView()
    }
    //?????? ?????? api ??????
    private fun SearchPhotoAPICall(query:String){
        RetrofitManager.instance.searchPhotos(searchTerm = query, completion = {responseStatus, arrayList ->
        when(responseStatus){
            RESPONSE_STATUS.OKAY ->{
                if(arrayList!=null){
                    this.photoList.clear()
                    this.photoList=arrayList
                    this.photoGridRecyclerViewAdapter.submitList(this.photoList)
                    this.photoGridRecyclerViewAdapter.notifyDataSetChanged()
                }
            }
            RESPONSE_STATUS.NO_CONTENT->{
                Toast.makeText(this,"$query ??? ?????? ??????????????? ????????????",Toast.LENGTH_SHORT).show()
            }
        }
        })
    }
    private fun handleSearchViewUI(){
        Log.d(TAG,"handelSearchUI")
        if(this.searchHistoryList.size>0){
            my_search_recycler_view.visibility=View.VISIBLE
            history_labeled.visibility=View.VISIBLE
            clear_search_history_buttton.visibility=View.VISIBLE
        }else{
            my_search_recycler_view.visibility=View.INVISIBLE
            history_labeled.visibility=View.INVISIBLE
            clear_search_history_buttton.visibility=View.INVISIBLE
        }
    }
    //????????? ?????? 
    private fun insettSearchTermHistory(searchTerm:String){
        if (SharedPrefMnager.checkSearchHistoryMode() == true){
            val indexListToRemove = ArrayList<Int>()
            this.searchHistoryList.forEachIndexed{index, searchData ->
                if(searchData.term==searchTerm){
                    indexListToRemove.add(index)
                }

            }
            indexListToRemove.forEach {
                this.searchHistoryList.removeAt(it)
            }

            val newSearchData = SearchData(term = searchTerm, timeStamp = Date().toSimpleString())
            this.searchHistoryList.add(newSearchData)
            SharedPrefMnager.storeSearchHistoryList(this.searchHistoryList)
            this.searchLogViewAdapter.notifyDataSetChanged()
        }
    }
}