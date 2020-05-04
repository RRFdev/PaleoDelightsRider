package com.rrdsolutions.paleodelightsrider.ui.log

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.rrdsolutions.paleodelightsrider.R

class logFragment : Fragment() {
    private lateinit var vm: logViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.findViewById<ConstraintLayout>(R.id.drawer)?.visibility = View.GONE
        activity?.findViewById<ConstraintLayout>(R.id.loadingscreenmain)?.visibility = View.GONE
        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(this).get(logViewModel::class.java)
        //code body here



    }


}