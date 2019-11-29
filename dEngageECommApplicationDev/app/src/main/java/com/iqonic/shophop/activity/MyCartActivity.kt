package com.iqonic.shophop.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.fragments.MyCartFragment
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.extensions.addFragment
import com.iqonic.shophop.utils.extensions.registerCartReceiver
import kotlinx.android.synthetic.main.toolbar.*

class MyCartActivity : AppBaseActivity() {
    private val mCartItemChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Constants.AppBroadcasts.CARTITEM_UPDATE -> {
                    myCartFragment.setCart()
                }
            }
        }
    }
    private var myCartFragment:MyCartFragment=MyCartFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)
        setToolbar(toolbar)
        title = getString(R.string.menu_my_cart)
        registerCartReceiver(mCartItemChangedReceiver)
        addFragment(myCartFragment, R.id.container)

    }
    override fun onDestroy() {
        unregisterReceiver(mCartItemChangedReceiver)
        super.onDestroy()
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }

}
