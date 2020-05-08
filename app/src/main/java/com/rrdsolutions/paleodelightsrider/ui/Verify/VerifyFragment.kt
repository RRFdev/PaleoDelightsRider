package com.rrdsolutions.paleodelightsrider.ui.verifydelivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.rrdsolutions.paleodelightsrider.R
import com.rrdsolutions.paleodelightsrider.ui.Verify.VerifyViewModel

class VerifyFragment : Fragment() {

    lateinit var vm: VerifyViewModel
    lateinit var coordinate: LatLng

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.findViewById<Toolbar>(R.id.toolbarmain)?.title = "Verify Delivery"
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