package uz.suhrob.darsjadvalitatuuf.utils

import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import uz.suhrob.darsjadvalitatuuf.DataLoadInterface

class NetworkUtils {
    private val baseUrl = "http://suhrobbotcodes.000webhostapp.com/schedule/"

    fun getGroupList(dataLoadInterface: DataLoadInterface) {
        AsyncHttpClient().get(baseUrl + "group_list.php", object: TextHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Array<out Header>?,
                                   responseString: String?, throwable: Throwable?) {
                Log.d("api", "grouplist error")
            }

            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                dataLoadInterface.groupListLoaded(responseString)
            }
        })
    }

    fun getSchedule(groupName: String, dataLoadInterface: DataLoadInterface) {
        AsyncHttpClient().get(baseUrl + "get_schedule.php", RequestParams("group", groupName), object: TextHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, throwable: Throwable?) {
                Log.d("api", "getSchedule error")
            }

            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                val splittedResponse = responseString?.split("|||")
                val group = JSONUtils.getGroupFromJSON(splittedResponse?.get(0))
                val settings = JSONUtils.getSettingsFromJSON(splittedResponse?.get(1))
                if (group != null && settings != null) {
                    dataLoadInterface.scheduleLoaded(group, settings, true)
                } else {
                    Log.d("api", "group parse error")
                }
            }
        })
    }
}