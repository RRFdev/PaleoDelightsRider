package com.rrdsolutions.paleodelightsrider.ui.pendingdelivery

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.transition.AutoTransition
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rrdsolutions.paleodelightsrider.QResult
import com.rrdsolutions.paleodelightsrider.R
import kotlinx.android.synthetic.main.fragment_pendingdelivery.*
import kotlinx.android.synthetic.main.menuitemstext.view.*

import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.fragment_currentdelivery.*
import kotlinx.android.synthetic.main.fragment_pendingdelivery.address
import kotlinx.android.synthetic.main.fragment_pendingdelivery.cardView
import kotlinx.android.synthetic.main.fragment_pendingdelivery.dotbutton
import kotlinx.android.synthetic.main.fragment_pendingdelivery.expandimage
import kotlinx.android.synthetic.main.fragment_pendingdelivery.hiddenlayout
import kotlinx.android.synthetic.main.fragment_pendingdelivery.leftbutton
import kotlinx.android.synthetic.main.fragment_pendingdelivery.mainlayout
import kotlinx.android.synthetic.main.fragment_pendingdelivery.menuitemsholder
import kotlinx.android.synthetic.main.fragment_pendingdelivery.notificationlayout
import kotlinx.android.synthetic.main.fragment_pendingdelivery.notificationtext
import kotlinx.android.synthetic.main.fragment_pendingdelivery.number
import kotlinx.android.synthetic.main.fragment_pendingdelivery.phonebtn
import kotlinx.android.synthetic.main.fragment_pendingdelivery.phonenumber
import kotlinx.android.synthetic.main.fragment_pendingdelivery.rightbutton

class PendingDeliveryFragment : Fragment(){
    private lateinit var vm: PendingDeliveryViewModel
    lateinit var coordinate: LatLng

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vm = ViewModelProvider(this).get(PendingDeliveryViewModel::class.java)
        activity?.findViewById<Toolbar>(R.id.toolbarmain)?.title = "Pending Delivery"
        return inflater.inflate(R.layout.fragment_pendingdelivery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.setUsernameValue(activity?.intent?.getStringExtra("username") as String)
        map.onCreate(savedInstanceState)

        vm.setIndexValue(vm.index.value!!)

        vm.queryRider(){callback->
            when (callback){
                QResult.DELIVERY_PRESENT->{
                    if (getView()!=null){
                        vm.index.observe(viewLifecycleOwner){
                            if (vm.list.size>0){
                                loadPendingDelivery(it)
                            }
                        }
                    }
                }
                QResult.NO_DELIVERY->loadNoDelivery("No deliveries at the moment")
                QResult.NO_CONNECTION->loadNoDelivery("ERROR: Unable to reach database. Please check your online connection")
            }
        }

        map.getMapAsync{googleMap->
            vm.coordinate.observe(viewLifecycleOwner){
                googleMap.addMarker(MarkerOptions().position(it).title("Customer Location")).showInfoWindow()
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 18f))
            }
        }


        leftbutton.setOnClickListener{
            vm.decreaseIndex()
        }

        rightbutton.setOnClickListener{
            vm.increaseIndex()
        }

        dotbutton.setOnClickListener{
            fun menuClicked(i:Int): MenuItem.OnMenuItemClickListener{
                val onclick = MenuItem.OnMenuItemClickListener {
                    when (it.title){
                        vm.list[i].number->{
                            vm.setIndexValue(i)
                        }
                    }
                    true
                }
                return onclick
            }
            val popupMenu = PopupMenu(requireContext(),dotbutton)

            for (i in 0 until vm.list.size){
                popupMenu.menu
                    .add(vm.list[i].number)
                    .setOnMenuItemClickListener(menuClicked(i))
            }
            Log.d("_pending", "dot button clicked")
            popupMenu.show()
        }

        pickupbtn.setOnClickListener{
            //activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.VISIBLE
            val ordernumber = vm.list[vm.index.value!!].number

                vm.updateDelivery(ordernumber){taskCompleted->
                    if (taskCompleted){
                        Toast.makeText(this.context as Activity, ordernumber + " marked for delivery", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this.context as Activity, "Delivery update failed. Please check your online connection and retry.", Toast.LENGTH_SHORT).show()
                    }
                    //activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.GONE
                }
        }

        mainlayout.visibility = View.GONE
        notificationlayout.visibility = View.GONE

    }

    override fun onPause(){
        super.onPause()
        map.onPause()
        //gets rid of Parcel: unable to marshal value errors
        vm.setListValue(ArrayList())
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    fun loadNoDelivery(text:String){

        if (mainlayout!=null&& notificationlayout!=null){
            notificationtext.text = text

            mainlayout.visibility = View.GONE
            notificationlayout.visibility = View.VISIBLE
            //activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.GONE
        }

    }

    fun loadPendingDelivery(i:Int){
        if (mainlayout!=null&& notificationlayout!=null) {
            mainlayout.visibility = View.VISIBLE
            notificationlayout.visibility = View.GONE

            val order = vm.list[i]

            number.text = order.number

            menuitemsholder.removeAllViews()
            for (u in 0 until order.itemlist.size){
                val menuitemstext = layoutInflater.inflate(R.layout.menuitemstext, null)
                menuitemstext.desc.text = order.itemlist[u]
                menuitemsholder.addView(menuitemstext)
            }

            address.text = order.address

            phonenumber.text = order.phonenumber

            phonebtn.setOnClickListener{
                callCustomerDialog(i)
            }
            cardView.setOnClickListener{
                if (hiddenlayout.visibility == View.GONE) {
                    TransitionManager.beginDelayedTransition(
                        cardView as ViewGroup,
                        AutoTransition()
                    )
                    hiddenlayout.visibility = View.VISIBLE
                    expandimage.animate().rotation(180f).start()
                } else {
                    TransitionManager.beginDelayedTransition(
                        cardView as ViewGroup,
                        Fade().setDuration(300)
                    )
                    hiddenlayout.visibility = View.GONE
                    expandimage.animate().rotation(0f).start()
                }
            }

            getCoordinate(i){locationFound->
                if (locationFound){
                    errorcard.visibility = View.GONE
                    Log.d("_pending", "callback requested")
                    Log.d("_pending", "2coordinate = " + coordinate)
                    activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE
                }
                else{
                    errorcard.visibility = View.VISIBLE
                    activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE
                }

            }

            mainlayout.visibility = View.VISIBLE
            notificationlayout.visibility = View.GONE

            //activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.GONE
        }
    }

    fun getCoordinate(i: Int, locationFound:(Boolean)->Unit){
        val address = vm.list[i].address
        Log.d("_pending", "address = " + address)
        val location = Geocoder(this.context as Activity)
            .getFromLocationName(address, 1)
        Log.d("_pending", "location.size = " + location.size)

        if (location.size == 0){
            locationFound(false)
        }
        else{
            coordinate = LatLng (location[0].latitude, location[0].longitude)
            vm.coordinate.value = coordinate

            Log.d("_pending", "coordinate = " + coordinate)
            locationFound(true)
        }

    }

    fun callCustomerDialog(i:Int){
        fun makePhoneCall(phonenumber:String){
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + phonenumber)
            startActivity(callIntent)
        }

        val phonenumber = vm.list[i].phonenumber
        MaterialDialog(this.context as Activity).show {
            title(text = "Call confirmation")
            message(text = "Call customer at $phonenumber directly?")

            positiveButton(text = "Proceed"){ dialog ->
                makePhoneCall(phonenumber)
            }
            negativeButton(text = "Cancel"){ dialog ->
                dismiss()
            }
        }

    }

}


