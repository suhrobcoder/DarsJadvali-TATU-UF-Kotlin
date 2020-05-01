package uz.suhrob.darsjadvalitatuuf.models

data class Settings(var startTime: Int = 0, var lessonDuration: Int = 0, var breakTime: Int = 0) {
    fun getStartTime(order: Int): Int {
        return startTime + (lessonDuration+breakTime)*(order-1)
    }
}