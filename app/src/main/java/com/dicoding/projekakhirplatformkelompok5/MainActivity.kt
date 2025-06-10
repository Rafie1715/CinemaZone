package com.dicoding.projekakhirplatformkelompok5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dicoding.projekakhirplatformkelompok5.databinding.ActivityMainBinding
import com.dicoding.projekakhirplatformkelompok5.ui.home.HomeFragment
import com.dicoding.projekakhirplatformkelompok5.ui.orderticket.OrderTicketFragment
import com.dicoding.projekakhirplatformkelompok5.ui.profile.ProfileFragment
import com.dicoding.projekakhirplatformkelompok5.ui.wishlist.WishlistFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.navView.setOnItemSelectedListener { menuItem ->
            var selectedFragment: Fragment? = null
            var title = getString(R.string.app_name)

            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    selectedFragment = HomeFragment()
                    title = getString(R.string.title_home)
                }
                R.id.navigation_order_ticket -> {
                    selectedFragment = OrderTicketFragment()
                    title = getString(R.string.title_order_ticket)
                }
                R.id.navigation_wishlist -> {
                    selectedFragment = WishlistFragment()
                    title = getString(R.string.title_wishlist)
                }
                R.id.navigation_profile -> {
                    selectedFragment = ProfileFragment()
                    title = getString(R.string.title_profile)
                }
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment)
                supportActionBar?.title = title
            }

            true
        }

        if (savedInstanceState == null) {
            binding.navView.selectedItemId = R.id.navigation_home
            loadFragment(HomeFragment())
            supportActionBar?.title = getString(R.string.title_home)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}