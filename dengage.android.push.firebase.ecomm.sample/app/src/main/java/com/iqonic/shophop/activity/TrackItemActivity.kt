package com.iqonic.shophop.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.databinding.ItemOrderBinding
import com.iqonic.shophop.databinding.ItemTrackBinding
import com.iqonic.shophop.models.*
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.activity_track_item.*
import kotlinx.android.synthetic.main.activity_track_item.rvOrderItems
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.math.roundToInt


class TrackItemActivity : AppBaseActivity() {
    private lateinit var mOrderItemAdapter: BaseRecyclerAdapter<LineItem, ItemOrderBinding>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_item)
        setToolbar(toolbar)
        title = getString(R.string.lbl_track_item)
        disableHardwareRendering(rcvTracks)
        val orderModel = intent.getSerializableExtra(Constants.KeyIntent.DATA) as MyOrderData
        mOrderItemAdapter = object : BaseRecyclerAdapter<LineItem, ItemOrderBinding>() {
            override val layoutResId: Int get() = R.layout.item_order

            @SuppressLint("SetTextI18n")
            override fun onBindData(model: LineItem, position: Int, dataBinding: ItemOrderBinding) {
                dataBinding.tvPrice.text = model.total.roundToInt().toString().currencyFormat(orderModel.currency)
                dataBinding.tvOriginalPrice.text = model.price.toString().currencyFormat(orderModel.currency)
                dataBinding.tvTotalItem.text = getString(R.string.text_total_item_1) + model.quantity
                dataBinding.tvOriginalPrice.applyStrike()
                if (model.color.isNotEmpty()) {
                    dataBinding.ivChecked.changeBackgroundTint(android.graphics.Color.parseColor(model.color))
                }
                if (model.image.isNotEmpty()) {
                    dataBinding.ivProduct.loadImageFromUrl(model.image)
                }
            }

            override fun onItemClick(view: View, model: LineItem, position: Int, dataBinding: ItemOrderBinding) {}
            override fun onItemLongClick(view: View, model: LineItem, position: Int) {}

        }

        rvOrderItems.setVerticalLayout()
        rvOrderItems.adapter = mOrderItemAdapter
        rcvTracks.setVerticalLayout()
        rcvTracks.adapter = mTracksAdapter
        mOrderItemAdapter.addItems(orderModel.line_items)
        getOrderTrackings()
        btnCancelOrder.hide()
        loadBannerAd(R.id.adView)

    }

    private fun getOrderTrackings() {
        addData()
    }

    var mTracksAdapter = object : BaseRecyclerAdapter<Track, ItemTrackBinding>() {
        override val layoutResId: Int = R.layout.item_track

        override fun onBindData(model: Track, position: Int, dataBinding: ItemTrackBinding) {
            if (model.status == TrackStatus.NOTDONE) {
                dataBinding.tvText1.text = ""
                dataBinding.tvDate.text = ""
            } else {
                if (model.status == TrackStatus.OUTFORDELIVERY && !(model.isDone!!)) {
                    dataBinding.tvText1.text = ""
                    dataBinding.tvDate.text = ""
                } else {
                    dataBinding.tvText1.text = model.trackStatus!!.getHtmlString()
                    dataBinding.tvDate.text = model.date
                }
            }

            val color = color(getTrackColor(model.status))
            dataBinding.ivCircle.setCircleColor(color)

            if (model.isActive!!) {
                dataBinding.ivCircle.setRadius(19f)
            } else {
                dataBinding.ivCircle.setRadius(14f)
            }
            if (model.isDone!!) {
                dataBinding.ivLine.setLineColor(color)
            } else {
                dataBinding.ivLine.setLineColor(color(R.color.track_grey))
            }
            if (model.status == TrackStatus.OUTFORDELIVERY) {
                dataBinding.ivCircle.hide()
                dataBinding.tvDate.hide()
                dataBinding.tvText1.setTextColor(color(R.color.textColorSecondary))
            } else {
                dataBinding.ivCircle.show()
                dataBinding.tvDate.show()
                dataBinding.tvText1.setTextColor(color(R.color.textColorPrimary))


            }
            if (model.status == TrackStatus.DELIVERED || model.status == TrackStatus.DELIVERING) {
                dataBinding.tvText1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_right_black, 0)
                dataBinding.ivLine.hide()
            } else {
                dataBinding.tvText1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                dataBinding.ivLine.show()
            }

        }

        override fun onItemClick(view: View, model: Track, position: Int, dataBinding: ItemTrackBinding) {
        }

        override fun onItemLongClick(view: View, model: Track, position: Int) {

        }
    }

    private fun getTrackColor(status: TrackStatus?): Int {
        when (status) {
            TrackStatus.NOTDONE -> {
                return R.color.track_grey
            }
            TrackStatus.SHIPPED -> {
                return R.color.track_green
            }
            TrackStatus.ARRIVED -> {
                return R.color.track_green
            }
            TrackStatus.OUTFORDELIVERY -> {
                return R.color.track_green
            }
            TrackStatus.DELIVERED -> {
                return R.color.track_green
            }
            TrackStatus.PENDING -> {
                return R.color.track_yellow
            }
            else -> return R.color.track_grey
        }
    }

    private fun addData() {
        val tracks = ArrayList<Track>()
        val track = Track()
        track.status = TrackStatus.SHIPPED
        track.trackStatus = "Order <font color=#64B931>Shipped</font>"
        track.date = "Mon,17 July 2019"

        val track2 = Track()
        track2.status = TrackStatus.ARRIVED
        track2.trackStatus = "Item <font color=#64B931>Arrived</font> at Mumbai"
        track2.date = "Mon,17 July 2019"

        val track3 = Track()
        track3.status = TrackStatus.ARRIVED
        track3.trackStatus = "Item <font color=#64B931>Arrived</font> at Ahmedabad"
        track3.date = "Mon,17 July 2019"

        val track4 = Track()
        track4.status = TrackStatus.OUTFORDELIVERY
        track4.trackStatus = "Your order is out for delivery"
        track4.date = "Mon,17 July 2019"

        val track5 = Track()
        track5.status = TrackStatus.ARRIVED
        track5.trackStatus = "Item <font color=#64B931>Arrived</font> at Anand"
        track5.date = "Mon,17 July 2019"

        val track6 = Track()
        track6.status = TrackStatus.DELIVERED
        // track6.trackStatus = "Item Delivering"
        track6.trackStatus = "Item <font color=#64B931>Delivered</font>"
        track6.date = "Mon,17 July 2019"

        tracks.add(track)
        tracks.add(track2)
        tracks.add(track3)
        tracks.add(track4)
        tracks.add(track5)
        tracks.add(track6)
        mTracksAdapter.addItems(tracks)
    }
}
