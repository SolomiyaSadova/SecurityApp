package com.ralabs.security.app.models

import javax.persistence.*

@Entity
@Table(name = "roles")
data class Role (
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,
        @Enumerated(EnumType.STRING)
        val roleName: RoleName
)

enum class RoleName {
        ROLE_USER,
        ROLE_ADMIN
}
