package uz.suhrob.darsjadvalitatuuf.utils

import uz.suhrob.darsjadvalitatuuf.models.Group
import uz.suhrob.darsjadvalitatuuf.models.Settings

interface DataLoadInterface {
    fun groupListLoaded(responseString: String?)
    fun scheduleLoaded(group: Group?, settings: Settings?, loadedFromInternet: Boolean)
    fun loadError()
}