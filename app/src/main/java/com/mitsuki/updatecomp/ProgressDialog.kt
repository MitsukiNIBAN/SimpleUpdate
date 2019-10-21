package com.mitsuki.updatecomp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.ProgressBar
import com.mitsuki.update.*
import kotlinx.android.synthetic.main.dialog_update.*

class ProgressDialog(context: Context) :
    Dialog(context, com.mitsuki.update.R.style.UpdateDialogStyle) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress)
        setCancelable(false)
    }

    fun setProgress(progress:Int){
        versionProgress.progress = progress
    }
}