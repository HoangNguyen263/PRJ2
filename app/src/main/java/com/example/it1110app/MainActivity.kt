package com.example.it1110app

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.it1110app.Activities.BookmarkActivity
import com.example.it1110app.Fragments.AccountFragment
import com.example.it1110app.Fragments.BaseConversionFragment
import com.example.it1110app.Fragments.CategoryFragment
import com.example.it1110app.Fragments.IEEE754Fragment
import com.example.it1110app.Fragments.LeaderboardFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

//handling navigation events and fragment transactions.
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var main_frame : FrameLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var drawerProfileName : TextView
    private lateinit var drawerProfileText: TextView
    private val OnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navi_home -> {
                setFragment(CategoryFragment())
                true
            }
            R.id.navi_leaderboard -> {
                setFragment(LeaderboardFragment())
                true
            }
            R.id.navi_account -> {
                setFragment(AccountFragment())
                true
            }
            else -> false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Categories"


        bottomNavigationView = findViewById(R.id.bottom_nav_bar)
        main_frame = findViewById(R.id.main_frame)

        bottomNavigationView.setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener)

        var drawer : DrawerLayout = findViewById(R.id.drawer_layout)
        var toggle : ActionBarDrawerToggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        var navigationView : NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        drawerProfileName = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_name)
        drawerProfileText = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_text_img)

        drawerProfileName.text = DbQuery.myProfile.name
        drawerProfileText.text = DbQuery.myProfile.name.toUpperCase().substring(0,1) //first letter of name

        setFragment(CategoryFragment())

    }

    override fun onBackPressed() {
        var drawer : DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_frame, fragment).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                setFragment(CategoryFragment())
                return true
            }
            R.id.nav_leaderboard -> {
                setFragment(LeaderboardFragment())
                return true
            }
            R.id.nav_account -> {
                setFragment(AccountFragment())
                return true
            }
            R.id.nav_bookmark -> {
                var intent: Intent = Intent(this, BookmarkActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.nav_ieee754 -> {
                setFragment(IEEE754Fragment())
                return true
            }
            R.id.nav_base_conversion -> {
                setFragment(BaseConversionFragment())
                return true
            }
            else -> return false
        }
    }

}