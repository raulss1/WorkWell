data class HabitSection(
    val title: String,
    val description: String
)

data class Habit(
    val id: String,
    val title: String,
    val subtitle: String?,
    val description: String,
    val sections: List<HabitSection>
)