package com.dicoding.projekakhirplatformkelompok5

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
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

        // Set Toolbar kustom sebagai ActionBar jika Anda menggunakannya
        setSupportActionBar(binding.toolbar)

        // Atur listener untuk BottomNavigationView
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

            // Jika ada fragment yang terpilih, ganti fragment di container
            if (selectedFragment != null) {
                loadFragment(selectedFragment)
                // Ganti judul di toolbar secara manual
                supportActionBar?.title = title
            }

            true // Listener harus mengembalikan boolean
        }

        // Tampilkan halaman default (HomeFragment) saat activity pertama kali dibuat
        if (savedInstanceState == null) {
            binding.navView.selectedItemId = R.id.navigation_home // Set item Home sebagai yang aktif
            loadFragment(HomeFragment())
            supportActionBar?.title = getString(R.string.title_home)
        }
    }

    /**
     * Fungsi helper untuk memuat (mengganti) fragment ke dalam container.
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment) // Ganti fragment di dalam FrameLayout
            .commit()
    }
}