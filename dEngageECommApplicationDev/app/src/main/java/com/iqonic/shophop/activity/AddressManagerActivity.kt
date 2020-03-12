package com.iqonic.shophop.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.databinding.ItemAddressNewBinding
import com.iqonic.shophop.models.Address
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.activity_address_manager.*
import kotlinx.android.synthetic.main.toolbar.*

class AddressManagerActivity : AppBaseActivity() {

    private var addressAdapter = object : BaseRecyclerAdapter<Address, ItemAddressNewBinding>() {
        override val layoutResId: Int = R.layout.item_address_new

        override fun onBindData(model: Address, position: Int, dataBinding: ItemAddressNewBinding) {
            dataBinding.rbDefaultAddress.isChecked = model.isDefault!!
            dataBinding.included.tvUserName.text = model.fullName
            dataBinding.included.tvTypeAddress.text = model.addressType
            dataBinding.included.tvAddress.text = model.address
            dataBinding.included.tvMobileNo.text = model.mobileNo
        }

        override fun onItemClick(view: View, model: Address, position: Int, dataBinding: ItemAddressNewBinding) {
            when (view.id) {
                R.id.addressLayout -> {
                    setDefaultAddress(position)
                }
                R.id.fabEdit -> {
                    dataBinding.swipeLayout.close(true)
                    runDelayed(200) {
                        launchActivity<AddAddressActivity>(Constants.RequestCode.ADD_ADDRESS) {
                            putExtra(Constants.KeyIntent.DATA, model)
                            putExtra(Constants.KeyIntent.ADDRESS_ID, position)
                        }
                    }
                }
                R.id.fabDelete -> {
                    getAlertDialog(getString(R.string.msg_confirmation), onPositiveClick = { _, _ ->
                        mModelList.removeAt(position)
                        notifyItemRemoved(position)
                        setAddressList(mModelList)
                    }, onNegativeClick = { dialog, _ ->
                        dialog.dismiss()
                    }).show()
                }
            }
        }

        override fun onItemLongClick(view: View, model: Address, position: Int) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_manager)
        setToolbar(toolbar)
        title = getString(R.string.lbl_address_manager)
        rvAddress.setVerticalLayout()
        rvAddress.adapter = addressAdapter
        loadAddressList()
        btnAddNew.onClick {
            launchActivity<AddAddressActivity>(Constants.RequestCode.ADD_ADDRESS)
        }
        loadBannerAd(R.id.adView)

    }

    private fun loadAddressList() {
        addressAdapter.addItems(getAddressList())
    }

    private fun setDefaultAddress(position: Int) {
        addressAdapter.mModelList.forEachIndexed { i: Int, address: Address ->
            address.isDefault = i == position
        }
        addressAdapter.notifyDataSetChanged()
        setAddressList(addressAdapter.mModelList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RequestCode.ADD_ADDRESS && resultCode == Activity.RESULT_OK) {
            loadAddressList()
        }
    }
}