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

class ProgressUpdateDialog(context: Context, private val versionBean: AppVersionBean) :
    Dialog(context, com.mitsuki.update.R.style.UpdateDialogStyle) {


    lateinit var apkPath: String
    lateinit var confirm: () -> Unit
    lateinit var install: (String) -> Unit
    lateinit var retry: () -> Unit

    private var tag: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_update)


        versionConfirm.setOnClickListener {
            if (!versionBean.forceUpdate) dismiss()
            if (tag == DOWNLOAD_FAIL) {
                retry.invoke()
            } else {
                confirm.invoke()
            }
        }
        versionClose.setOnClickListener {
            if (!versionBean.forceUpdate) dismiss()
        }
        versionInstall.setOnClickListener {
            install.invoke(apkPath)
        }

        setCancelable(!versionBean.forceUpdate)
        versionClose.visibility = if (versionBean.forceUpdate) View.INVISIBLE else View.VISIBLE
    }

    fun onDownloadEvent(msg: Message) {
        tag = msg.what
        when (msg.what) {
            DOWNLOAD_START -> {
                versionInstall.visibility = View.GONE
                versionConfirm.visibility = View.GONE
                versionProgress.visibility = View.VISIBLE
            }

            DOWNLOAD_SUCCESS -> {
                apkPath = msg.obj as String
                versionInstall.visibility = View.VISIBLE
                versionConfirm.visibility = View.GONE
                versionProgress.visibility = View.GONE
            }
            DOWNLOAD_FAIL -> {
                versionInstall.visibility = View.GONE
                versionConfirm.visibility = View.VISIBLE
                versionProgress.visibility = View.GONE

                versionConfirm.text = "重  试"
            }
            DOWNLOADING -> versionProgress.progress = msg.obj as Int
        }
    }
}