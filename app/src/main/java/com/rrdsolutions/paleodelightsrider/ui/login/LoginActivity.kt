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

    var username = ""
    var password = ""

    data class Logindetails(
        val username:String,
        val password:String
    )

    data class Logindetails2(
        val username: String,

        val encryptedpassword: String,
        val key: SecretKeys
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(this)

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
        vm.visibility.value = View.GONE
        vm.visibility.observe(this, Observer{
            loadingscreen.visibility = it
        })

        checkLogin()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        this.startActivity(a)
    }

    private fun checkLogin(){

        //decrypt here


        val file = File(this.filesDir.path.toString() + "logindetails")
        if (file.exists()) {
           val logindetails = Gson().fromJson(file.readText(), Logindetails::class.java)
           username = logindetails.username
           password = logindetails.password
        }
        if (username !="" && password !="") toMainActivity()
    }

    private fun saveLogin(){
        val file = File(this.filesDir.path.toString() + "logindetails")

        val savedlogin = Logindetails(username, password)
        file.writeText(Gson().toJson(savedlogin))

        //encrypt here

        //val savedlogin = encrypt()
        //file.writeText(encryptSavedLogin())

        //encryptedlogin = ""
        //file.writeText(encryptedlogin)


    }

    private fun toMainActivity(){
        username_edt.setText("")
        password_edt.setText("")

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("username", username)
        }
        startActivity(intent)
        Log.d("_login", "moving to MainActivity")
    }

    fun encryptSavedLogin(): String{
        val key = generateKeyFromPassword(password, saltString(generateSalt()))
        val encryptedpassword = encrypt(password, key).toString()
        val logindetails2 = Logindetails2(username, encryptedpassword, key)
        val result = Gson().toJson(logindetails2)

        return result
    }

    fun decrypt(string:String){
        val logindetails2 = Gson().fromJson(string, Logindetails2::class.java)
        val encryptedpassword = logindetails2.encryptedpassword
        val key = logindetails2.key
        password = decryptString(CipherTextIvMac(encryptedpassword), key)
        username = logindetails2.username

    }

}

