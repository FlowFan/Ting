package com.example.ting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.ting.model.DailyWord
import com.example.ting.repository.TingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TingViewModel @Inject constructor(
    private val tingRepository: TingRepository
) : ViewModel() {
    val albumList = tingRepository.getRecommendData().cachedIn(viewModelScope).asLiveData()
    fun getTrackList(albumId: Int) =
        tingRepository.getDetailData(albumId).cachedIn(viewModelScope).asLiveData()

    val playList by lazy { tingRepository.getPlayList() }
    private val _dailyWord by lazy { MutableStateFlow(DailyWord()) }
    val dailyWord: StateFlow<DailyWord> get() = _dailyWord
    val newSong by lazy { tingRepository.getNewSong() }
    val topList by lazy { tingRepository.getTopList() }
    fun refreshDailyWord() {
        tingRepository.getDailyWord().onEach {
            _dailyWord.value = it
        }.launchIn(viewModelScope)
    }

    fun getSongList(id: Long) = tingRepository.getSongList(id)

    init {
        refreshDailyWord()
    }
}