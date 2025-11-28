package com.example.workwell.Model

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class UserProviderFirebase : UserProvider {

    val db = Firebase.firestore
    val usersCollection = db.collection("user")

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

    override fun createUser(): User {
        TODO("Not yet implemented")
    }

    override fun editUser(id: String): User {
        TODO("Not yet implemented")
    }
}