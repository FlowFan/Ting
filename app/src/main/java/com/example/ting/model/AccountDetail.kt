package com.example.ting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class AccountDetail(
    val account: Account = Account(),
    val profile: Profile = Profile()
) : Parcelable {
    @Parcelize
    @Serializable
    data class Account(
        val id: Long = 0
    ) : Parcelable

    @Parcelize
    @Serializable
    data class Profile(
        val avatarUrl: String = "",
        val nickname: String = ""
    ) : Parcelable
}
