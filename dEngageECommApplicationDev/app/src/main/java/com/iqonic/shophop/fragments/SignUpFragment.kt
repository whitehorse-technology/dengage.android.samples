package com.iqonic.shophop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.activity.OTPActivity
import com.iqonic.shophop.activity.SignInUpActivity
import com.iqonic.shophop.models.RequestModel
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFragment()
    }

    private fun initializeFragment() {
        edtEmail.onFocusChangeListener = this
        edtPassword.onFocusChangeListener = this
        edtConfirmPassword.onFocusChangeListener = this
        edtFirstName.onFocusChangeListener = this
        edtLastName.onFocusChangeListener = this
        edtPassword.transformationMethod = BiggerDotTransformation
        edtConfirmPassword.transformationMethod = BiggerDotTransformation

        btnSignUp.onClick {
            when {
                validate() -> {
                    createCustomer()
                }
            }
        }
        btnSignIn.onClick {
            (activity as SignInUpActivity).loadSignInFragment()
        }
    }

    private fun createCustomer() {
        val requestModel = RequestModel()
        requestModel.userEmail = edtEmail.textToString()
        requestModel.firstName = edtFirstName.textToString()
        requestModel.lastName = edtLastName.textToString()
        requestModel.password = edtPassword.textToString()
        (activity as AppBaseActivity).registerUser(requestModel, false)
        activity!!.launchActivity<OTPActivity>()
        (activity as SignInUpActivity).loadSignInFragment()

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
            edtPassword.checkIsEmpty() -> {
                edtPassword.showError(getString(R.string.error_field_required))
                false
            }
            edtConfirmPassword.checkIsEmpty() -> {
                edtConfirmPassword.showError(getString(R.string.error_field_required))
                false
            }
            !edtPassword.text.toString().equals(edtConfirmPassword.text.toString(), false) -> {
                edtConfirmPassword.showError(getString(R.string.error_password_not_matches))
                false
            }
            else -> true
        }
    }
}