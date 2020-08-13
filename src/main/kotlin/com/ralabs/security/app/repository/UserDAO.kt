package com.ralabs.security.app.repository

import com.ralabs.security.app.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserDAO : JpaRepository<User, Long>{
    fun findByUsername(username: String?): User
    fun existsByUsername(username: String?): Boolean?
}