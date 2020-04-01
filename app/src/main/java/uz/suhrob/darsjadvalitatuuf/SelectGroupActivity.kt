package uz.suhrob.darsjadvalitatuuf

import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_select_group.*
import uz.suhrob.darsjadvalitatuuf.api.ApiHelper
import uz.suhrob.darsjadvalitatuuf.models.Group

/**
 * Created by User on 11.03.2020.
 */
class SelectGroupActivity : AppCompatActivity(), DataLoadInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_group)

        supportActionBar?.title = "Guruhni kiritish"

        select_retry_btn.setOnClickListener {
            if (hasInternetConnection()) {
                ApiHelper().getGroupList(this)
                select_no_internet_layout.visibility = View.GONE
                select_progressbar.visibility = View.VISIBLE
            }
        }
        if (hasInternetConnection()) {
            ApiHelper().getGroupList(this)
        } else {
            select_no_internet_layout.visibility = View.VISIBLE
            select_progressbar.visibility = View.GONE
        }
    }

    private fun hasInternetConnection(): Boolean {
        var result = false
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        return result
    }

    override fun groupListLoaded(responseString: String?) {
        select_progressbar.visibility = View.GONE
        val groups = responseString?.split("\n")!!.toMutableList()
        if (groups[groups.size-1].isEmpty()) {
            groups.removeAt(groups.size-1)
        }
        group_list_view.adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, groups)
        group_list_view.setOnItemClickListener { _, _, position, _ ->
            val returnIntent = Intent()
            returnIntent.putExtra("result", groups[position])
            setResult(AppCompatActivity.RESULT_OK, returnIntent)
            finish()
        }
    }

    override fun scheduleLoaded(group: Group, loadedFromInternet: Boolean) {

    }
}
