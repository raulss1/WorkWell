package com.example.workwell.ViewModel

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp

class FirestoreUserFacade(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserFacade {

    override suspend fun usernameExists(username: String): Boolean =
        !db.collection("user")
            .whereEqualTo("UserName", username)
            .get()
            .await()
            .isEmpty

    override suspend fun emailExists(email: String): Boolean =
        !db.collection("user")
            .whereEqualTo("Email", email)
            .get()
            .await()
            .isEmpty

    override suspend fun saveUser(
        userId: String,
        name: String,
        username: String,
        email: String,
        birthDate: java.util.Date
    ) {
        val data = hashMapOf(
            "Name" to name,
            "UserName" to username,
            "Email" to email,
            "BirthDate" to Timestamp(birthDate),
            "UserId" to userId
        )

        db.collection("user").document(userId).set(data).await()
    }
}
