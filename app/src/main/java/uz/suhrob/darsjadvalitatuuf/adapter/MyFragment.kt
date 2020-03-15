package uz.suhrob.darsjadvalitatuuf.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import uz.suhrob.darsjadvalitatuuf.R
import uz.suhrob.darsjadvalitatuuf.models.Group

/**
 * Created by User on 12.03.2020.
 */
class MyFragment : Fragment() {

    companion object {
        private val ARG_SCHEDULES = "arg_schedules"
        fun newInstance(schedules: String) : MyFragment {
            val fragment = MyFragment()
            val arguments = Bundle()
            arguments.putString(ARG_SCHEDULES, schedules)
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val arguments = arguments
        val schedules = Gson().fromJson(arguments.getString(ARG_SCHEDULES), Group::class.java).schedules
        val view = LayoutInflater.from(context)
                .inflate(R.layout.day_item, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MyAdapter(context, schedules)
        return view
    }
}