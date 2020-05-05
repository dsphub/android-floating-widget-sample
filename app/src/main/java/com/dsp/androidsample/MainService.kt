package com.dsp.androidsample

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.RelativeLayout
import com.dsp.androidsample.Logger.d

class MainService : Service() {
    private lateinit var windowManager: WindowManager
    private var floatingWidget: View? = null
    private val position = Pair(100, 100)

    override fun onBind(intent: Intent?): IBinder? {
        d { "onBind" }
        return null
    }

    override fun onCreate() {
        d { "onCreate" }
        super.onCreate()
        initWidget()

        val closeButton = floatingWidget?.findViewById<ImageView>(R.id.imageView_close)
        closeButton?.setOnClickListener {
            d { "closeButton" }
            stopSelf()
        }
    }

    private fun initWidget() {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = position.first
        params.y = position.second
        floatingWidget = LayoutInflater.from(this).inflate(R.layout.floating_widget, null)
        floatingWidget?.findViewById<RelativeLayout>(R.id.relativeLayout_container)
            ?.setOnTouchListener(object : OnTouchListener {
                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f
                override fun onTouch(v: View?, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params.x
                            initialY = params.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            val xDiff = (event.rawX - initialTouchX).toInt()
                            val yDiff = (event.rawY - initialTouchY).toInt()
                            if (xDiff < 10 && yDiff < 10) {
                                d { "onTouch up" }
                                v?.performClick()
                                Intent(this@MainService, MainActivity::class.java)
                                    .also {
                                        startActivity(it)
                                    }
                            }
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            params.x = initialX + (event.rawX - initialTouchX).toInt()
                            params.y = initialY + (event.rawY - initialTouchY).toInt()
                            windowManager.updateViewLayout(floatingWidget, params)
                            return true
                        }
                    }
                    return false
                }
            })
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingWidget, params)
    }

    override fun onDestroy() {
        d { "onDestroy " }
        super.onDestroy()
        if (floatingWidget != null) windowManager.removeView(floatingWidget)
    }
}
