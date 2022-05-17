package com.example.ting.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity
@Parcelize
@Serializable
data class AccountDetail(
    @PrimaryKey
    @Embedded
    val account: Account = Account(),
    @Embedded
    val profile: Profile = Profile()
) : Parcelable {
    @Entity
    @Parcelize
    @Serializable
    data class Account(
        val id: Long = 0
    ) : Parcelable

    @Entity
    @Parcelize
    @Serializable
    data class Profile(
        val avatarUrl: String = "",
        val nickname: String = ""
    ) : Parcelable
}
