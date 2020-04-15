package uz.suhrob.darsjadvalitatuuf.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import uz.suhrob.darsjadvalitatuuf.R
import uz.suhrob.darsjadvalitatuuf.models.Group

class ScheduleFragment : Fragment() {

    companion object {
        private var animate = true
        private const val ARG_SCHEDULES = "arg_schedules"
        fun newInstance(schedules: String) : ScheduleFragment {
            val fragment = ScheduleFragment()
            val arguments = Bundle()
            arguments.putString(ARG_SCHEDULES, schedules)
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val arguments = arguments
        val schedules = Gson().fromJson(arguments?.getString(ARG_SCHEDULES), Group::class.java).schedules
        val view = LayoutInflater.from(context)
                .inflate(R.layout.day_item, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.schedule_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        if (animate) {
            recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.schedule_layout_anim)
            animate = false
        }
        recyclerView.adapter = SchedulesAdapter(context!!, schedules)
        return view
    }
}