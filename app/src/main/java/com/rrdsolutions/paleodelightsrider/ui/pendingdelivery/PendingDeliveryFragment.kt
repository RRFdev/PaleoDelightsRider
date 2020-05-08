package com.rrdsolutions.paleodelightsrider.ui.pendingdelivery

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.rrdsolutions.paleodelightsrider.OrderModel
import com.rrdsolutions.paleodelightsrider.R
import com.rrdsolutions.paleodelightsrider.ui.currentdelivery.CurrentDeliveryFragment
import com.rrdsolutions.paleodelightsrider.ui.currentdelivery.CurrentDeliveryViewModel
import com.rrdsolutions.paleodelightsrider.ui.verifydelivery.VerifyDeliveryFragment
import com.rrdsolutions.paleodelightsrider.ui.verifydelivery.VerifyFragment
import kotlinx.android.synthetic.main.fragment_currentdelivery.*
import kotlinx.android.synthetic.main.fragment_currentdelivery.layout
import kotlinx.android.synthetic.main.fragment_pendingdelivery.*
import kotlinx.android.synthetic.main.fragment_pendingdelivery2.*
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
        activity?.findViewById<Toolbar>(R.id.toolbar)?.title = "Pending Delivery"
        if (activity?.getPreferences(0)?.getBoolean("back", false ) == true){
            val root = ViewHolder.view
            activity?.getPreferences(0)?.edit()?.putBoolean("back", false)?.apply()
            return root
        }
        else{
            val root = activity?.layoutInflater?.inflate(R.layout.fragment_pendingdelivery2, container, false)
            return root
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        testbutton.setOnClickListener{
            moveToVerifyDelivery(0)
        }


        if (isAdded && activity != null){
            vm.queryPendingDelivery{ callback->
                when (callback){
                    "Delivery Present"->loadPendingDelivery()
                    "No Delivery"->loadNoDelivery("No deliveries at the moment")
                    "No Connection"->loadNoDelivery("ERROR: Unable to reach database. Please check your online connection")
                }
                activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE
            }

        }

        activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()




    }

    fun loadPendingDelivery(){

        for (i in 0 until OrderModel.pendingorderlist.size){
            val ordercard = getActivity()?.layoutInflater?.inflate(R.layout.ordercard, null)
            ordercard?.number?.text = OrderModel.pendingorderlist[i].number
            ordercard?.time?.text = OrderModel.pendingorderlist[i].time

            ordercard?.setOnClickListener{
                moveToVerifyDelivery(i)
            }


            layout.addView(ordercard)
        }

    }

    fun loadNoDelivery(text:String){
        val notificationcard = getActivity()?.layoutInflater?.inflate(R.layout.notificationcard, null)
        notificationcard?.notificationtext?.text = text
        layout.addView(notificationcard)
    }

    fun moveToVerifyDelivery(i:Int){

        activity?.getPreferences(0)?.edit()?.putInt("index", i)?.apply()

//        val fm = fragmentManager
//        fm?.beginTransaction()
//
//            //?.add(PendingDeliveryFragment(),"")
//            //?.remove(VerifyDeliveryFragment())
////            ?.replace(R.id.nav_host_fragment, VerifyDeliveryFragment())
////            ?.remove(CurrentDeliveryFragment())
//            ?.remove(CurrentDeliveryFragment())
//            ?.remove(PendingDeliveryFragment())
//            ?.add(VerifyDeliveryFragment(),"")
//
//
//            //?.addToBackStack(null)
//            ?.commit()

        view?.let { Navigation.findNavController(it).navigate(R.id.nav_verifydelivery) }

    }

}

object ViewHolder{
    lateinit var view:View
}