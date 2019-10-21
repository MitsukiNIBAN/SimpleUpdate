package com.mitsuki.updatecomp

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mitsuki.update.CurrentActivityProvider
import com.mitsuki.update.UpdateController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var versionBean = AppVersionBean()
    private lateinit var unionDialogController: UnionDialogController
    private lateinit var splitDialogController: SplitDialogController

    private var tag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        unionDialogController = UnionDialogController(this, versionBean)
        splitDialogController = SplitDialogController(this, versionBean)

        UpdateController.initProvider(object : CurrentActivityProvider {
            override fun currentActivity(): Activity {
                return this@MainActivity
            }
        })
        UpdateController.bindDialogController(unionDialogController)

        UpdateController.bindUpdateService(MyUpdateService::class.java)


        updateBtn.setOnClickListener {
            if (tag)
                unionDialogController.showDialog()
            else
                splitDialogController.showHHintDialog()
        }

        mustSwitch.setOnCheckedChangeListener { _, isChecked ->
            versionBean.forceUpdate = isChecked
            tomoSwitch.isEnabled = isChecked
        }

        sumSwitch.setOnCheckedChangeListener { _, isChecked ->
            versionBean.isSimulation = isChecked
        }

        tomoSwitch.setOnCheckedChangeListener { _, isChecked ->
            tag = isChecked
            if (isChecked) {
                UpdateController.bindDialogController(unionDialogController)
            } else {
                UpdateController.bindDialogController(splitDialogController)
            }
        }
    }
}
