package com.rrdsolutions.paleodelightsrider.ui.login

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings

class LoginViewModel: ViewModel(){
    var visibility = MutableLiveData<Int>().apply{ value = View.GONE }

    fun loginWith(username: String, password:String, callback:(String)-> Unit){
        val db = FirebaseFirestore.getInstance()
            .apply{ firestoreSettings = firestoreSettings{isPersistenceEnabled = false} }
            .collection("riders")
        val db2 = FirebaseFirestore.getInstance().collection("riders")

        db.document(username).get()
            .addOnSuccessListener{ document->

                if (document.exists()){
                    val realpassword = document.data?.get("password") as String
                    Log.d("_login", "realpassword = $realpassword")

                    if (realpassword == password){
                        callback("login success")
                    }
                    else{
                        callback("password incorrect")
                    }
                }
                else callback ("username incorrect")

            }
            .addOnFailureListener{
                callback("login fail")
            }
    }
}