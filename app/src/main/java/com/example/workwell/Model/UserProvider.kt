package com.example.workwell.Model

interface UserProvider {
    fun getUser(): User
    fun createUser(): User
    fun editUser(): User
}