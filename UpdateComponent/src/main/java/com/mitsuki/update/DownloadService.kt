package com.mitsuki.update

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import java.lang.RuntimeException
import java.lang.ref.WeakReference

abstract class DownloadService : Service() {


    private var downloadTag = false
    //    private var updateTag = false
    private lateinit var handlerWeakReference: WeakReference<Handler>

    abstract fun startDownload(intent: Intent)

    override fun onCreate() {
        super.onCreate()
        Log.e("Update", "Service : onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("Update", "Service : onDestroy")
    }

    @SuppressLint("NewApi")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e("Update", "Service : onStartCommand")
        if (downloadTag) return super.onStartCommand(intent, flags, startId)

        when (intent.getIntExtra(DOWNLOAD_INTENT_TAG, -1)) {
            EVENT_INSTALL_APK -> {
                //安装APK
                Log.e("Update", "Service : install apk")
                handler()?.sendMessage(Message.obtain().apply {
                    what = DOWNLOAD_INSTALL_APK
                    obj = intent.getStringExtra(APK_LOCAL_PATH)
                })
            }
            EVENT_RETRY, EVENT_NORMAL -> {
                startDownload(intent)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun bindHandler(handler: Handler) {
        this.handlerWeakReference = WeakReference(handler)
    }

    protected fun downloadStart() {
        Log.e("Update", "Service : download start")
        downloadTag = true
        handler()?.sendMessage(Message.obtain().apply { what = DOWNLOAD_START })
    }

    protected fun downloadSuccess(path: String) {
        Log.e("Update", "Service : download success")
        downloadTag = false
        handler()?.sendMessage(Message.obtain().apply {
            what = DOWNLOAD_SUCCESS
            obj = path
        })
    }

    protected fun downloadFail() {
        Log.e("Update", "Service : download fail")
        downloadTag = false
        handler()?.sendMessage(Message.obtain().apply { what = DOWNLOAD_FAIL })
    }

    protected fun downloading(progress: Int) {
        Log.e("Update", "Service : downloading")
        handler()?.sendMessage(Message.obtain().apply {
            what = DOWNLOADING
            obj = progress
        })
    }

    private fun handler(): Handler? = handlerWeakReference.get()

    override fun onBind(intent: Intent?): IBinder? = ServiceBinder()

    inner class ServiceBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): DownloadService = this@DownloadService
    }

}