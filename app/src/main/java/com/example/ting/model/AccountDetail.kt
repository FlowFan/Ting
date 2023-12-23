package com.example.ting.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class AccountDetail(
    @PrimaryKey
    @Embedded
    val account: Account = Account(),
    @Embedded
    val profile: Profile = Profile()
) {
    @Entity
    @Serializable
    data class Account(
        val id: Long = 0
    )

    @Entity
    @Serializable
    data class Profile(
        val avatarUrl: String = "",
        val nickname: String = ""
    )
}