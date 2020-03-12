package com.iqonic.shophop.activity

import android.os.Bundle
import android.view.View
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.databinding.ItemOrderlistBinding
import com.iqonic.shophop.models.MyOrderData
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.Constants.KeyIntent.DATA
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.layout_nodata.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.math.roundToInt


class OrderActivity : AppBaseActivity() {

    private lateinit var mOrderAdapter: BaseRecyclerAdapter<MyOrderData, ItemOrderlistBinding>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        title = getString(R.string.title_my_orders)
        setToolbar(toolbar)

        disableHardwareRendering(rvOrder)
        mOrderAdapter = object : BaseRecyclerAdapter<MyOrderData, ItemOrderlistBinding>() {
            override fun onItemLongClick(view: View, model: MyOrderData, position: Int) {

            }

            override fun onItemClick(view: View, model: MyOrderData, position: Int, dataBinding: ItemOrderlistBinding) {
                if (view.id == R.id.rlMainOrder) {
                    launchActivity<OrderDescriptionActivity> {
                        putExtra(DATA, model)
                    }
                }
            }

            override val layoutResId: Int = R.layout.item_orderlist

            override fun onBindData(model: MyOrderData, position: Int, dataBinding: ItemOrderlistBinding) {
                dataBinding.tvOriginalPrice.applyStrike()

                if (model.line_items.size > 0) {
                    dataBinding.tvProductName.text = model.line_items[0].name
                    if (model.line_items[0].image.isNotEmpty()) {
                        dataBinding.ivProduct.loadImageFromUrl(model.line_items[0].image)
                    }
                } else {
                    dataBinding.tvProductName.text = getString(R.string.hint_no_products)
                }
                dataBinding.tvPrice.text = (model.total.toFloat() - model.discount_total.toFloat()).roundToInt().toString().currencyFormat(model.currency)
                if (model.discount_total == 0.0) {
                    dataBinding.tvOriginalPrice.hide()
                } else {
                    dataBinding.tvOriginalPrice.show()
                    dataBinding.tvOriginalPrice.text = model.total.toFloat().roundToInt().toString().currencyFormat(model.currency)
                }
                dataBinding.ivCircle.setCircleColor(color(R.color.track_yellow))

                when (model.status) {
                    Constants.OrderStatus.PENDING -> {
                        dataBinding.tvTrack1.text = (toDate(model.date_created) + "<br/>Order <font color=#ECC134>Pending</font>").getHtmlString()
                        dataBinding.tvTrack2.text = getString(R.string.lbl_order_pend).getHtmlString()
                    }
                    Constants.OrderStatus.PROCESSING -> {
                        dataBinding.tvTrack1.text = (toDate(model.date_created) + "<br/>Order <font color=#64B931>Processing</font>").getHtmlString()
                        dataBinding.tvTrack2.text = getString(R.string.lbl_item_delivering).getHtmlString()
                        dataBinding.ivCircle.setCircleColor(color(R.color.track_green))
                    }
                    Constants.OrderStatus.ONHOLD -> {
                        dataBinding.tvTrack1.text = (toDate(model.date_created) + "<br/>Order <font color=#ECC134>On Hold</font>").getHtmlString()
                        dataBinding.tvTrack2.text = getString(R.string.lbl_order_hold).getHtmlString()
                    }
                    Constants.OrderStatus.COMPLETED -> {
                        dataBinding.tvTrack1.text = (toDate(model.date_created) + "<br/>Order <font color=#64B931>Placed</font>").getHtmlString()
                        dataBinding.tvTrack2.text = getString(R.string.lbl_order_completed).getHtmlString()
                        dataBinding.tvProductDeliveryDate.text = toDate(model.date_completed!!)
                        dataBinding.ivCircle.setCircleColor(color(R.color.track_green))
                        dataBinding.ivLine.setLineColor(color(R.color.track_green))
                        dataBinding.ivCircle1.setCircleColor(color(R.color.track_green))
                    }
                    Constants.OrderStatus.CANCELLED -> {
                        dataBinding.ivCircle.setCircleColor(color(R.color.track_red))
                        dataBinding.tvTrack1.text = (toDate(model.date_created) + "<br/>Order <font color=#F61929>Cancelled</font>").getHtmlString()
                        dataBinding.tvTrack2.text = getString(R.string.lbl_order_cacelled).getHtmlString()
                    }
                    Constants.OrderStatus.REFUNDED -> {
                        dataBinding.ivCircle.setCircleColor(color(R.color.track_grey))
                        dataBinding.tvTrack1.text = (toDate(model.date_created) + "<br/>Order <font color=#D3D3D3>Refunded</font>").getHtmlString()
                        dataBinding.tvTrack2.text = getString(R.string.lbl_refunded).getHtmlString()
                    }
                    else -> {
                        dataBinding.ivCircle.setCircleColor(color(R.color.track_red))
                        dataBinding.tvTrack1.text = (toDate(model.date_created) + "<br/>Order <font color=#F61929>Trashed</font>").getHtmlString()
                        dataBinding.tvTrack2.text = "Order Trashed"
                    }
                }

                if (model.status == Constants.OrderStatus.COMPLETED) {
                    dataBinding.llDeliveryDate.show()
                    dataBinding.llDeliveryInfo.show()
                    dataBinding.rlStatus.hide()
                    dataBinding.tvProductDeliveryDate.text = toDate(model.date_completed!!)
                } else {
                    dataBinding.llDeliveryDate.hide()
                    dataBinding.llDeliveryInfo.hide()
                    dataBinding.rlStatus.show()
                }
            }
        }
        rvOrder.adapter = mOrderAdapter

        val list = getOrders()
        if (list.size == 0) {
            rlNoData.show()
        } else {
            rlNoData.hide()
            mOrderAdapter.addItems(list)
        }
        loadBannerAd(R.id.adView)

    }

}
