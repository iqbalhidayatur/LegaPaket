package com.example.legapaket

object UserSession {

    data class UserData(
        val username: String,
        val fullName: String,
        val email: String,
        val phone: String,
        val agentName: String,
        val address: String
    )

    private val users = mapOf(
        "admin" to UserData(
            username = "admin",
            fullName = "Iqbal Hidayatur Rahman",
            email = "iqbalhidayatur@legapaket.com",
            phone = "081324473082",
            agentName = "Agen LegaPaket Bandung",
            address = "Jl. Cigondewah No. 123"
        )
    )

    var currentUser: UserData? = null
        private set

    fun login(username: String): Boolean {
        val user = users[username] ?: return false
        currentUser = user
        return true
    }

    fun logout() {
        currentUser = null
    }

}