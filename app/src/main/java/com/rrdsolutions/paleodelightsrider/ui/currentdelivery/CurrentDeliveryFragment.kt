package com.rrdsolutions.paleodelightsrider.ui.currentdelivery

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import com.rrdsolutions.paleodelightsrider.R

import kotlinx.android.synthetic.main.fragment_currentdelivery.*
import kotlinx.android.synthetic.main.fragment_currentdelivery_layout2.*
import kotlinx.android.synthetic.main.fragment_currentdelivery_layout2.view.*
import kotlinx.android.synthetic.main.fragment_currentdelivery_layout2.view.dotbutton
import kotlinx.android.synthetic.main.notificationcard.view.*

class CurrentDeliveryFragment : Fragment() {

    private lateinit var vm: CurrentDeliveryViewModel
    //private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<Toolbar>(R.id.toolbarmain)?.title = "Current Delivery"


        return inflater.inflate(R.layout.fragment_currentdelivery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(this).get(CurrentDeliveryViewModel::class.java)

        vm.username = activity?.intent?.getStringExtra("username") as String
        //vm.username = arguments?.getString("username")!!
        //vm.username = MainActivity().username
        //vm.username = Login.username
        Log.d("_currentdelivery", "username = $vm.username")

    }

    override fun onPause(){
        layout.removeAllViews()
        super.onPause()
    }



    override fun onResume(){
        super.onResume()

        vm.queryRider(){ callback->

            when (callback){
                "Delivery Present"->loadCurrentDelivery()
                "No Delivery"->loadNoDelivery("No deliveries at the moment")
                "No Connection"->loadNoDelivery("ERROR: Unable to reach database. Please check your online connection")
            }


        }

    }

    fun loadCurrentDelivery(){
        layout.removeAllViews()
        val currentdeliverylayout = layoutInflater.inflate(R.layout.fragment_currentdelivery_layout2, null)

        currentdeliverylayout.dotbutton.setOnClickListener{
            val popupMenu: PopupMenu = PopupMenu(requireContext(),dotbutton)

            val textlist = listOf("haha", "hihi", "huhu")
            //popupMenu.menuInflater.inflate(textlist, popupMenu.menu)
            popupMenu.menu.add("haha")
            popupMenu.menu.add("hihi")
            popupMenu.menu.add("huhu")
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.title) {
                    "haha" ->{

                    }

                    "hihi" ->{

                    }

                    "huhu" ->{

                    }

                }
                true
            })
        }












        activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    fun loadNoDelivery(text:String){
        layout.removeAllViews()
        val notificationcard = layoutInflater.inflate(R.layout.notificationcard, null)
        notificationcard.notificationtext.text = text
        layout.addView(notificationcard)
        activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.GONE
    }

}