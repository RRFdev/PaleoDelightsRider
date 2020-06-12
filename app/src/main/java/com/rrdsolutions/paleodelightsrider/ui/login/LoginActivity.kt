package com.rrdsolutions.paleodelightsrider.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.pddstudio.preferences.encrypted.EncryptedPreferences
import com.rrdsolutions.paleodelightsrider.MainActivity
import com.rrdsolutions.paleodelightsrider.R
import com.tozny.crypto.android.AesCbcWithIntegrity
import com.tozny.crypto.android.AesCbcWithIntegrity.*
import kotlinx.android.synthetic.main.activity_login.*
import java.io.File
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class LoginActivity : AppCompatActivity() {

    lateinit var vm: LoginViewModel
    lateinit var ep: EncryptedPreferences
    var username = ""
    var password = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("_login", "ONCREATE")

        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(this)

        ep = EncryptedPreferences.Builder(applicationContext)
            .withEncryptionPassword("432fdsfds3ll4")
            .build()

        vm = ViewModelProvider(this).get(LoginViewModel::class.java)

        loginbutton.setOnClickListener{

            vm.visibility.value = View.VISIBLE

            username = username_edt.text.toString()
            password = password_edt.text.toString()
            Log.d("_login", "username = $username, password = $password")

            vm.loginWith(username, password){ callback->
                when (callback){
                    "login success"->{
                        Log.d("_login", "login success")
                        if (checkbox.isChecked) saveLogin()
                        toMainActivity()
                    }
                    "password incorrect"->{
                        Log.d("_login", "password incorrect")
                        Toast.makeText(this, "Error: Password incorrect",
                            Toast.LENGTH_LONG).show()
                        vm.visibility.value = View.GONE
                    }
                    "username incorrect"->{
                        Log.d("_login", "username incorrect")
                        Toast.makeText(this, "Error: Username incorrect",
                            Toast.LENGTH_LONG).show()
                        vm.visibility.value = View.GONE
                    }
                    "login fail"->{
                        Log.d("_login", "login fail")
                        Toast.makeText(this, "Login failed. Please check your internet connection and try again.",
                        Toast.LENGTH_LONG).show()
                        vm.visibility.value = View.GONE
                    }
                }

            }
        }

    }

    override fun onResume(){
        super.onResume()

        Log.d("_login", "ONRESUME")

        vm.visibility.value = View.GONE
        vm.visibility.observe(this, Observer{
            loadingscreen.visibility = it
        })

        checkLogin()
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
        Log.d("_login", "moving to MainActivity")
    }

}

