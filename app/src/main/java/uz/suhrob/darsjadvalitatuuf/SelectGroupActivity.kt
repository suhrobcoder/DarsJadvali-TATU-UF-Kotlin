package uz.suhrob.darsjadvalitatuuf

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_select_group.*
import uz.suhrob.darsjadvalitatuuf.adapter.ExpandableListViewAdapter
import uz.suhrob.darsjadvalitatuuf.models.Group
import uz.suhrob.darsjadvalitatuuf.models.Settings
import uz.suhrob.darsjadvalitatuuf.storage.SharedPreferencesHelper
import uz.suhrob.darsjadvalitatuuf.utils.FirebaseHelper
import java.util.*
import kotlin.collections.HashMap

class SelectGroupActivity : AppCompatActivity(), DataLoadInterface {
    private var darkThemeEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        if (SharedPreferencesHelper(applicationContext).darkThemeEnabled()) {
            setTheme(R.style.DarkTheme)
            darkThemeEnabled = true
        } else {
            setTheme((R.style.AppTheme))
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_group)

        supportActionBar?.title = applicationContext.resources.getString(R.string.enter_group)

        select_retry_btn.setOnClickListener {
            if (hasInternetConnection()) {
                FirebaseHelper.getInstance().getGroups(this)
                select_no_internet_layout.visibility = View.GONE
                select_progressbar.visibility = View.VISIBLE
            }
        }
        if (hasInternetConnection()) {
            FirebaseHelper.getInstance().getGroups(this)
        } else {
            select_no_internet_layout.visibility = View.VISIBLE
            select_progressbar.visibility = View.GONE
        }
    }

    private fun hasInternetConnection(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = cm.activeNetwork ?: return false
            val actNw = cm.getNetworkCapabilities(networkCapabilities) ?: return false
            when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val netInfo = cm.activeNetworkInfo
            !(netInfo != null && netInfo.isConnected)
        }
    }

    override fun groupListLoaded(responseString: String?) {
        select_progressbar.visibility = View.GONE
        val groups = responseString?.split(" ")!!.toMutableList()
        if (groups[groups.size-1].isEmpty()) {
            groups.removeAt(groups.size-1)
        }
        val childTitles = HashMap<String, List<String>>()
        var thisYear = Calendar.getInstance().get(Calendar.YEAR) % 100
        if (Calendar.getInstance().get(Calendar.MONTH) < 8) {
            thisYear--
        }
        for (group in groups) {
            val groupYear = getGroupYear(group)
            val header = "${thisYear - groupYear + 1}-kurs"
            if (childTitles.containsKey(header)) {
                val children = childTitles[header]?.toMutableList()
                children?.add(group)
                childTitles[header] = children ?: emptyList()
            } else {
                childTitles[header] = listOf(group)
            }
        }
        val headerTitles = childTitles.keys.toMutableList()
        headerTitles.sort()
        group_list_view.setAdapter(ExpandableListViewAdapter(applicationContext, darkThemeEnabled, headerTitles, childTitles))
        group_list_view.setOnChildClickListener { _, _, headerPosition, childPosition, _ ->
            val returnIntent = Intent()
            returnIntent.putExtra("result", childTitles[headerTitles[headerPosition]]?.get(childPosition))
            setResult(RESULT_OK, returnIntent)
            finish()
            true
        }
    }

    override fun scheduleLoaded(group: Group?, settings: Settings?, loadedFromInternet: Boolean) {

    }

    private fun getGroupYear(groupName: String): Int {
        val year = groupName.subSequence(groupName.length-2, groupName.length)
        return Integer.parseInt(year.toString())
    }
}
