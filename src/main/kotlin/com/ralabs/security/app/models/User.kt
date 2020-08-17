package com.ralabs.security.app.models


import com.ralabs.security.app.request.UserResponse
import java.util.Optional.ofNullable
import javax.persistence.*;
import javax.validation.constraints.Email


@Entity
@Table(name = "users")
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long? = null,
        @Email
        val email: String,
        val username: String,
        val password: String,
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "user_roles",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "role_id")])
        val role: Set<Role> = emptySet()
) {
        fun toResponse(): UserResponse = UserResponse(ofNullable(id).orElse(0), email, username)
}
