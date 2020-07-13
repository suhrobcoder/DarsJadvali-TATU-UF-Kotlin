package uz.suhrob.darsjadvalitatuuf.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import uz.suhrob.darsjadvalitatuuf.models.Group
import uz.suhrob.darsjadvalitatuuf.models.Settings

class FirebaseHelper {
    companion object {
        fun getInstance() : FirebaseHelper {
            return FirebaseHelper()
        }
    }

    fun getGroups(dataLoadInterface: DataLoadInterface) {
        FirebaseDatabase.getInstance().getReference("groups").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                dataLoadInterface.loadError()
            }

            override fun onDataChange(p0: DataSnapshot) {
                Timber.d("${p0.value}")
                dataLoadInterface.groupListLoaded(p0.getValue(String::class.java))
            }
        })
    }

    fun getSchedule(groupName: String, dataLoadInterface: DataLoadInterface) {
        var group: Group? = null
        var settings: Settings? = null
        FirebaseDatabase.getInstance().getReference("schedules").child(groupName).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                dataLoadInterface.loadError()
            }

            override fun onDataChange(p0: DataSnapshot) {
                Timber.d("${p0.value}")
                group = p0.getValue(Group::class.java)
                if (settings != null) {
                    dataLoadInterface.scheduleLoaded(group, settings, true)
                }
            }
        })
        FirebaseDatabase.getInstance().getReference("settings").child(getSmene(groupName)).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                dataLoadInterface.loadError()
            }

            override fun onDataChange(p0: DataSnapshot) {
                Timber.d("${p0.value}")
                settings = p0.getValue(Settings::class.java)
                if (group != null) {
                    dataLoadInterface.scheduleLoaded(group, settings, true)
                }
            }
        })
    }

    private fun getSmene(group: String): String {
        val year = group.subSequence(group.length-2, group.length).toString()
        return if (year == "19" || year == "18") {
            "1"
        } else {
            "2"
        }
    }
}