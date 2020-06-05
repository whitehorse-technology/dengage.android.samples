package com.iqonic.shophop.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.utils.extensions.launchActivity
import com.iqonic.shophop.utils.extensions.runDelayed

class SplashActivity : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_new)
            val w = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        runDelayed(500) {
            launchActivity<WalkThroughActivity>(); onBackPressed()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    override fun onResume(){
        super.onResume()
        val bundle : Bundle? = intent.extras

    }
}