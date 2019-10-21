package com.mitsuki.updatecomp

import android.content.Context
import android.content.Intent
import android.os.Message
import com.mitsuki.update.*

class UnionDialogController(private val context: Context, private val versionBean: AppVersionBean) :
    DialogController() {

    private val dialog: ProgressUpdateDialog =
        ProgressUpdateDialog(context, versionBean)

    init {
        dialog.retry = { sendRetrySignal() }
        dialog.confirm = { sendDownloadSignal() }
        dialog.install = { sendInstallSignal(it) }
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
        dialog.onDownloadEvent(msg)
    }

    fun showDialog() {
        dialog.show()
    }
}
