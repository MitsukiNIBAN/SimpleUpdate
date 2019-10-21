package com.mitsuki.update

import android.content.Intent
import android.os.Handler
import android.os.Message
import android.widget.ProgressBar
import java.lang.ref.WeakReference

abstract class DialogController {

    private lateinit var handlerWeakReference: WeakReference<Handler>

    abstract fun mustBeUpdated(): Boolean

    abstract fun downloadIntent(): Intent

    abstract fun onDownloadEvent(msg: Message)

    private fun handler(): Handler? = handlerWeakReference.get()

    fun bindHandler(handler: Handler) {
        this.handlerWeakReference = WeakReference(handler)
    }

    fun sendDownloadSignal() {
        handler()?.sendMessage(Message.obtain().apply {
            what = DIALOG_START_SERVICE_TAG
            obj = downloadIntent()
        })
    }

    fun sendInstallSignal(path: String) {
        handler()?.sendMessage(Message.obtain().apply {
            what = DIALOG_INSTALL_APK
            obj = path
        })
    }

    fun sendRetrySignal() {
        handler()?.sendMessage(Message.obtain().apply { what = DIALOG_RETRY })
    }
}