package com.mitsuki.update

import android.app.Application
import android.app.Dialog
import android.app.PendingIntent
import android.content.*
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.LocalBroadcastManager
import java.lang.RuntimeException
import java.lang.ref.WeakReference

object UpdateController : Handler() {

    private val ID = 54

    private lateinit var mService: DownloadService
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var mDialogController: WeakReference<DialogController>
//    private lateinit var mDialog: WeakReference<UpdateHintDialog>

    private lateinit var provider: CurrentActivityProvider

    private lateinit var context: Application

    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {
        @Suppress("UNCHECKED_CAST")
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as DownloadService.ServiceBinder
            mService = binder.getService()
            mService.bindHandler(this@UpdateController)
            initBuilder(mService)

            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    /**********************************************************************************************/
    override fun handleMessage(msg: Message) {
        //来自service事件，转发至dialog
        when (msg.what) {
            DOWNLOAD_START, DOWNLOADING, DOWNLOAD_FAIL, DOWNLOAD_SUCCESS -> {
                if (dialogController()?.mustBeUpdated() == true)
                    dialogController()?.onDownloadEvent(msg)
                else
                    notifyDownloadState(msg)
            }
            DOWNLOAD_INSTALL_APK -> installApk(provider.currentActivity(), msg.obj as String)
        }

        //来自dialog事件，转发service
        if (mBound) {
            when (msg.what) {
                DIALOG_START_SERVICE_TAG -> (msg.obj as Intent).apply {
                    if (dialogController()?.mustBeUpdated() == false)
                        getIntExtra(SMALL_ICON_TAG, 0).apply {
                            if (this == 0) throw RuntimeException("no valid small icon")
                            builder.setSmallIcon(this)
                        }
                    provider.currentActivity().startService(this)
                }
                DIALOG_INSTALL_APK -> installApk(provider.currentActivity(), msg.obj as String)
                DIALOG_RETRY -> Intent(provider.currentActivity(), mService.javaClass).apply {
                    putExtra(DOWNLOAD_INTENT_TAG, EVENT_RETRY)
                    provider.currentActivity().startService(this)
                }
            }
        }
    }

    /**********************************************************************************************/
    fun init(application: Application) {
        context = application
    }

    fun initProvider(p: CurrentActivityProvider) {
        provider = p
    }

    fun bindUpdateService(cls: Class<*>) {
        Intent(provider.currentActivity(), cls).apply {
            context.bindService(this, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unBindUpdateService() {
        context.unbindService(connection)
        mBound = false
    }


    fun bindDialogController(controller: DialogController) {
        mDialogController = WeakReference(controller)
        dialogController()?.bindHandler(this)
    }

    /**********************************************************************************************/
    private fun dialogController(): DialogController? = mDialogController.get()

    private fun initBuilder(context: Context) {
        builder = (context.createNotificationChannel("10086", "Update download service")
            ?.let { NotificationCompat.Builder(context, it) }
            ?: NotificationCompat.Builder(context)).apply {
            priority = NotificationCompat.PRIORITY_DEFAULT

            setShowWhen(false)
            setOnlyAlertOnce(true)
        }
    }

    private fun notifyDownloadState(msg: Message) {
        when (msg.what) {
            DOWNLOAD_START -> {
                builder.setOngoing(true)
                builder.setAutoCancel(false)
                builder.setContentIntent(null)
                builder.setContentText("0%")
                builder.setContentTitle("正在下载新版本")
                builder.setProgress(100, 0, false)
            }
            DOWNLOADING -> {
                builder.setProgress(100, msg.obj as Int, false)
                builder.setContentText("${msg.obj}%")
            }
            DOWNLOAD_FAIL -> {
                builder.setContentTitle("新版本下载失败")
                builder.setContentText("点击重试")
                builder.setContentIntent(
                    PendingIntent.getService(
                        provider.currentActivity(),
                        0,
                        Intent(provider.currentActivity(), mService.javaClass)
                            .apply {
                                putExtra(DOWNLOAD_INTENT_TAG, EVENT_RETRY)
                            },
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                builder.setAutoCancel(true)
                builder.setOngoing(false)
            }
            DOWNLOAD_SUCCESS -> {
                builder.setContentTitle("新版本下载完成")
                builder.setContentText("点击安装")
                builder.setContentIntent(
                    PendingIntent.getService(
                        provider.currentActivity(),
                        0,
                        Intent(provider.currentActivity(), mService.javaClass)
                            .apply {
                                putExtra(DOWNLOAD_INTENT_TAG, EVENT_INSTALL_APK)
                                putExtra(APK_LOCAL_PATH, msg.obj as String)
                            },
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                builder.setAutoCancel(true)
                builder.setOngoing(false)

            }
        }
        with(NotificationManagerCompat.from(mService)) { notify(ID, builder.build()) }
    }
}
