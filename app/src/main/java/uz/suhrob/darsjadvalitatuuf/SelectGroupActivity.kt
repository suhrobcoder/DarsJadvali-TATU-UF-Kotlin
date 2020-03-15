package uz.suhrob.darsjadvalitatuuf

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
            } else {
                select_progressbar.visibility = View.GONE
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
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info != null && info.isConnectedOrConnecting
    }

    override fun groupListLoaded(responseString: String?) {
        select_progressbar.visibility = View.GONE
        val groups = responseString?.split("\n")
        groups?.filter { !it.isEmpty() }
        group_list_view.adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, groups)
        group_list_view.setOnItemClickListener { _, _, position, _ ->
            val returnIntent = Intent()
            returnIntent.putExtra("result", groups?.get(position))
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }

    override fun scheduleLoaded(group: Group, loadedFromInternet: Boolean) {

    }
}
