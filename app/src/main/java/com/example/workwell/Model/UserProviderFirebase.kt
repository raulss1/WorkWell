package com.example.workwell.Model

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class UserProviderFirebase : UserProvider {

    val db = Firebase.firestore
    val usersCollection = db.collection("user")
    val userTaskCollection = db.collection("UserTask")

    override suspend fun getUser(id: String): User {

        val document = usersCollection.document(id).get().await()

        if (document.exists()) {
            return User(
                document.getString("Name") ?: "N/A",
                document.getString("UserName") ?: "N/A",
                document.getString("Birthday") ?: "N/A",
                document.getString("Email") ?: "N/A"
            )
        } else {
            throw NoSuchElementException("Usuario con ID $id no encontrado en Firestore")
        }
    }

    override suspend fun getUserTask(id: String): List<Task> {
        // 1. CORRECCIÓN LÓGICA: Quitamos las comillas a "id" para usar la variable real
        val snapshot = userTaskCollection
            .whereEqualTo("userId", id)
            .get()
            .await()

        return snapshot.documents.map { doc ->

            // 2. CONVERSIÓN DE FECHA
            // Obtenemos la fecha nativa de Java desde Firestore
            val firestoreDate: java.util.Date? = doc.getDate("DateTask")

            // Preparamos tu clase personalizada
            val customDate = if (firestoreDate != null) {
                val cal = Calendar.getInstance()
                cal.time = firestoreDate

                Date(
                    dia = cal.get(Calendar.DAY_OF_MONTH),
                    mes = cal.get(Calendar.MONTH) + 1,
                    año = cal.get(Calendar.YEAR)
                )
            } else {
                // Valor por defecto si viene nulo
                Date(1, 1, 2000)
            }

            Task(
                name = doc.getString("Task") ?: "",
                // Asegúrate que el campo UserId es correcto, a veces es solo doc.id
                id = doc.getString("UserId") ?: "",
                date = customDate
            )
        }
    }

    override suspend fun createUserTask(userId: String, task: String) {
        val newTaskData = hashMapOf(
            "Task" to task,
            "userId" to userId,
            "Date" to Calendar.getInstance().time
        )

        userTaskCollection.add(newTaskData).await()
    }

    override fun editUser(id: String): User {
        TODO("Not yet implemented")
    }
}