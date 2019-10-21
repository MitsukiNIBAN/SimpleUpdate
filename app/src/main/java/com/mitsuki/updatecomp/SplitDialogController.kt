package com.mitsuki.updatecomp

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Message
import android.view.View
import com.mitsuki.update.*
import kotlinx.android.synthetic.main.dialog_update.*

class SplitDialogController(private val context: Context, private val versionBean: AppVersionBean) :
    DialogController() {

    private val dialog: HintUpdateDialog =
        HintUpdateDialog(context, versionBean)
    private val progressDialog: ProgressDialog =
        ProgressDialog(context)

    init {
        dialog.confirm = { sendDownloadSignal() }
    }

    override fun mustBeUpdated(): Boolean {
        return versionBean.forceUpdate
    }

    override fun downloadIntent(): Intent {
        return Intent(context, MyUpdateService::class.java).apply {
            putExtra(SMALL_ICON_TAG, R.mipmap.ic_launcher)
            putExtra(DOWNLOAD_INTENT_TAG, EVENT_NORMAL)
            putExtra(MUST_BE_DOCUMENTED_TAG, versionBean.forceUpdate)
            putExtra(UPDATE_URL, versionBean.accessUrl)
            putExtra(SIMULATION_TAG, versionBean.isSimulation)
        }
    }

    override fun onDownloadEvent(msg: Message) {

        when (msg.what) {
            DOWNLOAD_START -> {
                dialog.dismiss()
                progressDialog.show()
                progressDialog.setProgress(0)
            }

            DOWNLOAD_SUCCESS -> {
                progressDialog.dismiss()
                val path = msg.obj as String

                AlertDialog.Builder(context)
                    .apply {
                        setTitle("提示")
                        setMessage("下载成功，是否立即安装?")
                        setCancelable(false)
                        setPositiveButton("安装") { _, _ ->
                            sendInstallSignal(path)
                        }
                        setNegativeButton("取消") { dialog, _ ->
                            dialog.dismiss()
                        }
                    }.show()
            }
            DOWNLOAD_FAIL -> {
                progressDialog.dismiss()

                AlertDialog.Builder(context)
                    .apply {
                        setTitle("提示")
                        setMessage("下载失败，是否重试?")
                        setCancelable(false)
                        setPositiveButton("重试") { _, _ ->
                            sendRetrySignal()
                        }
                        setNegativeButton("取消") { dialog, _ ->
                            dialog.dismiss()
                        }
                    }.show()

            }
            DOWNLOADING -> progressDialog.setProgress(msg.obj as Int)
        }


    }

    fun showHHintDialog() {
        dialog.show()
    }
}
