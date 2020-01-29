package com.iqonic.shophop.activity

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.models.Address
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.SimpleLocation
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.layout_address_type.*
import kotlinx.android.synthetic.main.toolbar.*

class AddAddressActivity : AppBaseActivity(), SimpleLocation.Listener {

    private lateinit var dialog: Dialog
    private var address: Address? = null
    private var simpleLocation: SimpleLocation? = null
    private var addressId: Int? = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)
        setToolbar(toolbar)

        simpleLocation = SimpleLocation(this)
        simpleLocation?.setListener(this)

        if (intent.hasExtra(Constants.KeyIntent.DATA)) {
            address = intent?.getSerializableExtra(Constants.KeyIntent.DATA) as Address
            addressId = intent?.getIntExtra(Constants.KeyIntent.ADDRESS_ID, -1)
        }

        if (address != null) {
            title = getString(R.string.lbl_edit_address)
            edtFullName.setText(address?.fullName!!)
            edtCity.setText(address?.city!!)
            edtState.setText(address?.state!!)
            edtPinCode.setText(address?.pincode!!)
            edtAddressType.setText(address?.addressType!!)
            edtAddress.setText(address?.address!!)
            edtMobileNo.setText(address?.mobileNo!!)
            edtFullName.setSelection(edtFullName.text.length - 1)
        } else {
            title = getString(R.string.lbl_add_new_address)
        }
        btnSaveAddress.onClick {
            if (validate()) {
                if (address == null) {
                    address = Address()
                    assignData()
                    addAddress(address!!)
                } else {
                    val list = getAddressList()
                    val adr = list[addressId!!]
                    adr.fullName = edtFullName.text.toString()
                    adr.city = edtCity.text.toString()
                    adr.state = edtState.text.toString()
                    adr.pincode = edtPinCode.text.toString()
                    adr.addressType = edtAddressType.text.toString()
                    adr.address = edtAddress.text.toString()
                    adr.mobileNo = edtMobileNo.text.toString()
                    setAddressList(list)
                }
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        initAddressTypeDialog()
        edtAddressType.onClick {
            dialog.show()
        }
        rlUseCurrentLocation.onClick {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), onResult = {
                if (it) {
                    if (isGPSEnable()) {
                        if (isNetworkAvailable()) {
                            showProgress(true)
                            simpleLocation?.beginUpdates()
                        } else {
                            snackBarError(getString(R.string.error_gps_not_enabled))
                        }
                    } else {
                        showGPSEnableDialog()
                    }
                } else {
                    showPermissionAlert(this)
                }
            })
        }
        loadBannerAd(R.id.adView)

    }

    private fun initAddressTypeDialog() {
        dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.layout_address_type)
        dialog.rgAddressType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbHome -> {
                    dialog.dismiss()
                    edtAddressType.setText(getString(R.string.lbl_home))
                }
                R.id.rbOffice -> {
                    dialog.dismiss()
                    edtAddressType.setText(getString(R.string.lbl_office_commercial))
                }
            }
        }
        dialog.ivClose.onClick {
            dialog.dismiss()
        }
    }

    private fun validate(): Boolean {
        when {
            edtFullName.checkIsEmpty() -> {
                edtFullName.showError(getString(R.string.error_field_required))
                edtFullName.requestFocus()
                return false
            }
            edtPinCode.checkIsEmpty() -> {
                edtPinCode.showError(getString(R.string.error_field_required))
                edtPinCode.requestFocus()
                return false
            }
            edtCity.checkIsEmpty() -> {
                edtCity.showError(getString(R.string.error_field_required))
                edtCity.requestFocus()
                return false
            }
            edtState.checkIsEmpty() -> {
                edtState.showError(getString(R.string.error_field_required))
                edtState.requestFocus()
                return false
            }
            edtAddressType.checkIsEmpty() -> {
                snackBar(getString(R.string.error_address_type))
                edtAddressType.requestFocus()
                return false
            }
            edtAddress.checkIsEmpty() -> {
                edtAddress.showError(getString(R.string.error_field_required))
                edtAddress.requestFocus()
                return false
            }
            edtMobileNo.checkIsEmpty() -> {
                edtMobileNo.showError(getString(R.string.error_field_required))
                edtMobileNo.requestFocus()
                return false
            }
            else -> return true
        }
    }

    override fun onPositionChanged() {
        showProgress(false)

        val address = simpleLocation?.address
        if (address != null) {
            edtState.setText(address.adminArea)
            edtPinCode.setText(address.postalCode)
            edtCity.setText(address.locality)
            if (address.getAddressLine(0) != null) {
                edtAddress.setText(address.getAddressLine(0))
            }
            simpleLocation?.endUpdates()
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    private fun assignData() {
        if (address !== null) {
            address!!.fullName = edtFullName.text.toString()
            address!!.city = edtCity.text.toString()
            address!!.state = edtState.text.toString()
            address!!.pincode = edtPinCode.text.toString()
            address!!.addressType = edtAddressType.text.toString()
            address!!.address = edtAddress.text.toString()
            address!!.mobileNo = edtMobileNo.text.toString()
        }
    }
}