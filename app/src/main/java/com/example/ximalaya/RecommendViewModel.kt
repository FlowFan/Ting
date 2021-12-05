package com.example.ximalaya

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn

class RecommendViewModel : ViewModel() {
    private val recommendRepository = RecommendRepository()
    val albumList = recommendRepository.getRecommendData().cachedIn(viewModelScope)
}