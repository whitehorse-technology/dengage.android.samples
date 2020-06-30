package com.iqonic.shophop

import android.app.Dialog
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.iqonic.shophop.utils.SharedPrefUtils
import dengage.android.push.firebase.DengageManager
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class dEngageClass {
    companion object {
        // This is your Integration Key which you got on dEngage Push Application settings page.

        fun getMoreInfo():String { return "This is more fun" }
    }
}

class ShopHopApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this

        // Set Custom Font
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/Montserrat-Regular.ttf").setFontAttrId(R.attr.fontPath).build())

        val context = applicationContext
        val manager = DengageManager
                .getInstance(context)
                .setLogStatus(true)
                .setIntegrationKey(Constants.INTEGRATION_KEY)
                .init()

        manager.syncSubscription();
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
