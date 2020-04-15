package com.simplex.whatsup.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.simplex.whatsup.R
import com.simplex.whatsup.api.network.HeaderInterceptor
import com.simplex.whatsup.api.network.NetworkErrorHandler
import com.simplex.whatsup.networkSubscribe
import com.simplex.whatsup.viewmodels.LogInViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_login.*
import org.koin.android.ext.android.inject
import retrofit2.http.Header
import java.util.*

class UserLoginActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth by inject()
    private val logInViewModel: LogInViewModel by inject()
    private val headerInterceptor: HeaderInterceptor by inject()
    private val networkErrorHandler: NetworkErrorHandler by inject()

    private lateinit var providers : List<AuthUI.IdpConfig>
    private var disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        providers = listOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        if (firebaseAuth.currentUser != null) { loggedIn() }
        button_login.setOnClickListener { showSignInOptions() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == MY_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    saveUser(user)
                }
            }
            else {
                Toast.makeText(this, ""+response!!.error!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSignInOptions() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.FirebaseUiTheme)
            .build(), MY_REQUEST_CODE)
    }

    private fun saveUser(currentUser: FirebaseUser) {
        val userObservable = logInViewModel.addUser(currentUser.email!!, currentUser.displayName!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .networkSubscribe( Consumer {
                loggedIn()
            }, networkErrorHandler)

        disposables += userObservable
    }

    private fun loggedIn() {
        firebaseAuth.currentUser?.apply {
            this.getIdToken(true).addOnSuccessListener {
                headerInterceptor.updateToken(it.token)
                val intent = Intent(this@UserLoginActivity, MapsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    companion object {
        const val MY_REQUEST_CODE: Int = 1234
    }

}
