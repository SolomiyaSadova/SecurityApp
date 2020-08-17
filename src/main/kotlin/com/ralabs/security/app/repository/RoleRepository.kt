package com.ralabs.security.app.repository

import com.ralabs.security.app.models.Role
import com.ralabs.security.app.models.RoleName
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleDAO : JpaRepository<Role?, Long?> {
    fun findByRoleName(roleName: RoleName): Role
}