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

class HintUpdateDialog(context: Context, private val versionBean: AppVersionBean) :
    Dialog(context, com.mitsuki.update.R.style.UpdateDialogStyle) {

    lateinit var confirm: () -> Unit

    private var tag: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_update)


        versionConfirm.setOnClickListener {
            confirm.invoke()
        }
        versionClose.setOnClickListener {
            if (!versionBean.forceUpdate) dismiss()
        }

        setCancelable(!versionBean.forceUpdate)
        versionClose.visibility = if (versionBean.forceUpdate) View.INVISIBLE else View.VISIBLE
    }
}