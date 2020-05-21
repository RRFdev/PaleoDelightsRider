package com.rrdsolutions.paleodelightsrider

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.rrdsolutions.paleodelightsrider.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.testtext.*
import kotlinx.android.synthetic.main.testtext.view.*
import java.io.File

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var vm:MainViewModel

    var username = ""

    data class Logindetails(
        val username:String,
        val password:String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbarmain)
        setSupportActionBar(toolbar)
        loadingscreenmain.visibility = View.GONE
        Log.d("_TEST", "in main")
        FirebaseApp.initializeApp(this)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        //val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_currentdelivery, R.id.nav_pendingdelivery), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.getHeaderView(0).findViewById<TextView>(R.id.logouttext)
            .setOnClickListener{
            Log.d("_main", "clicked")
                logout()
        }
        val username = intent.getStringExtra("username") as String
        navView.getHeaderView(0).findViewById<TextView>(R.id.loggedintext)
            .text = "Logged in as $username"



        //navView.menu.findItem(R.id.nav_logout).isVisible = false

        val testtext = layoutInflater.inflate(R.layout.testtext, null)
        testtext.textView.text = "this is a test"


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
//        getPreferences(0).edit().putString("username", "").apply()
//        getPreferences(0).edit().putString("password", "").apply()
//
//        val username = getPreferences(0).getString("username", "").toString()
//        val password = getPreferences(0).getString("password", "").toString()
//
//        Log.d("_login", "after logout, username = $username, password = $password")

        val emptylogin = Logindetails("", "")
        val file = File(this.filesDir.path.toString() + "logindetails")
        file.writeText(Gson().toJson(emptylogin))



        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
//        Log.d("_main", "moving to LoginActivity")
//        Log.d("_main", "logout clicked")
//

    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//
//        val a = Intent(Intent.ACTION_MAIN)
//        a.addCategory(Intent.CATEGORY_HOME)
//        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        this.startActivity(a)
//    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_currentdelivery->{}
            R.id.nav_pendingdelivery->{}

        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

}




