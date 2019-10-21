package com.mitsuki.updatecomp

import android.content.Intent
import android.util.Log
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.mitsuki.update.DownloadService
import java.io.File

class MyUpdateService : DownloadService() {
    override fun startDownload(intent: Intent) {
        val tag = intent.getBooleanExtra(SIMULATION_TAG, true)
        if (tag) {
            Thread {
                downloadStart()
                for (i in 0..10) {
                    downloading(i * 10)
                    Thread.sleep(1000)
                }
                downloadFail()
            }
                .start()
        } else {
            val file = intent.getStringExtra(UPDATE_URL)
            OkGo.get<File>(file)
                .tag(this)
                .execute(object : FileCallback() {
                    override fun onStart(request: Request<File, out Request<*, *>>?) {
                        super.onStart(request)
                        downloadStart()
                    }

                    override fun onSuccess(response: Response<File>) {
                        downloadSuccess(response.body().absolutePath)
                    }

                    override fun onError(response: Response<File>?) {
                        super.onError(response)
                        downloadFail()
                    }

                    override fun downloadProgress(progress: Progress) {
                        downloading((progress.fraction * 100).toInt())
                    }
                })
        }

    }
}