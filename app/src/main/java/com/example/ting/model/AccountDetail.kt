package com.example.ting.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountDetail(
    val account: Account = Account(),
    val profile: Profile = Profile()
) {
    @Serializable
    data class Account(
        val id: Long = 0
    )

    @Serializable
    data class Profile(
        val avatarUrl: String = "",
        val nickname: String = ""
    )
}