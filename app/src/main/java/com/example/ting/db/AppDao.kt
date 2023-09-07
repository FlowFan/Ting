package com.example.ting.db

import androidx.room.Dao
import androidx.room.Insert
import com.example.ting.model.AccountDetail
import com.example.ting.model.DailyWord
import com.example.ting.model.LoginResponse
import com.example.ting.model.PlayList

@Dao
fun interface AccountDetailDao {
    @Insert(entity = AccountDetail::class)
    suspend fun insertAccountDetail(vararg accountDetail: AccountDetail)
}

@Dao
fun interface DailyWordDao {
    @Insert(entity = DailyWord::class)
    suspend fun insertDailyWord(vararg dailyWord: DailyWord)
}

@Dao
fun interface LoginResponseDao {
    @Insert(entity = LoginResponse::class)
    suspend fun insertLoginResponse(vararg loginResponse: LoginResponse)
}

@Dao
fun interface PlayListDao {
    @Insert(entity = PlayList.Result::class)
    suspend fun insertPlayList(playList: List<PlayList.Result>)
}