package com.example.ting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.ting.model.*
import com.example.ting.other.toast
import com.example.ting.repository.TingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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
    val dailyWord get() = _dailyWord.asStateFlow()
    val newSong by lazy { tingRepository.getNewSong() }
    private val _songList by lazy { MutableStateFlow(SongList()) }
    val songList get() = _songList.asStateFlow()
    val topList by lazy { tingRepository.getTopList() }
    val dailyList by lazy { tingRepository.getDailyList() }
    val categoryAll by lazy { tingRepository.getTypeList() }
    private val _categorySelected by lazy { MutableStateFlow(listOf<String>()) }
    val categorySelected get() = _categorySelected.asStateFlow()
    val highQualityPlaylist by lazy { tingRepository.getHighQualityPlaylist() }
    val playlistCatPager by lazy { mutableMapOf<String, Flow<PagingData<Playlists>>>() }
    private val _loginState by lazy { MutableStateFlow(LoginResponse()) }
    val loginState get() = _loginState.asStateFlow()
    private val _userData by lazy { MutableStateFlow(AccountDetail()) }
    val userData get() = _userData.asStateFlow()
    private val _isReady by lazy { MutableStateFlow(false) }
    val isReady get() = _isReady.asStateFlow()
    private val _isSend by lazy { MutableStateFlow(false) }
    val isSend get() = _isSend.asStateFlow()
    private val _userPlaylist by lazy { MutableStateFlow(UserPlaylist()) }
    val userPlaylist get() = _userPlaylist.asStateFlow()
    private val _likeList by lazy { MutableStateFlow(LikeList()) }
    val likeList get() = _likeList.asStateFlow()
    private val _musicDetail by lazy { MutableStateFlow(MusicDetail()) }
    val musicDetail get() = _musicDetail.asStateFlow()
    private val _lyric by lazy { MutableStateFlow(Lyric()) }
    val lyric get() = _lyric.asStateFlow()
    fun getTopPlaylist(category: String) =
        tingRepository.getTopPlaylist(category).cachedIn(viewModelScope)

    fun refreshDailyWord() {
        tingRepository.getDailyWord().onEach {
            _dailyWord.value = it
        }.launchIn(viewModelScope)
    }

    fun refreshSelectedCategory() {
        tingRepository.getHotPlaylistTags().onEach {
            _categorySelected.value = it
        }.launchIn(viewModelScope)
    }

    fun getSongList(id: Long) {
        tingRepository.getSongList(id).onEach {
            _songList.value = it
        }.launchIn(viewModelScope)
    }

    fun loginCellPhone(phone: String, password: String) {
        tingRepository.loginCellphone(phone, password).onEach {
            _loginState.value = it
        }.launchIn(viewModelScope)
    }

    fun loginCaptcha(phone: String, captcha: String) {
        tingRepository.loginCaptcha(phone, captcha).onEach {
            _loginState.value = it
        }.launchIn(viewModelScope)
    }

    fun sendCaptcha(phone: String) {
        tingRepository.sendCaptcha(phone).onEach {
            if (it["code"].toString() == "200") {
                "验证码发送成功".toast()
                _isSend.value = true
                delay(60000)
                _isSend.value = false
            }
        }.launchIn(viewModelScope)
    }

    fun refreshLibraryPage(id: Long) {
        tingRepository.getUserPlaylists(id).onEach {
            _userPlaylist.value = it
        }.launchIn(viewModelScope)
    }

    fun subscribe(id: Long, sub: Boolean) {
        tingRepository.subPlaylist(id, sub).onEach {
            if (it.code == 200) {
                getSongList(id)
            }
        }.launchIn(viewModelScope)
    }

    fun like(id: Long) {
        val uid = musicDetail.value.songs[0].id
        val like = likeList.value.ids.contains(uid)
        tingRepository.likeMusic(
            uid,
            !like
        ).onEach {
            if (it.code == 200) {
                loadLikeList(id)
            }
        }.launchIn(viewModelScope)
    }

    fun loadLikeList(id: Long) {
        if (id <= 0) return
        tingRepository.getLikeList(id).onEach {
            _likeList.value = it
        }.launchIn(viewModelScope)
    }

    fun loadMusicDetail(id: Long) {
        if (id == 0L) {
            _musicDetail.value = MusicDetail()
            _lyric.value = Lyric()
            return
        }
        tingRepository.getMusicDetail(id).onEach {
            _musicDetail.value = it
        }.launchIn(viewModelScope)
        tingRepository.getLyric(id).onEach {
            _lyric.value = it
        }.launchIn(viewModelScope)
    }

    fun init() {
        combine(tingRepository.refreshLogin(), tingRepository.getAccountDetail()) { a, b ->
            a to b
        }.onEach {
            if (it.first["code"].toString() == "301") {
                "未登录".toast()
            }
            _userData.value = it.second
        }.onCompletion {
            _isReady.value = true
        }.launchIn(viewModelScope)
    }

    init {
        refreshDailyWord()
        refreshSelectedCategory()
        init()
    }
}