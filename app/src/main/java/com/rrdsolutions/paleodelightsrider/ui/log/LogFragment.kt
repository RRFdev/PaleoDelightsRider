package com.rrdsolutions.paleodelightsrider.ui.log

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.rrdsolutions.paleodelightsrider.R
import com.rrdsolutions.paleodelightsrider.ui.currentdelivery.CurrentDeliveryFragment
import kotlinx.android.synthetic.main.activity_login.*

class LogFragment : Fragment() {
    private lateinit var vm: LogViewModel
    var username = ""
    var password = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.findViewById<NavigationView>(R.id.nav_view)?.visibility = View.GONE
        activity?.findViewById<AppBarLayout>(R.id.appbar)?.visibility = View.GONE
        //activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.GONE
        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(this).get(LogViewModel::class.java)
        //code body here
        Log.d("_login", "fragment loaded")

//
        vm.visibility.observe(viewLifecycleOwner, Observer{
            activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = it
        })

        checkLogin()

        loginbutton.setOnClickListener{

            vm.visibility.value = View.VISIBLE

            username = username_edt.text.toString()
            password = password_edt.text.toString()
            Log.d("_login", "username = $username, password = $password")

            vm.loginWith(username, password){ callback->
                when (callback){
                    "login success"->{
                        Log.d("_login", "login success")
                        status.text = "Login success"
                        status.setTextColor("#a4c639".toColorInt())
                        if (checkbox.isChecked) saveLogin()
                        login()
                    }
                    "password incorrect"->{
                        Log.d("_login", "password incorrect")
                        status.text = "Password incorrect"
                        status.setTextColor("#FF0000".toColorInt())
                        vm.visibility.value = View.GONE
                    }
                    "username incorrect"->{
                        Log.d("_login", "username incorrect")
                        status.text = "Username incorrect"
                        status.setTextColor("#FF0000".toColorInt())
                        vm.visibility.value = View.GONE
                    }
                    "login fail"->{
                        Log.d("_login", "login fail")
                        status.text = "Login failed"
                        status.setTextColor("#FF0000".toColorInt())
                        vm.visibility.value = View.GONE
                    }
                }

            }
        }


    }

    private fun checkLogin(){
        username = activity?.getPreferences(0)?.getString("username", "").toString()
        password = activity?.getPreferences(0)?.getString("password", "").toString()
        Log.d("_login", "username = $username, password = $password")
        if (username !="" && password !="") login()
    }

    private fun saveLogin(){
        activity?.getPreferences(0)?.edit()?.putString("username", username)?.apply()
        activity?.getPreferences(0)?.edit()?.putString("password", password)?.apply()
        //
        val usernametest = activity?.getPreferences(0)?.getString("username", "").toString()
        val passwordtest = activity?.getPreferences(0)?.getString("password", "").toString()
        Log.d("_login", "username and password saved; " +
                "username = $usernametest, password = $passwordtest")
    }
    private fun login(){
        val username = username
        val frag = CurrentDeliveryFragment().apply{
            arguments?.putString("username", username)
        }

        val fm = fragmentManager
        fm?.beginTransaction()
            ?.replace(R.id.nav_host_fragment, frag)
            ?.addToBackStack(null)
            ?.commit()

        vm.visibility.value = View.GONE
        activity?.findViewById<NavigationView>(R.id.nav_view)?.visibility = View.VISIBLE
        activity?.findViewById<AppBarLayout>(R.id.appbar)?.visibility = View.VISIBLE
    }
}