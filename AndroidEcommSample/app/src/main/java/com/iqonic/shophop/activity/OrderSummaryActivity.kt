package com.iqonic.shophop.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.RelativeLayout
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.databinding.ItemCartBinding
import com.iqonic.shophop.databinding.ItemUserAddressBinding
import com.iqonic.shophop.models.*
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.activity_order_summary.*
import kotlinx.android.synthetic.main.dialog_change_address.*
import kotlinx.android.synthetic.main.item_address.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class OrderSummaryActivity : AppBaseActivity() {

    private lateinit var dialog: Dialog
    private var mAddressAdapter = object : BaseRecyclerAdapter<Address, ItemUserAddressBinding>() {
        override val layoutResId: Int
            get() = R.layout.item_user_address

        override fun onBindData(model: Address, position: Int, dataBinding: ItemUserAddressBinding) {

            dataBinding.rbDefaultAddress.isChecked = model.isDefault!!
            if (model.isDefault!!) {
                dataBinding.included.tvEdit.show()
            } else {
                dataBinding.included.tvEdit.hide()
            }
            dataBinding.included.tvUserName.text = model.fullName
            dataBinding.included.tvTypeAddress.text = model.addressType
            dataBinding.included.tvAddress.text = model.address
            dataBinding.included.tvMobileNo.text = model.mobileNo
            dataBinding.included.tvEdit.onClick {
                launchActivity<AddAddressActivity>(Constants.RequestCode.ADD_ADDRESS) {
                    putExtra(Constants.KeyIntent.DATA, model)
                    putExtra(Constants.KeyIntent.ADDRESS_ID, position)
                }
            }
        }

        override fun onItemClick(view: View, model: Address, position: Int, dataBinding: ItemUserAddressBinding) {
            setDefaultAddress(position)
        }

        override fun onItemLongClick(view: View, model: Address, position: Int) {

        }

    }

    private var cartAdapter: BaseRecyclerAdapter<Key, ItemCartBinding> =
        object : BaseRecyclerAdapter<Key, ItemCartBinding>() {
            override val layoutResId: Int = R.layout.item_cart

            override fun onBindData(model: Key, position: Int, dataBinding: ItemCartBinding) {
                dataBinding.tvOriginalPrice.applyStrike()
                dataBinding.ivDropDown.visibility=View.INVISIBLE
                dataBinding.qtySpinner.text = model.quantity.toString()
                dataBinding.tvOriginalPrice.applyStrike()
                if (model.sale_price.isNotEmpty()) {
                    dataBinding.tvPrice.text =
                        (model.sale_price.toInt() * model.quantity).toString().currencyFormat()
                }
                if (model.product_price.isNotEmpty()) {
                    dataBinding.tvOriginalPrice.text =
                        (model.product_price.toInt() * model.quantity).toString().currencyFormat()
                }
                dataBinding.ivProduct.loadImageFromUrl(model.product_image)
            }

            override fun onItemClick(
                view: View,
                model: Key,
                position: Int,
                dataBinding: ItemCartBinding
            ) {
            }

            override fun onItemLongClick(view: View, model: Key, position: Int) {

            }
        }
    private val mImg = ArrayList<String>()
    private var total = 0

    private fun setDefaultAddress(position: Int) {
        mAddressAdapter.mModelList.forEachIndexed { i: Int, address: Address ->
            address.isDefault = i == position
        }
        mAddressAdapter.notifyDataSetChanged()
        setAddressList(mAddressAdapter.mModelList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)
        setToolbar(toolbar)
        title = getString(R.string.order_summary)
        getOffers()
        rvItems.setVerticalLayout()
        rvItems.adapter = cartAdapter
        cartAdapter.addItems(getCartList())
        total = getCartTotal()

        initChangeAddressDialog()
        btnChangeAddress.onClick {
            if (mAddressAdapter.size == 0) {
                launchActivity<AddAddressActivity>(Constants.RequestCode.ADD_ADDRESS)
            } else {
                dialog.show()
            }
        }
        val mPaymentDetail = getCartTotal()
        tvReset.text = mPaymentDetail.toString().currencyFormat()
        tvApply.onClick {
            createOrder()
        }
        if (getAddressList().size == 0) {
            launchActivity<AddAddressActivity>(Constants.RequestCode.ADD_ADDRESS)
            llAddress.hide()
        } else {
            llAddress.show()
        }
        loadBannerAd(R.id.adView)

    }

    private fun getOffers() {
        listAllProducts {
            it.forEach {
                if (it.images.isNotEmpty()) {
                    mImg.add(it.images[0].src)
                }
            }
            val handler = Handler()
            val runnable = object : Runnable {
                var i = 0
                override fun run() {
                    ivOffer.loadImageFromUrl(mImg[i])
                    i++
                    if (i > mImg.size - 1) {
                        i = 0
                    }
                    handler.postDelayed(this, 3000)
                }
            }
            handler.postDelayed(runnable, 3000)

        }
    }

    private fun initChangeAddressDialog() {
        dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.setContentView(R.layout.dialog_change_address)
        dialog.window?.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.tvAddNewAddress.onClick {
            launchActivity<AddAddressActivity>(Constants.RequestCode.ADD_ADDRESS)
        }
        dialog.tvItemDeliverHere.onClick {
            dialog.dismiss()
            llAddress.show()
            updateAddress()
        }
        dialog.rvAddress.setVerticalLayout()
        dialog.rvAddress.adapter = mAddressAdapter
        loadAddressList()
        updateAddress()
    }

    private fun updateAddress() {
        mAddressAdapter.mModelList.forEach {
            if (it.isDefault!!) {
                tvUserName.text = it.fullName
                tvTypeAddress.text = it.addressType
                tvAddress.text = it.address
                tvMobileNo.text = it.mobileNo
            }
        }
    }

    private fun loadAddressList() {
        mAddressAdapter.addItems(getAddressList())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RequestCode.ADD_ADDRESS) {
            loadAddressList()
            if (mAddressAdapter.itemCount == 0) {
                finish()
            } else {
                dialog.show()
            }
        } else if (requestCode == Constants.RequestCode.PAYMENT && resultCode == Activity.RESULT_OK) {
            getSharedPrefInstance().removeKey(Constants.SharedPref.KEY_USER_CART)
            getSharedPrefInstance().removeKey(Constants.SharedPref.KEY_CART_COUNT)
            setResult(Activity.RESULT_OK)
            finish()
        }

    }

    private fun createOrder() {
        val requestModel = MyOrderData()
        val mData = ArrayList<LineItem>()

        getCartList().forEach {
            val mlineitem = LineItem()
            mlineitem.product_id = it.product_id
            mlineitem.quantity = it.quantity
            mlineitem.variation_id = it.variation_id
            mlineitem.image = it.product_image
            mlineitem.name = it.product_name
            mlineitem.total = it.product_price.toDouble()
            mlineitem.quantity = it.quantity
            mlineitem.size = it.product_size
            mlineitem.color = it.product_color
            mData.add(mlineitem)
        }
        requestModel.line_items = mData
        mAddressAdapter.mModelList.forEach {
            if (it.isDefault!!) {
                val mShippingRequest = Shipping()
                mShippingRequest.first_name = it.fullName!!
                mShippingRequest.last_name = it.fullName!!
                mShippingRequest.address_1 = it.address!!
                mShippingRequest.address_2 = it.address!!
                mShippingRequest.city = it.city!!
                mShippingRequest.state = it.state!!
                mShippingRequest.postcode = it.pincode!!
                mShippingRequest.country = it.state!!
                requestModel.shipping = mShippingRequest
            }

        }
        requestModel.customer_id = getUserId().toInt()
        requestModel.status = Constants.OrderStatus.PENDING
        requestModel.total = total.toDouble()
        requestModel.date_created = Constants.FULL_DATE_FORMATTER.format(Date())
        dialog.dismiss()
        launchActivity<PaymentActivity>(Constants.RequestCode.PAYMENT) {
            putExtra(Constants.KeyIntent.DATA, requestModel)
        }
    }


}