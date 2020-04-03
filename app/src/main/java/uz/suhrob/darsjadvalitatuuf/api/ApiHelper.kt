package uz.suhrob.darsjadvalitatuuf.api

import android.util.Log
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import uz.suhrob.darsjadvalitatuuf.DataLoadInterface
import uz.suhrob.darsjadvalitatuuf.models.Group

/**
 * Created by User on 11.03.2020.
 */
class ApiHelper {

    private val baseUrl = "http://suhrobbotcodes.000webhostapp.com/schedule/"

    fun getGroupList(dataLoadInterface: DataLoadInterface) {
        val httpClient = AsyncHttpClient()
        httpClient.get(baseUrl + "group_list.php", object: TextHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, throwable: Throwable?) {
                Log.d("api", "grouplist error")
            }

            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                dataLoadInterface.groupListLoaded(responseString)
            }
        })
    }

    fun getSchedule(group: String, dataLoadInterface: DataLoadInterface) {
        val httpClient = AsyncHttpClient()
        val requestParams = RequestParams()
        requestParams.add("group", group)
        httpClient.get(baseUrl + "get_schedule.php", requestParams, object: TextHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, throwable: Throwable?) {
                Log.d("api", "getSchedule error")
            }

            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                dataLoadInterface.scheduleLoaded(Gson().fromJson(responseString, Group::class.java), true)
            }
        })
    }

}