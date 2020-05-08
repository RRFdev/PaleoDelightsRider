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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.rrdsolutions.paleodelightsrider.R

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_main.*

class LogFragment : Fragment() {
    private lateinit var vm: LogViewModel
    var username = ""
    var password = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        activity?.findViewById<NavigationView>(R.id.nav_view)?.visibility = View.GONE
//        activity?.findViewById<AppBarLayout>(R.id.appbar)?.visibility = View.GONE
//        activity?.findViewById<DrawerLayout>(R.id.drawer)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        hideDrawer(true)
        //activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.GONE
        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(this).get(LogViewModel::class.java)

        //code body here
        Log.d("_login", "fragment loaded")
        activity?.getPreferences(0)?.edit()?.putBoolean("back", false)?.apply()
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

    override fun onResume(){
        super.onResume()

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
        Login.username = username
        Login.password = password
        activity?.findViewById<NavigationView>(R.id.nav_view)?.setCheckedItem(R.id.nav_currentdelivery)
        //MainActivity().username = username
        //val bundle = Bundle()
        //bundle.putString("username",username)

//
//        val fm = fragmentManager
//        fm?.beginTransaction()
//            ?.replace(R.id.nav_host_fragment, CurrentDeliveryFragment())
//            //?.addToBackStack(null)
//            ?.commit()

//        activity?.supportFragmentManager?.commit{
//
//            remove(CurrentDeliveryFragment())
//            add(LogFragment(), "")
//            //replace(R.id.nav_host_fragment, CurrentDeliveryFragment())
//            //addToBackStack(null)
//        }



        view?.let { findNavController(it).navigate(R.id.nav_currentdelivery) }
        //Fragment.findNavController().navigate(R.id.nav_currentdelivery)

        vm.visibility.value = View.GONE
          hideDrawer(false)

    }

    fun hideDrawer(boolean:Boolean){
        if (boolean == true){
            activity?.findViewById<NavigationView>(R.id.nav_view)?.visibility = View.GONE
            activity?.findViewById<AppBarLayout>(R.id.appbar)?.visibility = View.GONE
            activity?.findViewById<DrawerLayout>(R.id.drawer)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
        else{
            activity?.findViewById<NavigationView>(R.id.nav_view)?.visibility = View.VISIBLE
            activity?.findViewById<AppBarLayout>(R.id.appbar)?.visibility = View.VISIBLE
            activity?.findViewById<DrawerLayout>(R.id.drawer)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

}

object Login{
    var username = ""
    var password = ""
}