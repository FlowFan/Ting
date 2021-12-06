package com.example.ximalaya.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.ximalaya.repository.RecommendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    recommendRepository: RecommendRepository
) : ViewModel() {
    val albumList = recommendRepository.getRecommendData().cachedIn(viewModelScope).asLiveData()
}