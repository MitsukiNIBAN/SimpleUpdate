package com.mitsuki.updatecomp

import android.app.Application
import com.mitsuki.update.UpdateController
import kotlin.concurrent.timer

class MyApplication :Application(){
    override fun onCreate() {
        super.onCreate()


        UpdateController.init(this)
    }
}