package com.iqonic.shophop.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.fragments.MyCartFragment
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.extensions.addFragment
import com.iqonic.shophop.utils.extensions.getCartList
import com.iqonic.shophop.utils.extensions.getCartTotal
import com.iqonic.shophop.utils.extensions.registerCartReceiver
import com.segmentify.segmentifyandroidsdk.SegmentifyManager
import com.segmentify.segmentifyandroidsdk.model.PageModel
import com.segmentify.segmentifyandroidsdk.model.RecommendationModel
import com.segmentify.segmentifyandroidsdk.utils.SegmentifyCallback
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


        //var data : HashMap<String, Any>
        // = HashMap<String, Any> ();
        // data.put("page_type", "basket");

        //DengageEvent.getInstance(applicationContext, intent).pageView(data);

        //DengageManager.getInstance(applicationContext).sendDeviceEvent("user_events", details)
        //DengageManager.getInstance(applicationContext).sendPageView(details);

        //val model = PageModel()
        //model.category = "Basket Page"

        //SegmentifyManager.sendPageView(
        //    model,
        //   object : SegmentifyCallback<ArrayList<RecommendationModel>> {
        //       override fun onDataLoaded(data: ArrayList<RecommendationModel>) {
        //           Log.d("Segmentify: ", data.toString())
        //       }
        //   })



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
