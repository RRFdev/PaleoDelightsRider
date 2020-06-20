package com.rrdsolutions.paleodelightsrider.ui.login

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.rrdsolutions.paleodelightsrider.LoginResult

class LoginViewModel: ViewModel(){
    var visibility = MutableLiveData<Int>().apply{ value = View.GONE }

    fun loginWith(username: String, password:String, callback:(LoginResult)-> Unit){
        val db = FirebaseFirestore.getInstance()
            .apply{ firestoreSettings = firestoreSettings{isPersistenceEnabled = false} }
            .collection("riders")

        db.document(username).get()
            .addOnSuccessListener{ document->

                if (document.exists()){
                    val realpassword = document.data?.get("password") as String

                    if (realpassword == password){
                        callback(LoginResult.LOGIN_SUCCESS)
                    }
                    else{
                        callback(LoginResult.PASSWORD_INCORRECT)
                    }
                }
                else callback (LoginResult.USERNAME_INCORRECT)

            }
            .addOnFailureListener{
                LoginResult.LOGIN_FAIL
            }
    }
}

