package com.ralabs.security.app.models


import javax.persistence.*;


@Entity
@Table(name = "users")
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long?,
        var username: String,
        var password: String,
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "user_roles",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "role_id")])
        var role: Set<Role>

) {
        constructor(username: String, password: String) : this(null, username, password, emptySet())
}
