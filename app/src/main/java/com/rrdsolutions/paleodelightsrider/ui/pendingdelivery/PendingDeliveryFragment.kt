package com.rrdsolutions.paleodelightsrider.ui.pendingdelivery

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.rrdsolutions.paleodelightsrider.OrderModel
import com.rrdsolutions.paleodelightsrider.R
import com.rrdsolutions.paleodelightsrider.ui.currentdelivery.CurrentDeliveryViewModel
import kotlinx.android.synthetic.main.fragment_currentdelivery.*
import kotlinx.android.synthetic.main.fragment_currentdelivery.layout
import kotlinx.android.synthetic.main.fragment_pendingdelivery.*
import kotlinx.android.synthetic.main.notificationcard.view.*
import kotlinx.android.synthetic.main.ordercard.view.*


class PendingDeliveryFragment : Fragment() {
    private lateinit var vm: PendingDeliveryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<Toolbar>(R.id.toolbar)?.title = "Pending Delivery"
        return inflater.inflate(R.layout.fragment_pendingdelivery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(this).get(PendingDeliveryViewModel::class.java)
        //code body here









        //activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()

        vm.queryPendingDelivery{ callback->
            when (callback){
                "Delivery Present"->loadPendingDelivery()
                "No Delivery"->loadNoDelivery("No deliveries at the moment")
                "No Connection"->loadNoDelivery("ERROR: Unable to reach database. Please check your online connection")
            }
            activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE
        }

    }

    fun loadPendingDelivery(){

        val ordercard = layoutInflater.inflate(R.layout.ordercard, null)

        for (i in 0 until OrderModel.pendingorderlist.size){
            ordercard.number.text = OrderModel.pendingorderlist[i].number
            ordercard.time.text = OrderModel.pendingorderlist[i].time

            ordercard.setOnClickListener{
                moveToVerifyAddress(OrderModel.pendingorderlist[i])
            }
            layout.addView(ordercard)
        }

    }

    fun loadNoDelivery(text:String){
        val notificationcard = layoutInflater.inflate(R.layout.notificationcard, null)
        notificationcard.notificationtext.text = text
        layout.addView(notificationcard)
    }

    fun moveToVerifyAddress(order: OrderModel.Order){

    }

}