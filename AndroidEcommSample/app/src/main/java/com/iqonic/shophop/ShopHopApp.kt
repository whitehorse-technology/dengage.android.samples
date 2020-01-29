package com.iqonic.shophop

import android.app.Dialog
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.iqonic.shophop.utils.SharedPrefUtils
import uk.co.chrisjenx.calligraphy.CalligraphyConfig


class ShopHopApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this

        // Set Custom Font
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/Montserrat-Regular.ttf").setFontAttrId(R.attr.fontPath).build())

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        private lateinit var appInstance: ShopHopApp
        var sharedPrefUtils: SharedPrefUtils? = null
        var noInternetDialog : Dialog?= null

        fun getAppInstance(): ShopHopApp {
            return appInstance
        }
    }
}
