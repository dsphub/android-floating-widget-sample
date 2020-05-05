package com.dsp.androidsample

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dsp.androidsample.Logger.d
import com.dsp.androidsample.Logger.i
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        i { "onCreate" }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestOverlayPermission {
            initView()
        }
    }

    private fun requestOverlayPermission(block: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            ).also {
                startActivityForResult(it, APP_PERMISSION_REQUEST_CODE)
            }
        } else {
            block()
        }
    }

    private fun initView() {
        d { "initializeView" }
        button_start.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        d { "onActivityResult" }
        super.onActivityResult(requestCode, resultCode, data)
        onOverlayPermissionSuccess(requestCode, resultCode) {
            initView()
        }
    }

    private fun onOverlayPermissionSuccess(requestCode: Int, resultCode: Int, block: () -> Unit) {
        if (requestCode == APP_PERMISSION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            block()
        } else {
            Toast.makeText(
                this,
                "Overlay permission is not enabled",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val APP_PERMISSION_REQUEST_CODE: Int = 1
    }
}
