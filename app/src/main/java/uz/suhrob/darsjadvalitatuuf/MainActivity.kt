package uz.suhrob.darsjadvalitatuuf

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import uz.suhrob.darsjadvalitatuuf.storage.SharedPreferencesHelper

class MainActivity : AppCompatActivity(), RestartActivity {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private lateinit var homeFragment : HomeFragment
    private lateinit var settingsFragment : SettingsFragment
    private lateinit var activeFragment : Fragment
    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferencesHelper = SharedPreferencesHelper(applicationContext)
        if (sharedPreferencesHelper.darkThemeEnabled()) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme((R.style.AppTheme))
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.elevation = 0F
        val mainTitle = "${resources.getString(R.string.app_name)} ${sharedPreferencesHelper.getGroup()}"
        supportActionBar?.title = mainTitle

        homeFragment = HomeFragment(applicationContext)
        settingsFragment = SettingsFragment(applicationContext, this)
        activeFragment = homeFragment
        fragmentManager.beginTransaction().add(R.id.main_frame, settingsFragment, "2").hide(settingsFragment).commit()
        fragmentManager.beginTransaction().add(R.id.main_frame, homeFragment, "1").commit()
        if (sharedPreferencesHelper.isThemeChanged()) {
            setFragment(settingsFragment)
            bottom_navbar.selectedItemId = R.id.navbar_settings
            supportActionBar?.title = applicationContext.resources.getString(R.string.settings)
        }
        bottom_navbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navbar_home -> {
                    setFragment(homeFragment)
                    supportActionBar?.title = mainTitle
                }
                R.id.navbar_settings -> {
                    setFragment(settingsFragment)
                    supportActionBar?.title = resources.getString(R.string.settings)
                }
            }
            true
        }
    }

    private fun setFragment(fragment: Fragment) {
        if (fragment != activeFragment) {
            fragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit()
            activeFragment = fragment
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.change_group_menu -> startActivityForResult(Intent(applicationContext, SelectGroupActivity::class.java), 1)
        }
        return true
    }

    override fun restartActivity() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            data?.getStringExtra("result")?.let {
                sharedPreferencesHelper.setGroup(it)
                sharedPreferencesHelper.setScheduleLoaded(false)
            }
            this.restartActivity()
        }
    }
}

interface RestartActivity {
    fun restartActivity()
}