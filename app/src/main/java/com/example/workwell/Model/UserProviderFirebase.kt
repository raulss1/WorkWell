package com.example.workwell.Model

import android.app.Notification
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

            val startFirestoreDate: java.util.Date? = doc.getDate("StartDateTask")
            val endFiresroteDate: java.util.Date? = doc.getDate("EndDateTask")

            val customDate = date(startFirestoreDate)
            val customDate2 = date(endFiresroteDate)

            Task(
                name = doc.getString("Task") ?: "",
                id = doc.getString("UserId") ?: "",
                startDate = customDate,
                endDate = customDate2,
                type = doc.getString("Type") ?: "",
                priority = doc.getString("Priority") ?: "",
                eatNotificationTime = doc.getString("EatNotificationTime") ?: "",
                standNotificationTime = doc.getString("StandNotificationTime") ?: "",
                strechNotificationTime = doc.getString("StrechNotificationTime") ?: ""
            )
        }
    }

    private fun date(firestoreDate: java.util.Date?): Date {
        val customDate = if (firestoreDate != null) {
            val cal = Calendar.getInstance()
            cal.time = firestoreDate

            Date(
                dia = cal.get(Calendar.DAY_OF_MONTH),
                mes = cal.get(Calendar.MONTH) + 1,
                año = cal.get(Calendar.YEAR),
                hora = cal.get(Calendar.HOUR_OF_DAY),
                minuto = cal.get(Calendar.MINUTE)
            )
        } else {
            // Valor por defecto si viene nulo
            Date(1, 1, 2000, 0, 0)
        }
        return customDate
    }

    override suspend fun createUserTask(task: String, userId: String, startDate: java.util.Date, endDate: java.util.Date, type: String, priority: String, eatNotificationTime: String, standNotificationTime: String, strechNotificationTime: String) {
        val newTaskData = hashMapOf(
            "Task" to task,
            "UserId" to userId,
            "StartDateTask" to startDate,
            "EndDateTask" to endDate,
            "Type" to type,
            "Priority" to priority,
            "EatNotificationTime" to eatNotificationTime,
            "StandNotificationTime" to standNotificationTime,
            "StrechNotificationTime" to strechNotificationTime
        )

        userTaskCollection.add(newTaskData).await()
    }

    override fun editUser(id: String): User {
        TODO("Not yet implemented")
    }
}