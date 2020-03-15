package uz.suhrob.darsjadvalitatuuf

import uz.suhrob.darsjadvalitatuuf.models.Group

/**
 * Created by User on 11.03.2020.
 */
interface DataLoadInterface {
    fun groupListLoaded(responseString: String?)
    fun scheduleLoaded(group: Group, loadedFromInternet: Boolean)
}