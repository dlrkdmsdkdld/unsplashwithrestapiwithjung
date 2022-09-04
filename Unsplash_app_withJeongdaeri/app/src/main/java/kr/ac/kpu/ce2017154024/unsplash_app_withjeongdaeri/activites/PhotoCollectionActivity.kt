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
import kotlin.collections.ArrayList

class photoCollectionActivity:AppCompatActivity()
,SearchView.OnQueryTextListener
,CompoundButton.OnCheckedChangeListener
,View.OnClickListener,
ISearchHistoryRecylcerview{
    var photoList = ArrayList<Photo>()
    //어답터
    private lateinit var photoGridRecyclerViewAdapter: PhotoGridRecyclerViewAdapter
    //서치뷰
    private lateinit var mySearchView: SearchView
    //서치뷰 에딧 텍스트
    private lateinit var mySearchViewEditText: EditText
    //검색기록배열
    private var searchHistoryList=ArrayList<SearchData>()


    private var Loga=ArrayList<String>()
    private var TimeStampa=ArrayList<String>()
    private lateinit var searchLogViewAdapter: searchLogRecyclerViewAdapater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_collection)
        Log.d(TAG,"photoCollectionActivity - onCreate() called")

        val bundle=intent.getBundleExtra("array_bundle")
        val searchTerm = intent.getStringExtra("search_term")

        //설정해준 클릭리스너 , 체크리스너들 컴포넌트에 연결
        search_history_mode_switch.setOnCheckedChangeListener(this)
        clear_search_history_buttton.setOnClickListener(this)


        photoList = bundle?.getSerializable("photo_array_list") as ArrayList<Photo>
        Log.d(TAG,"photoCollectionActivity - onCreate() called / searchTerm:$searchTerm,photoList.count:${photoList.count()}")
        search_history_mode_switch.isChecked=SharedPrefMnager.checkSearchHistoryMode()
        topAppBar.title=searchTerm
        //액티비티에서 어떤 액션바를 사용할 건지 설정한다.
        setSupportActionBar(topAppBar)



        this.photoGridRecyclerViewAdapter= PhotoGridRecyclerViewAdapter()
        this.photoGridRecyclerViewAdapter.submitList(photoList)
        my_photo_recycler_view.layoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)
        my_photo_recycler_view.adapter=this.photoGridRecyclerViewAdapter
        //저장된 검색기록 가져오기
        this.searchHistoryList = SharedPrefMnager.getSearchHistoryList() as ArrayList<SearchData>

        this.searchHistoryList.forEach {

            Log.d(TAG,"저장된 검색기록 it.term : ${it.term} ,it.timestamp: ${it.timeStamp}")
        }
        handleSearchViewUI()
        Log.d(TAG,"저장된 검색기록 it.term : ${searchHistoryList}")
        searchViewSetting(searchHistoryList)
        if (searchTerm!!.isNotEmpty()){
            val term = searchTerm?.let {
                it
            }?: ""
            this.insettSearchTermHistory(term)
        }

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
        //리소스 연결 매개변수로 들어오는 메뉴와 topmenu를 연결
        inflater.inflate(R.menu.top_app_bar_menu,menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        this.mySearchView = menu?.findItem(R.id.search_menu_item)?.actionView as SearchView

        //서치뷰에 대한여러가지 설정
        this.mySearchView.apply {
            this.queryHint="검색어를 입력해주세요"

            this.setOnQueryTextFocusChangeListener { _, hasExpaned ->
                when(hasExpaned){
                    true -> {
                        Log.d(TAG,"서치뷰 열림")
                        linear_searchview.visibility= View.VISIBLE
                        handleSearchViewUI()  }
                    false ->{
                        Log.d(TAG,"서치뷰 닫힘")
                        linear_searchview.visibility= View.INVISIBLE
                    }
                }
            this.setOnQueryTextListener(this@photoCollectionActivity)
            }

            //서치 뷰에서 에딧텍스트를 가져온다
            mySearchViewEditText =this.findViewById(androidx.appcompat.R.id.search_src_text)

        }
        this.mySearchViewEditText.apply {
            this.filters=arrayOf(InputFilter.LengthFilter(12))
            this.setTextColor(Color.WHITE)
            this.setHintTextColor(Color.WHITE)
        }
        return true
    }
    //서치뷰 검색 입력 이벤트
    //검색버튼이 클릭되었을때
    override fun onQueryTextSubmit(p0: String?): Boolean {
        Log.d(TAG,"PhotoCollectionAcititvy - onQueryTextSubmit() called / query:$p0")
        if (!p0.isNullOrEmpty()){
            this.topAppBar.title=p0
            //여기서부터 api 호출하면된다
            //검색어 저장
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
            Toast.makeText(this,"검색어는 12자 까지만 입력가능합니다",Toast.LENGTH_SHORT).show()
        }

        return true
    }

    override fun onCheckedChanged(switch: CompoundButton?, isChecked: Boolean) {
        when(switch){
            search_history_mode_switch ->{
                if (isChecked==true){
                    Log.d(TAG,"검색어 저장기능 온")
                    SharedPrefMnager.setSearchHistoryMode(isActivated = isChecked)
                }else{
                    SharedPrefMnager.setSearchHistoryMode(isActivated = false)
                    Log.d(TAG,"검색어 저장기능 끔")
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when(view){
            clear_search_history_buttton ->{
                Log.d(TAG,"검색했던것 삭제버튼이 클릭되었다")
                SharedPrefMnager.clearSearchHistoryList()
                this.searchHistoryList.clear()
                handleSearchViewUI()
            }
        }
    }

    override fun onSearchItemDeleteClicked(position: Int) {
    Log.d(TAG,"position = $position")
        //해당요소 삭제
        this.searchHistoryList.removeAt(position)
        //데이터 없애고 덮어쓰기
        SharedPrefMnager.storeSearchHistoryList(this.searchHistoryList)
        //데이터 변경 알려줌
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
    //사진 검색 api 호출
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
                Toast.makeText(this,"$query 에 대한 검색결과가 없습니다",Toast.LENGTH_SHORT).show()
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
    //검색어 저장 
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