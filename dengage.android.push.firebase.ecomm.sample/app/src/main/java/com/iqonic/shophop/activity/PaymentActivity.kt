package com.iqonic.shophop.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.ShopHopApp.Companion.getAppInstance
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.databinding.ItemCardBinding
import com.iqonic.shophop.fragments.BaseFragment
import com.iqonic.shophop.models.Card
import com.iqonic.shophop.models.MyOrderData
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.OTPEditText
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.dialog_change_password.*
import kotlinx.android.synthetic.main.dialog_success_transaction.*
import kotlinx.android.synthetic.main.toolbar.*


class PaymentActivity : AppBaseActivity() {
    private var listItems: Array<String>? = null
    private val mCardAdapter = object : BaseRecyclerAdapter<Card, ItemCardBinding>() {
        override fun onItemLongClick(view: View, model: Card, position: Int) {
        }

        override fun onItemClick(view: View, model: Card, position: Int, dataBinding: ItemCardBinding) {
            createPaymentRequest("card")

        }

        override val layoutResId: Int = R.layout.item_card

        override fun onBindData(model: Card, position: Int, dataBinding: ItemCardBinding) {
            dataBinding.model = model
            Glide.with(getAppInstance()).load(model.cardImg).into(dataBinding.ivCardbg)
        }
    }
    private var orderData:MyOrderData?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        setToolbar(toolbar)
        title = getString(R.string.title_payment)
        orderData=intent.getSerializableExtra(Constants.KeyIntent.DATA) as MyOrderData
        rvCard.setHorizontalLayout()
        rvCard.setHasFixedSize(true)
        rvCard.adapter = mCardAdapter
        mCardAdapter.addItems(getItems())
        addPaymentDetails()
        listItems = resources.getStringArray(R.array.other_wallet)
        tvOther.onClick {
            val mBuilder = AlertDialog.Builder(this@PaymentActivity)
            mBuilder.setTitle(context.getString(R.string.lbl_choose_item))
            mBuilder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
                tvOther.text = (listItems as Array<String>)[i]
                dialogInterface.dismiss()
            }
            val mDialog = mBuilder.create()
            mDialog.show()
        }
        btnAddCard.onClick {
            launchActivity<CardActivity> { }
        }
        tvPayWithCards.onClick {
          createPaymentRequest("paypal")
        }
        tvNetBanking.onClick {
          //  createPaymentRequest("paypal")

        }
        tvCash.onClick {
            createPaymentRequest("cod")
        }
        loadBannerAd(R.id.adView)

    }
    private fun showChangePasswordDialog() {
        //if (changePasswordDialog==null){
        val changePasswordDialog = Dialog(this)
        changePasswordDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        changePasswordDialog.setContentView(R.layout.dialog_success_transaction)
        changePasswordDialog.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        changePasswordDialog.tv_close.onClick {
            changePasswordDialog.dismiss()
            finish()
        }
        changePasswordDialog.show()
    }

    private fun createPaymentRequest(s: String) {
        orderData?.payment_method = s
        addOrder(orderData!!)
        snackBar("Successfully")
        setResult(Activity.RESULT_OK)
        showChangePasswordDialog()
    }


    private fun getItems(): ArrayList<Card> {
        val list = ArrayList<Card>()
        val card1 = Card()
        card1.cardImg = R.drawable.card
        card1.cardType = "Debit Card"
        card1.bankName = "MVK Bank"
        card1.cardNumber = "3434  5444  5454  4354"
        card1.validDate = " 12/22"
        card1.holderName = "John"

        val product1 = Card()
        product1.cardImg = R.drawable.card
        product1.cardType = "Debit Card"
        product1.bankName = "MVK Bank"
        product1.cardNumber = "3434 5444 5454 4354"
        product1.validDate = "12/22"
        product1.holderName = "John"

        val product2 = Card()
        product2.cardImg = R.drawable.card
        product2.cardType = "Debit Card"
        product2.bankName = "MVK Bank"
        product2.cardNumber = "3434 5444 5454 4354"
        product2.validDate = "12/22"
        product2.holderName = "John"

        list.add(card1)
        list.add(product1)
        list.add(product2)
        return list
    }

    private fun addPaymentDetails() {
        getCartTotal()
    }


}