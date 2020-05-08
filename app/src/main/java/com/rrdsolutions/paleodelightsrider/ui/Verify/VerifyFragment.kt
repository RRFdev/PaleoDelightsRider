package com.rrdsolutions.paleodelightsrider.ui.verifydelivery

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.AutoTransition
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rrdsolutions.paleodelightsrider.R
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.rrdsolutions.paleodelightsrider.OrderModel
import com.rrdsolutions.paleodelightsrider.ui.Verify.VerifyViewModel
import kotlinx.android.synthetic.main.fragment_verifydelivery.*
import kotlinx.android.synthetic.main.menuitemstext.view.*

class VerifyFragment : Fragment() {

    lateinit var vm: VerifyViewModel
    lateinit var coordinate: LatLng

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.findViewById<Toolbar>(R.id.toolbar)?.title = "Verify Delivery"
        return inflater.inflate(R.layout.fragment_verify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(this).get(VerifyViewModel::class.java)
        activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.VISIBLE
        vm.index = activity?.getPreferences(0)?.getInt("index", 0)!!

    }

    override fun onResume() {
        super.onResume()

        activity?.findViewById<ConstraintLayout>(R.id.loadingscreen)?.visibility = View.GONE
    }




}