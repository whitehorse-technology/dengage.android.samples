package com.iqonic.shophop

import android.app.Dialog
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.iqonic.shophop.utils.SharedPrefUtils
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import com.dengage.sdk.notification.dEngageMobileManager
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T

class dEngageClass {
    companion object {
        // This is your Integration Key which you got on dEngage Push Application settings page.

        fun getMoreInfo():String { return "This is more fun" }
    }
}

class ShopHopApp : MultiDexApplication() {

    private val integrationKey = "vR8gUvtpaHfUoBQ9kYdMDYuclZmYnUEEiDpYfg0_p_l_Z5R_s_l_1H_s_l_7XB3cUzLbVQtI58igmaaz_s_l_QbuL4ssw5jS36FBPyyg5PxspAGDVjiObBsN12inghqMSDTFIADrV2CSQ5mv"
    private var mobileManager = dEngageMobileManager.getInstance()

    override fun onCreate() {
        super.onCreate()
        appInstance = this

        // Set Custom Font
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/Montserrat-Regular.ttf").setFontAttrId(R.attr.fontPath).build())

        val context = applicationContext
        if(mobileManager == null) {
            mobileManager = dEngageMobileManager.createInstance(integrationKey, context)
            mobileManager.register()
        }
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
