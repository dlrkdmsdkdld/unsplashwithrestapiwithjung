package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_photo_collection.*
import kotlinx.android.synthetic.main.layout_button_search.*
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.model.Photo
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.recyclerview.PhotoGridRecyclerViewAdapter
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.retrofit.RetrofitManager
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.Constants.TAG
import kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri.utils.RESPONSE_STATUS

class photoCollectionActivity:AppCompatActivity()
,SearchView.OnQueryTextListener
,CompoundButton.OnCheckedChangeListener
,View.OnClickListener{
    var photoList = ArrayList<Photo>()
    //어답터
    private lateinit var photoGridRecyclerViewAdapter: PhotoGridRecyclerViewAdapter
    //서치뷰
    private lateinit var mySearchView: SearchView
    //서치뷰 에딧 텍스트
    private lateinit var mySearchViewEditText: EditText
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

        topAppBar.title=searchTerm
        //액티비티에서 어떤 액션바를 사용할 건지 설정한다.
        setSupportActionBar(topAppBar)



        this.photoGridRecyclerViewAdapter= PhotoGridRecyclerViewAdapter()
        this.photoGridRecyclerViewAdapter.submitList(photoList)
        my_photo_recycler_view.layoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)
        my_photo_recycler_view.adapter=this.photoGridRecyclerViewAdapter


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
                    }
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
        }

        this.mySearchView.setQuery("",false)
        this.mySearchView.clearFocus()
        this.topAppBar.collapseActionView()

        RetrofitManager.instance.searchPhotos(searchTerm = p0, completion = {
                responseState,responseDataArrayList ->

            when(responseState){
                RESPONSE_STATUS.OKAY ->{
                    Log.d(TAG, "api 호출 성공 : ${responseDataArrayList?.size} ")
                    this.photoGridRecyclerViewAdapter.removeAllData()
                    this.photoGridRecyclerViewAdapter.submitList(responseDataArrayList as ArrayList<Photo>)
                    my_photo_recycler_view.adapter=this.photoGridRecyclerViewAdapter

                }
                RESPONSE_STATUS.FAIL ->{
                    Log.d(TAG, "api 호출 실패패 : $responseDataArrayList ")
                    Toast.makeText(this,"api 호출 에러입니다.",Toast.LENGTH_SHORT).show()
                }
                RESPONSE_STATUS.NO_CONTENT ->{
                    Toast.makeText(this, "검색결과가 없습니다",Toast.LENGTH_SHORT).show()
                }
            }

        })



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
                }else{
                    Log.d(TAG,"검색어 저장기능 끔")
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when(view){
            clear_search_history_buttton ->{
                Log.d(TAG,"검색했던것 삭제버튼이 클릭되었다")
            }
        }
    }
}