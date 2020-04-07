package uz.suhrob.darsjadvalitatuuf

import uz.suhrob.darsjadvalitatuuf.models.Group

interface DataLoadInterface {
    fun groupListLoaded(responseString: String?)
    fun scheduleLoaded(group: Group, loadedFromInternet: Boolean)
}