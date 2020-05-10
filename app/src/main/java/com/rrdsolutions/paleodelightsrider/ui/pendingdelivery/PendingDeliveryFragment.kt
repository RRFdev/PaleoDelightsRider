package com.rrdsolutions.paleodelightsrider.ui.pendingdelivery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.rrdsolutions.paleodelightsrider.OrderModel
import com.rrdsolutions.paleodelightsrider.R
import kotlinx.android.synthetic.main.fragment_pendingdelivery.*
import kotlinx.android.synthetic.main.notificationcard.view.*
import kotlinx.android.synthetic.main.ordercard.view.*



class PendingDeliveryFragment : Fragment() {
    private lateinit var vm: PendingDeliveryViewModel
    lateinit var root: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProvider(this).get(PendingDeliveryViewModel::class.java)
        activity?.findViewById<Toolbar>(R.id.toolbarmain)?.title = "Pending Delivery"

            val root = activity?.layoutInflater?.inflate(R.layout.fragment_pendingdelivery, container, false)
            return root



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isAdded && activity != null){
            vm.queryPendingDelivery{ callback->
                when (callback){
                    "Delivery Present"->loadPendingDelivery()
                    "No Delivery"->loadNoDelivery("No deliveries at the moment")
                    "No Connection"->loadNoDelivery("ERROR: Unable to reach database. Please check your online connection")
                }

            }

        }

        activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE
    }

    override fun onPause(){
        super.onPause()
        layout.removeAllViews()
    }


    override fun onResume() {
        super.onResume()




    }

    fun loadPendingDelivery(){

        val linearlayout = getActivity()?.layoutInflater?.inflate(R.layout.pendingdeliverylinearlayout, null)
        for (i in 0 until OrderModel.pendingorderlist.size){
            val ordercard = getActivity()?.layoutInflater?.inflate(R.layout.ordercard, null)
            ordercard?.number?.text = OrderModel.pendingorderlist[i].number
            ordercard?.time?.text = OrderModel.pendingorderlist[i].time

            ordercard?.setOnClickListener{
                moveToVerifyDelivery(i)
            }


            //layout.addView(ordercard)
            linearlayout?.findViewById<LinearLayout>(R.id.layout222)?.addView(ordercard)
        }
        layout.addView(linearlayout)

        activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE

    }

    fun loadNoDelivery(text:String){
        val notificationcard = getActivity()?.layoutInflater?.inflate(R.layout.notificationcard, null)
        notificationcard?.notificationtext?.text = text
        layout.addView(notificationcard)

        activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE

    }

    fun moveToVerifyDelivery(i:Int){

        activity?.getPreferences(0)?.edit()?.putInt("index", i)?.apply()

        view?.let { Navigation.findNavController(it).navigate(R.id.nav_verifydelivery) }

    }

}
