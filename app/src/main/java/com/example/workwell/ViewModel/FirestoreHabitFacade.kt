package com.example.workwell.ViewModel

import Habit
import HabitSection
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await



class FirestoreHabitFacade() : HabitsFacade {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override suspend fun getHabits(): List<Habit> {
        return try {
            val habits = db.collection("habits").get().await()

            habits.documents.map { doc ->

                val infoSectionData = doc.get("infoSection") as? Map<String, Any> ?: emptyMap()

                val sections = infoSectionData.values.mapNotNull { rawSection ->
                    val sectionMap = rawSection as? Map<String, Any>

                    val title = sectionMap?.get("title") as? String
                    val description = sectionMap?.get("description") as? String

                    if (title != null && description != null) {
                        HabitSection(
                            title = title,
                            description = description
                        )
                    } else {
                        android.util.Log.e("HabitFacade", "Error mapeando sección en ${doc.id}")
                        null
                    }
                }

                Habit(
                    id = doc.id,
                    title = doc.getString("title") ?: "Sin Título",
                    subtitle = doc.getString("subtitle"),
                    description = doc.getString("description") ?: "Sin descripción",
                    sections = sections,
                    image = doc.getString("image")
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("HabitFacade", "Error al obtener hábitos de Firebase: ${e.message}", e)
            throw e
        }
    }
}