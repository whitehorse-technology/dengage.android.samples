package com.iqonic.shophop.fragments

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import com.iqonic.shophop.R
import com.iqonic.shophop.activity.DashBoardActivity
import com.iqonic.shophop.activity.SignInUpActivity
import com.iqonic.shophop.utils.OTPEditText
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.dialog_change_password.*
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.layout_otp.*

class SignInFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFragment()
    }

    private fun initializeFragment() {
        edtEmail.onFocusChangeListener = this
        edtPassword.onFocusChangeListener = this
        edtPassword.transformationMethod = BiggerDotTransformation

        edtEmail.setSelection(edtEmail.length())
        btnSignIn.onClick {
            if (validate()) {
                doLogin()
            }
        }

        tvForget.onClick {
            snackBar("Coming Soon")
        }

        btnSignUp.onClick {
            (activity as SignInUpActivity).loadSignUpFragment()
        }
        tvForget.onClick {
            showChangePasswordDialog()
        }
    }

    private fun validate(): Boolean {
        return when {
            edtEmail.checkIsEmpty() -> {
                edtEmail.showError(getString(R.string.error_field_required))
                false
            }
            !edtEmail.isValidEmail() -> {
                edtEmail.showError(getString(R.string.error_enter_valid_email))
                false
            }
            edtPassword.checkIsEmpty() -> {
                edtPassword.showError(getString(R.string.error_field_required))
                false
            }
            else -> true
        }
    }

    private fun doLogin() {
        signIn(onResult = {
            if (it) {
                if (activity != null) {
                    activity!!.launchActivityWithNewTask<DashBoardActivity>()
                }
            }
        })
    }
    private var mEds: Array<EditText?>? = null

    private fun showChangePasswordDialog() {
        //if (changePasswordDialog==null){
        val changePasswordDialog = Dialog(activity!!)
        changePasswordDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        changePasswordDialog.setContentView(R.layout.dialog_change_password)
        changePasswordDialog.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        changePasswordDialog.edtNewPassword.transformationMethod = BiggerDotTransformation
        changePasswordDialog.edtconfirmPassword.transformationMethod = BiggerDotTransformation
        mEds = arrayOf(changePasswordDialog.findViewById(R.id.edDigit1), changePasswordDialog.findViewById(R.id.edDigit2), changePasswordDialog.findViewById(R.id.edDigit3), changePasswordDialog.findViewById(R.id.edDigit4))
        OTPEditText(mEds!!, activity!!, activity?.getDrawable(R.color.transparent)!!, activity?.getDrawable(R.drawable.bg_unselected_dot)!!)
        mEds!!.forEachIndexed { _, editText ->
            editText?.onFocusChangeListener = focusChangeListener
        }
        changePasswordDialog.show()
    }
    private val focusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (hasFocus)
            (v as EditText).background = activity?.getDrawable(R.color.transparent)
    }


}