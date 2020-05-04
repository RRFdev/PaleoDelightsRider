package com.rrdsolutions.paleodelightsrider.ui.currentdelivery

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.rrdsolutions.paleodelightsrider.MainActivity
import com.rrdsolutions.paleodelightsrider.MainViewModel
import com.rrdsolutions.paleodelightsrider.R
import kotlinx.android.synthetic.main.fragment_currentdelivery.*

class CurrentDeliveryFragment : Fragment() {
    private lateinit var vm: CurrentDeliveryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_currentdelivery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(this).get(CurrentDeliveryViewModel::class.java)

        //run firebase Query
        //if query returns no else, load up notificationcard.
        //else load up fragment_currentdelivery_layout

        vm.queryFirebase(){ currentDeliveryExists->
            if (currentDeliveryExists){
                loadCurrentDelivery()
            }
            else loadNoDelivery()
        }
    }

    fun loadCurrentDelivery(){}

    fun loadNoDelivery(){}

}