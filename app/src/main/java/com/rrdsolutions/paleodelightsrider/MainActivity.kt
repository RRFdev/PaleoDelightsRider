package com.rrdsolutions.paleodelightsrider

import android.app.FragmentTransaction
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.FirebaseApp
import com.rrdsolutions.paleodelightsrider.ui.currentdelivery.CurrentDeliveryFragment
import com.rrdsolutions.paleodelightsrider.ui.log.LogFragment
import com.rrdsolutions.paleodelightsrider.ui.login.LoginActivity
import com.rrdsolutions.paleodelightsrider.ui.pendingdelivery.PendingDeliveryFragment

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var vm:MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        loadingscreenmain.visibility = View.GONE
        Log.d("_TEST", "in main")
        FirebaseApp.initializeApp(this)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_currentdelivery, R.id.nav_pendingdelivery), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.getHeaderView(0).findViewById<TextView>(R.id.logouttext)
            .setOnClickListener{
            Log.d("_main", "clicked")
                logout()
        }

        //navView.menu.findItem(R.id.nav_logout).isVisible = false




    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun logout(){
        getPreferences(0).edit().putString("username", "").apply()
        getPreferences(0).edit().putString("password", "").apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        Log.d("_main", "moving to LoginActivity")
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(a)
    }
}



