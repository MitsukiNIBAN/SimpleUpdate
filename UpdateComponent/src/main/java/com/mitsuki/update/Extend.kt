package com.mitsuki.update

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import java.io.File

const val DOWNLOAD_INTENT_TAG = "DOWNLOAD_INTENT_TAG"
const val SMALL_ICON_TAG = "SMALL_ICON_TAG"
const val MUST_BE_DOCUMENTED_TAG = "MUST_BE_DOCUMENTED_TAG"
const val APK_LOCAL_PATH = "APK_LOCAL_PATH"

const val DOWNLOAD_START = 0
const val DOWNLOADING = 1
const val DOWNLOAD_SUCCESS = 2
const val DOWNLOAD_FAIL = 3
const val DOWNLOAD_INSTALL_APK = 4
const val DIALOG_START_SERVICE_TAG = 5
const val DIALOG_INSTALL_APK = 6
const val DIALOG_RETRY = 7
const val EVENT_INSTALL_APK = 8
const val EVENT_RETRY = 9
const val EVENT_NORMAL = 10

fun installApk(context: Context, path: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val hasInstallPermission = context.packageManager.canRequestPackageInstalls()
        if (!hasInstallPermission) {
            //请求安装未知应用来源的权限
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.REQUEST_INSTALL_PACKAGES),
                1100
            )
        }
    }

    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    val file = File(path)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val contentUri =
            FileProvider.getUriForFile(context, "com.mitsuki.update.fileProvider", file)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
    } else {
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
    }

    context.startActivity(intent)
}

@SuppressLint("NewApi")
fun Context.createNotificationChannel(channelId: String, channelName: String): String? =
    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O).run {
        if (this) {
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .apply {
                    createNotificationChannel(
                        NotificationChannel(
                            channelId,
                            channelName,
                            NotificationManager.IMPORTANCE_DEFAULT
                        ).apply {
                            enableVibration(false)
                            enableLights(false)
                            setSound(null, null)
                        }
                    )
                }
            channelId
        } else null
    }

interface CurrentActivityProvider {
    fun currentActivity(): Activity
}