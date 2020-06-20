package com.rrdsolutions.paleodelightsrider.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.pddstudio.preferences.encrypted.EncryptedPreferences
import com.rrdsolutions.paleodelightsrider.LoginResult
import com.rrdsolutions.paleodelightsrider.MainActivity
import com.rrdsolutions.paleodelightsrider.R
import kotlinx.android.synthetic.main.activity_login.*
class LoginActivity : AppCompatActivity() {

    lateinit var vm: LoginViewModel
    lateinit var ep: EncryptedPreferences
    var username = ""
    var password = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ep = EncryptedPreferences.Builder(applicationContext)
            .withEncryptionPassword("432fdsfds3ll4")
            .build()
        vm = ViewModelProvider(this).get(LoginViewModel::class.java)
        checkLogin()
        loginbutton.setOnClickListener{

            vm.visibility.value = View.VISIBLE

            username = username_edt.text.toString()
            password = password_edt.text.toString()

            vm.loginWith(username, password){ callback->
                when (callback){
                    LoginResult.LOGIN_SUCCESS->{
                        if (checkbox.isChecked) saveLogin()
                        toMainActivity()
                    }
                    LoginResult.PASSWORD_INCORRECT->{
                        Toast.makeText(this, "Error: Password incorrect",
                            Toast.LENGTH_LONG).show()
                        vm.visibility.value = View.GONE
                    }
                    LoginResult.USERNAME_INCORRECT->{
                        Toast.makeText(this, "Error: Username incorrect",
                            Toast.LENGTH_LONG).show()
                        vm.visibility.value = View.GONE
                    }
                    LoginResult.LOGIN_FAIL->{
                        Toast.makeText(this, "Login failed. Please check your internet connection and try again.",
                        Toast.LENGTH_LONG).show()
                        vm.visibility.value = View.GONE
                    }
                }
            }
        }

        vm.visibility.observe(this, Observer{
            loadingscreen.visibility = it
        })



    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    fun checkLogin(){
        username = ep.getString("username", "")
        password = ep.getString("password", "")
        if (username !="" && password !="") toMainActivity()
    }

    fun saveLogin(){
        ep.edit()
            .putString("username", username)
            .putString("password", password)
            .apply()
    }

    fun toMainActivity(){
        username_edt.setText("")
        password_edt.setText("")

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("username", username)
        }
        startActivity(intent)
    }

}

