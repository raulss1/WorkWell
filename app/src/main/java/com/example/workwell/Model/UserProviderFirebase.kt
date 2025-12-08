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
        val snapshot = userTaskCollection
            .whereEqualTo("UserId", id)
            .get()
            .await()

        return snapshot.documents.map { doc ->

            val firestoreDate: java.util.Date? = doc.getDate("DateTask")

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
                id = doc.getString("UserId") ?: "",
                date = customDate
            )
        }
    }

    override suspend fun createUserTask(userId: String, task: String) {
        val newTaskData = hashMapOf(
            "Task" to task,
            "UserId" to userId,
            "DateTask" to Calendar.getInstance().time
        )

        userTaskCollection.add(newTaskData).await()
    }

    override fun editUser(id: String): User {
        TODO("Not yet implemented")
    }
}