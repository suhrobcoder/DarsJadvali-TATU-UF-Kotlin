package uz.suhrob.darsjadvalitatuuf.models

data class Group(
    val name: String = "",
    val lessons: List<Lesson> = listOf(),
)