package kr.ac.kpu.ce2017154024.unsplash_app_withjeongdaeri

import android.app.Application

class app : Application(){
    companion object{
        lateinit var instance: app
            private set
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }
}