package com.example.legapaket

object UserSession {

    enum class Role { ADMIN, PUSAT }

    data class UserData(
        val username: String,
        val fullName: String,
        val email: String,
        val phone: String,
        val agentName: String,
        val address: String,
        val role: Role
    )

    private val users = mapOf(
        "admin" to UserData(
            username  = "admin",
            fullName  = "Iqbal Hidayatur Rahman",
            email     = "iqbalhidayatur@legapaket.com",
            phone     = "081324473082",
            agentName = "Agen LegaPaket Bandung",
            address   = "Jl. Cigondewah No. 123",
            role      = Role.ADMIN
        ),
        "pusat" to UserData(
            username  = "pusat",
            fullName  = "Admin Pusat",
            email     = "pusat@legapaket.com",
            phone     = "021000000001",
            agentName = "LegaPaket Pusat",
            address   = "Jl. Sudirman No. 1, Jakarta",
            role      = Role.PUSAT
        )
    )

    var currentUser: UserData? = null
        private set

    val isAdmin: Boolean get() = currentUser?.role == Role.ADMIN

    val isPusat: Boolean get() = currentUser?.role == Role.PUSAT

    fun login(username: String): Boolean {
        val user = users[username] ?: return false
        currentUser = user
        LocalStorage.saveSession(username)
        return true
    }

    fun restoreSession(): Boolean {
        val savedUsername = LocalStorage.loadSession() ?: return false
        val user = users[savedUsername] ?: return false
        currentUser = user
        return true
    }

    fun logout() {
        currentUser = null
        LocalStorage.clearSession()
    }
}