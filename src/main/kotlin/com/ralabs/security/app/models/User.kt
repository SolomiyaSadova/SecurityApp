package com.ralabs.security.app.models


import com.ralabs.security.app.request.UserResponse
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.util.Optional.ofNullable
import javax.persistence.*;
import javax.validation.constraints.Email

@Entity
@Table(name = "users")
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long? = null,
        val firstName: String,
        val lastName: String,
        @Email
        val email: String,
        var password: String,
        var verified: Boolean = false,
        @CreatedDate
        val createdAt: LocalDateTime = LocalDateTime.now(),
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "users_roles",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "role_id")])
        var role: Set<Role> = emptySet()
) {
        fun toResponse(): UserResponse = UserResponse(ofNullable(id).orElse(0), firstName, lastName, email)
}
