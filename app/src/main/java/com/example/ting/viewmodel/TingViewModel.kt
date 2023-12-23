package com.example.ting.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.ting.model.*
import com.example.ting.model.Detail.Track
import com.example.ting.model.Event.OnIsPlayingChanged
import com.example.ting.model.Event.OnMediaItemTransition
import com.example.ting.other.toast
import com.example.ting.repository.TingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class TingViewModel @Inject constructor(
    private val tingRepository: TingRepository
) : ViewModel() {
    private val _detailId: MutableStateFlow<Long> = MutableStateFlow(0)
    private val _unit: MutableSharedFlow<Unit> = MutableSharedFlow()
    private val _account: MutableSharedFlow<Long> = MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val _phone: MutableStateFlow<String> = MutableStateFlow("")

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

    val mediaController: StateFlow<MediaController?> by lazy {
        tingRepository.getMediaController().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = null
        )
    }

    private val event: SharedFlow<Event> = mediaController.filterNotNull().flatMapLatest {
        callbackFlow {
            val listener = object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    trySendBlocking(OnMediaItemTransition(mediaItem ?: MediaItem.EMPTY, reason))
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    trySendBlocking(OnIsPlayingChanged(isPlaying))
                }
            }
            it.addListener(listener)
            awaitClose {
                it.removeListener(listener)
            }
        }.conflate().flowOn(Dispatchers.Main)
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO).shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds)
    )

    val currentMediaItem: StateFlow<MediaItem> = merge(
        mediaController.filterNotNull().mapLatest {
            it.currentMediaItem ?: MediaItem.EMPTY
        }.flowOn(Dispatchers.Main),
        event.filterIsInstance<OnMediaItemTransition>().mapLatest {
            it.mediaItem
        }
    ).catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = MediaItem.EMPTY
    )

    val isPlaying: StateFlow<Boolean> = merge(
        mediaController.filterNotNull().mapLatest {
            it.isPlaying
        }.flowOn(Dispatchers.Main),
        event.filterIsInstance<OnIsPlayingChanged>().mapLatest {
            it.isPlaying
        }
    ).catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = false
    )

    val playProgress: StateFlow<Pair<Long, Long>> = combine(mediaController.filterNotNull(), currentMediaItem, isPlaying) { mediaController, mediaItem, isPlaying ->
        Triple(mediaController, mediaItem, isPlaying)
    }.flatMapLatest { (mediaController, mediaItem, isPlaying) ->
        flow {
            while (mediaItem != MediaItem.EMPTY && isPlaying) {
                emit(mediaController.currentPosition to mediaController.duration.coerceAtLeast(1000L))
                delay(20.milliseconds)
            }
        }.flowOn(Dispatchers.Main)
    }.catch {
        it.printStackTrace()
    }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = 0L to 1000L
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
        with(mediaController.filterNotNull().first()) {
            stop()
            clearMediaItems()
            addMediaItems(mediaItem.toList())
            prepare()
            play()
        }
    }
}