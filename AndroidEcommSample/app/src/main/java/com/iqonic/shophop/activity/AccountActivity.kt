package com.iqonic.shophop.activity

import android.app.Activity
import android.os.Bundle
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.toolbar.*

class AccountActivity : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        setToolbar(toolbar)
        title = getString(R.string.title_account)

        txtDisplayName.text = getUserFullName()

        btnSignOut.onClick {
            val dialog = getAlertDialog(getString(R.string.lbl_logout_confirmation), onPositiveClick = { _, _ ->
                clearLoginPref()
                launchActivityWithNewTask<DashBoardActivity> ()
            }, onNegativeClick = { dialog, _ ->
                dialog.dismiss()
            })
            dialog.show()
        }
        tvOrders.onClick {
            launchActivity<OrderActivity>()
        }
        tvQuickPay.onClick {
            launchActivity<QuickPayActivity>()
        }
        tvOffer.onClick {
            launchActivity<OfferActivity>()
        }
        btnVerify.onClick {
            launchActivity<OTPActivity>()
        }
        tvAddressManager.onClick {
            if (getAddressList().size == 0) {
                launchActivity<AddAddressActivity>()
            } else {
                launchActivity<AddressManagerActivity>()
            }
        }
        ivProfileImage.onClick {
            launchActivity<EditProfileActivity> ()
        }
        tvWishlist.onClick {
            setResult(Activity.RESULT_OK)
            finish()
        }
        tvHelpCenter.onClick {
            launchActivity<HelpActivity>()
        }
        loadBannerAd(R.id.adView)
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }
}