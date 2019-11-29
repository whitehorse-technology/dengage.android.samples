package com.iqonic.shophop.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.FileProvider
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.BuildConfig
import com.iqonic.shophop.R
import com.iqonic.shophop.models.RequestModel
import com.iqonic.shophop.utils.Constants.SharedPref.USER_PASSWORD
import com.iqonic.shophop.utils.ImagePicker
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.dialog_reset.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.btnChangePassword
import java.io.File

class ProfileFragment : BaseFragment() {
    private var uri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isLoggedIn()) {
            edtEmail.setText(getEmail())
            edtFirstName.setText(getFirstName())
            edtLastName.setText(getLastName())
            edtFirstName.setSelection(edtFirstName.text.length)
            if (getProfile().isNotEmpty()) {
                uri = Uri.parse(getProfile())
                if (uri != null) ivProfileImage.setImageURI(uri)
            }


        }
        setUpListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && data.data != null) {
            ivProfileImage.setImageURI(data.data)
        }
        val path: String? = ImagePicker.getImagePathFromResult(activity!!, requestCode, resultCode, data) ?: return
        uri = FileProvider.getUriForFile(activity!!, BuildConfig.APPLICATION_ID + ".provider", File(path!!))
        ivProfileImage.setImageURI(uri)
    }

    private fun setUpListener() {
        btnSaveProFile.onClick {
            if (validate()) {
                updateProfile()
            }
        }
        btnChangePassword.onClick {
            showChangePasswordDialog()
        }
        btnDeactivate.onClick {

        }
        editProfileImage.onClick {
            activity!!.requestPermissions(
                    arrayOf(
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ), onResult = {
                if (it) {
                    ImagePicker.pickImage(
                            this@ProfileFragment,
                            "Select Image",
                            ImagePicker.mPickImageRequestCode,
                            false
                    )
                } else {
                    activity!!.showPermissionAlert(this)
                }
            })
        }

    }

    private fun showChangePasswordDialog() {

        val changePasswordDialog = Dialog(activity!!)
        changePasswordDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        changePasswordDialog.setContentView(R.layout.dialog_reset)
        changePasswordDialog.window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        changePasswordDialog.edtOldPwd.transformationMethod = BiggerDotTransformation
        changePasswordDialog.edtConfirmPwd.transformationMethod = BiggerDotTransformation
        changePasswordDialog.edtNewPwd.transformationMethod = BiggerDotTransformation

        changePasswordDialog.btnChangePassword.onClick {
            val mPassword = getSharedPrefInstance().getStringValue(USER_PASSWORD)
            Log.d("Password", mPassword)
            when {
                changePasswordDialog.edtOldPwd.checkIsEmpty() -> {
                    changePasswordDialog.edtOldPwd.showError(getString(R.string.error_field_required))
                }
//                changePasswordDialog.edtOldPwd.validPassword() -> {
//                    changePasswordDialog.edtOldPwd.showError(getString(R.string.error_pwd_digit_required))
//                }
                changePasswordDialog.edtNewPwd.checkIsEmpty() -> {
                    changePasswordDialog.edtNewPwd.showError(getString(R.string.error_field_required))
                }
                changePasswordDialog.edtNewPwd.validPassword() -> {
                    changePasswordDialog.edtNewPwd.showError(getString(R.string.error_pwd_digit_required))
                }
                changePasswordDialog.edtConfirmPwd.checkIsEmpty() -> {
                    changePasswordDialog.edtConfirmPwd.showError(getString(R.string.error_field_required))
                }
                changePasswordDialog.edtConfirmPwd.validPassword() -> {
                    changePasswordDialog.edtConfirmPwd.showError(getString(R.string.error_pwd_digit_required))
                }
                !changePasswordDialog.edtConfirmPwd.text.toString().equals(
                        changePasswordDialog.edtNewPwd.text.toString(),
                        false
                ) -> {
                    changePasswordDialog.edtConfirmPwd.showError(getString(R.string.error_password_not_matches))
                }
                /* !changePasswordDialog.edtOldPwd.text.toString().equals(mPassword, false) -> {
                     changePasswordDialog.edtOldPwd.showError(getString(R.string.error_old_password_not_matches))
                 }
                 changePasswordDialog.edtNewPwd.text.toString().equals(mPassword, false) -> {
                     changePasswordDialog.edtNewPwd.showError(getString(R.string.error_new_password_same))
                 }*/
                else -> {
                    Log.d("newPwd", changePasswordDialog.edtNewPwd.text.toString())
                    changePassword(changePasswordDialog.edtNewPwd.text.toString())
                    snackBar(context.getString(R.string.msg_successpwd))
                    changePasswordDialog.dismiss()
                }

            }
        }
        changePasswordDialog.show()


    }


    private fun updateProfile() {
        val requestModel = RequestModel()
        requestModel.userEmail = edtEmail.textToString()
        requestModel.firstName = edtFirstName.textToString()
        requestModel.lastName = edtLastName.textToString()
        if (uri != null) {
            requestModel.image = uri.toString()
        }
        (activity as AppBaseActivity).registerUser(requestModel, true)
        snackBar("Profile Saved Successfully")

    }

    private fun validate(): Boolean {
        return when {
            edtFirstName.checkIsEmpty() -> {
                edtFirstName.showError(getString(R.string.error_field_required))
                false
            }
            edtLastName.checkIsEmpty() -> {
                edtLastName.showError(getString(R.string.error_field_required))
                false
            }
            edtEmail.checkIsEmpty() -> {
                edtEmail.showError(getString(R.string.error_field_required))
                false
            }
            !edtEmail.isValidEmail() -> {
                edtEmail.showError(getString(R.string.error_enter_valid_email))
                false
            }
            else -> true
        }

    }
}