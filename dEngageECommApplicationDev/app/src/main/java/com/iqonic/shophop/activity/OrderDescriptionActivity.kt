package com.iqonic.shophop.activity

import android.os.Bundle
import android.view.View
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.databinding.ItemOrderBinding
import com.iqonic.shophop.models.LineItem
import com.iqonic.shophop.models.MyOrderData
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.Constants.OrderStatus.CANCELLED
import com.iqonic.shophop.utils.Constants.OrderStatus.COMPLETED
import com.iqonic.shophop.utils.Constants.OrderStatus.ONHOLD
import com.iqonic.shophop.utils.Constants.OrderStatus.PENDING
import com.iqonic.shophop.utils.Constants.OrderStatus.PROCESSING
import com.iqonic.shophop.utils.Constants.OrderStatus.REFUNDED
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.activity_orderdescription.*
import kotlinx.android.synthetic.main.layout_paymentdetail.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.math.roundToInt

class OrderDescriptionActivity : AppBaseActivity() {

    private lateinit var mOrderItemAdapter: BaseRecyclerAdapter<LineItem, ItemOrderBinding>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orderdescription)
        title = getString(R.string.title_my_orders)
        setToolbar(toolbar)
        disableHardwareRendering(llTrack)

        val orderModel = intent.getSerializableExtra(Constants.KeyIntent.DATA) as MyOrderData
        mOrderItemAdapter = object : BaseRecyclerAdapter<LineItem, ItemOrderBinding>() {
            override val layoutResId: Int = R.layout.item_order

            override fun onBindData(model: LineItem, position: Int, dataBinding: ItemOrderBinding) {
                dataBinding.tvPrice.text =
                    model.total.roundToInt().toString().currencyFormat(orderModel.currency)
                dataBinding.tvOriginalPrice.text =
                    model.price.toString().currencyFormat(orderModel.currency)
                dataBinding.tvTotalItem.text =String.format("%s %d",getString(R.string.text_total_item_1),model.quantity)
                dataBinding.tvOriginalPrice.applyStrike()
                if (model.color.isNotEmpty()) {
                    dataBinding.ivChecked.changeBackgroundTint(
                        android.graphics.Color.parseColor(
                            model.color
                        )
                    )
                }
                if (model.image.isNotEmpty()) {
                    dataBinding.ivProduct.loadImageFromUrl(model.image)
                }
            }

            override fun onItemClick(
                view: View,
                model: LineItem,
                position: Int,
                dataBinding: ItemOrderBinding
            ) {
            }

            override fun onItemLongClick(view: View, model: LineItem, position: Int) {}

        }
        rvOrderItems.setVerticalLayout()
        rvOrderItems.adapter = mOrderItemAdapter
        bindOrderData(orderModel)

        llTrack.onClick {
            launchActivity<TrackItemActivity> {
                putExtra(Constants.KeyIntent.DATA, orderModel)
            }
        }
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            txtRatings.text = rating.toString()
        }
        loadBannerAd(R.id.adView)

    }

    private fun bindOrderData(orderModel: MyOrderData) {
        mOrderItemAdapter.addItems(orderModel.line_items)
        val track1: String
        val track2: String
        ivCircle.setCircleColor(color(R.color.track_yellow))

        when (orderModel.status) {
            PENDING -> {
                track1 = "Order <font color=#ECC134>Pending</font>"
                track2 = "Order Pending"
            }
            PROCESSING -> {
                track1 = "Order <font color=#64B931>Processing</font>"
                track2 = "Item Delivering "
                ivCircle.setCircleColor(color(R.color.track_green))
            }
            ONHOLD -> {
                track1 = "Order <font color=#ECC134>On Hold</font>"
                track2 = "Order on hold"
            }
            COMPLETED -> {
                track1 = "Order <font color=#64B931>Placed</font>"
                track2 = "Order <font color=#64B931>Completed</font>"
                tvDeliveryDate.text = toDate(orderModel.date_completed!!)
                tvTrack2.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_keyboard_arrow_right_black,
                    0
                )
                tvDeliveryDate.show()
                tvDelivered.show()
                ivCircle.setCircleColor(color(R.color.track_green))
                ivLine.setLineColor(color(R.color.track_green))
                ivCircle1.setCircleColor(color(R.color.track_green))
            }
            CANCELLED -> {
                ivCircle.setCircleColor(color(R.color.track_red))
                track1 = "Order <font color=#F61929>Cancelled</font>"
                track2 = "Order Cancelled"
            }
            REFUNDED -> {
                ivCircle.setCircleColor(color(R.color.track_grey))
                track1 = "Order <font color=#D3D3D3>Refunded</font>"
                track2 = "Order Refunded"
            }
            else -> {
                ivCircle.setCircleColor(color(R.color.track_red))
                track1 = "Order <font color=#F61929>Trashed</font>"
                track2 = "Order Trashed"
            }
        }

        tvDate.text = toDate(orderModel.date_created)
        tvTrack1.text = track1.getHtmlString()
        tvTrack2.text = track2.getHtmlString()
        //tvOrderId.text = orderModel.order_key.split("_")[2].toUpperCase()
        tvOrderDate.text = toDate(orderModel.date_created)
        if (orderModel.shipping_total == 0.0) {
            tvShippingCharge.text = getString(R.string.lbl_free)
        } else {
            tvShippingCharge.text = orderModel.shipping_total.roundToInt().toString()
                .currencyFormat(orderModel.currency)
        }
        tvTotalAmount.text =
            ((orderModel.total - orderModel.discount_total) + orderModel.shipping_total).toString()
                .currencyFormat(orderModel.currency)
    }
}
