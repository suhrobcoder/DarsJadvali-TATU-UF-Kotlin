package uz.suhrob.darsjadvalitatuuf.models

data class Settings(val startTime: Int, val lessonDuration: Int, val breakTime: Int) {
    fun getStartTime(order: Int): Int {
        return startTime + (lessonDuration+breakTime)*(order-1)
    }
}