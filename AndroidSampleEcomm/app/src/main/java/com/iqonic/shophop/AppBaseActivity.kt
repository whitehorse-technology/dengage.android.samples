package com.iqonic.shophop

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.iqonic.shophop.ShopHopApp.Companion.noInternetDialog
import com.iqonic.shophop.utils.extensions.changeToolbarFont
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.*

open class AppBaseActivity : AppCompatActivity() {
    private var progressDialog: Dialog? = null

    fun disableHardwareRendering(v: View) {
        v.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    fun setToolbar(mToolbar: Toolbar) {
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mToolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace)
        mToolbar.changeToolbarFont()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarGradient(this)
        noInternetDialog = null

        if (progressDialog == null) {
            progressDialog = Dialog(this)
            progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
            progressDialog!!.setContentView(R.layout.custom_dialog)
        }

    }

    private fun setStatusBarGradient(activity: Activity) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                val window = activity.window
                //val background = activity.resources.getDrawable(R.drawable.bg_toolbar_gradient)
                var flags = activity.window.decorView.systemUiVisibility // get current flag
                flags =
                    flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR   // add LIGHT_STATUS_BAR to flag
                activity.window.decorView.systemUiVisibility = flags
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this,R.color.background_color)
                //window.setBackgroundDrawable(background)
            }
            else -> {
                window.statusBarColor =  ContextCompat.getColor(this,R.color.colorPrimaryDark)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun showProgress(show: Boolean) {
        when {
            show -> {
                if (!isFinishing) {
                    /*  progressDialog!!.setTitle(getString(R.string.msg_loading))
                      progressDialog!!.setMessage(getString(R.string.msg_please_wait))*/
                    progressDialog!!.setCanceledOnTouchOutside(false)
                    progressDialog!!.show()
                }
            }
            else -> try {
                if (progressDialog!!.isShowing && !isFinishing) {
                    progressDialog!!.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadBannerAd(id: Int) {
        val adContainer = findViewById<View>(id)
        if (adContainer != null) {

        }

    }

    fun loadInterstialAd(onFinished:(Int)->Unit) {


    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onStart() {
        Log.d("onStart", "called")
        super.onStart()
    }

    override fun onResume() {
        Log.d("onResume", "called")
        super.onResume()
    }

    override fun onPause() {
        Log.d("onPaused", "called")
        super.onPause()
    }

    override fun onDestroy() {
        Log.d("onDestroy", "called")
        super.onDestroy()
    }
}
