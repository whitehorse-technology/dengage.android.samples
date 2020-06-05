package com.iqonic.shophop.activity

import android.os.Bundle
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import android.widget.FrameLayout
import com.iqonic.shophop.fragments.SignInFragment
import com.iqonic.shophop.fragments.SignUpFragment
import com.iqonic.shophop.utils.extensions.addFragment
import com.iqonic.shophop.utils.extensions.fadeIn
import com.iqonic.shophop.utils.extensions.removeFragment
import com.iqonic.shophop.utils.extensions.replaceFragment
import dengage.android.push.firebase.DengageEvent


class SignInUpActivity : AppBaseActivity() {

    private val mSignInFragment: SignInFragment = SignInFragment()
    private val mSignUpFragment: SignUpFragment = SignUpFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_up)

        /**
         * Load Default Fragment
         */
        loadSignInFragment()
    }

    fun loadSignUpFragment() {
        var data : HashMap<String, Any>
                = HashMap<String, Any> ();
        data.put("page_type", "register");

        DengageEvent.getInstance(applicationContext, intent).pageView(data)
        if (mSignUpFragment.isAdded) {
            replaceFragment(mSignUpFragment, R.id.fragmentContainer)
            findViewById<FrameLayout>(R.id.fragmentContainer).fadeIn(500)
        } else {
            addFragment(mSignUpFragment, R.id.fragmentContainer)
        }
    }

    fun loadSignInFragment() {

        var data : HashMap<String, Any>
                = HashMap<String, Any> ();
        data.put("page_type", "login");
        DengageEvent.getInstance(applicationContext, intent).pageView(data)

        if (mSignInFragment.isAdded) {
            replaceFragment(mSignInFragment, R.id.fragmentContainer)
            findViewById<FrameLayout>(R.id.fragmentContainer).fadeIn(500)
        } else {
            addFragment(mSignInFragment, R.id.fragmentContainer)
        }
    }

    override fun onBackPressed() {
        when {
            mSignUpFragment.isVisible -> {
                removeFragment(mSignUpFragment)
            }
            else -> super.onBackPressed()

        }
    }
}