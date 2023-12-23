package com.example.ting.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.ting.model.*
import com.example.ting.model.Detail.Track
import com.example.ting.other.toast
import com.example.ting.repository.TingRepository
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class TingViewModel @Inject constructor(
    private val tingRepository: TingRepository,
    private val mediaControllerListenableFuture: ListenableFuture<MediaController>
) : ViewModel() {
    private val _detailId: MutableStateFlow<Long> = MutableStateFlow(0)
    private val _unit: MutableSharedFlow<Unit> = MutableSharedFlow()
    private val _account: MutableSharedFlow<Long> = MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val _phone: MutableStateFlow<String> = MutableStateFlow("")
    private val _play: MutableSharedFlow<List<MediaItem>> = MutableSharedFlow()

    val albumList: LiveData<PagingData<Album>> by lazy {
        tingRepository.getRecommendData().cachedIn(viewModelScope)
    }

    val trackList: Flow<PagingData<Track>> = _detailId.flatMapLatest {
        tingRepository.getDetailData(it)
    }.cachedIn(viewModelScope)

    val playList: LiveData<PlayList> by lazy { tingRepository.getPlayList() }

    val dailyWord: StateFlow<DailyWord> by lazy {
        tingRepository.getDailyWord().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = DailyWord()
        )
    }

    val newSong: StateFlow<NewSong> by lazy {
        tingRepository.getNewSong().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = NewSong()
        )
    }

    val topList: StateFlow<TopList> by lazy {
        tingRepository.getTopList().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = TopList()
        )
    }

    val dailyList: StateFlow<DailyList> by lazy {
        tingRepository.getDailyList().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = DailyList()
        )
    }

    val songList: StateFlow<SongList> = _detailId.flatMapLatest {
        tingRepository.getSongList(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = SongList()
    )

    val userData: StateFlow<AccountDetail> = _unit.onStart {
        emit(Unit)
    }.flatMapLatest {
        tingRepository.getAccountDetail()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AccountDetail()
    )

    val userPlaylist: StateFlow<UserPlaylist> = _account.flatMapLatest {
        tingRepository.getUserPlaylists(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = UserPlaylist()
    )

    private val _loginState: MutableStateFlow<Int> by lazy { MutableStateFlow(0) }
    val loginState: StateFlow<Int> get() = _loginState.asStateFlow()

    val isSend: StateFlow<Boolean> = _phone.flatMapLatest {
        tingRepository.sendCaptcha(it)
    }.flatMapLatest {
        flow {
            if (it["code"].toString() == "200") {
                "验证码发送成功".toast()
                emit(true)
                delay(60.seconds)
                emit(false)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = false
    )

    var isReady = false
        private set

    private val mediaController: StateFlow<MediaController?> = _play.flatMapLatest {
        flow {
            with(mediaControllerListenableFuture.await()) {
                emit(this)
                stop()
                clearMediaItems()
                addMediaItems(it)
                prepare()
                play()
            }
        }.flowOn(Dispatchers.Main)
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = null
    )

    init {
        tingRepository.refreshLogin().zip(userData) { jsonObject, _ ->
            if (jsonObject["code"].toString() == "301") {
                "未登录".toast()
            }
        }.onCompletion {
            isReady = true
        }.launchIn(viewModelScope)
        mediaController.launchIn(viewModelScope)
    }

    fun setDetailId(detailId: Long) = _detailId.update {
        detailId
    }

    fun refreshDailyWord() = viewModelScope.launch {
        tingRepository.refreshDailyWord()
    }

    fun refreshUserData() = viewModelScope.launch {
        _unit.emit(Unit)
    }

    fun refreshUserPlaylist(id: Long) = viewModelScope.launch {
        _account.emit(id)
    }

    fun sendCaptcha(phone: String) = _phone.update {
        phone
    }

    fun login(phone: String, password: String, isCaptcha: Boolean) =
        if (!isCaptcha) {
            tingRepository.loginCellphone(phone, password)
        } else {
            tingRepository.loginCaptcha(phone, password)
        }.onEach {
            it["code"]?.jsonPrimitive?.intOrNull?.let { loginState ->
                _loginState.update { loginState }
            }
        }.launchIn(viewModelScope)

    fun logout() {
        refreshUserData()
        _loginState.update { 0 }
    }

    fun play(vararg mediaItem: MediaItem) = viewModelScope.launch {
        _play.emit(mediaItem.toList())
    }
}