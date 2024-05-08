package io.github.vooft.kotlinsmile

import kotlinx.serialization.Serializable

@Serializable
data class Project(val name: String, val owner: User, val votes: Int)

@Serializable
data class User(val name: String)
