package com.example.ting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.Player
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil.compose.rememberAsyncImagePainter
import com.example.ting.databinding.FragmentPlayerBinding
import com.example.ting.exoplayer.MusicService
import com.example.ting.model.LyricLine
import com.example.ting.model.formatAsPlayerTime
import com.example.ting.model.parse
import com.example.ting.other.rememberCurrentMediaItem
import com.example.ting.other.rememberMediaSessionPlayer
import com.example.ting.other.rememberPlayProgress
import com.example.ting.other.rememberPlayState
import com.example.ting.ui.theme.TingTheme
import com.example.ting.viewmodel.TingViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToLong

@AndroidEntryPoint
class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<TingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TingTheme(false) {
                    val player by rememberMediaSessionPlayer(MusicService::class.java)
                    val userData by viewModel.userData.collectAsState()
                    LaunchedEffect(userData) {
                        viewModel.loadLikeList(userData.account.id)
                    }
                    when (player) {
                        null -> {
                            NotConnectScreen(findNavController())
                        }
                        else -> {
                            PlayerUI(player!!, viewModel, findNavController())
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotConnectScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.padding(
                    WindowInsets.statusBars.asPaddingValues()
                ),
                title = {
                    Text(text = "播放器")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Rounded.Close, "Back")
                    }
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "很抱歉，无法连接到播放器服务", modifier = Modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
private fun PlayerUI(
    player: Player,
    viewModel: TingViewModel,
    navController: NavController
) {
    val pagerState = rememberPagerState()
    val currentMediaItem = rememberCurrentMediaItem(player)
    val progress = rememberPlayProgress(player)
    val isPlaying = rememberPlayState(player)
    val rotator by produceState(
        initialValue = 0f,
        key1 = isPlaying
    ) {
        while (isActive && isPlaying == true) {
            value = (value + 1f) % 360f
            delay(12L)
        }
    }

    val musicDetail by viewModel.musicDetail.collectAsState()
    val painter = rememberAsyncImagePainter(
        model = musicDetail.songs[0].al.picUrl
    )

    LaunchedEffect(currentMediaItem) {
        viewModel.loadMusicDetail(currentMediaItem?.mediaId?.toLong() ?: 0L)
    }
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(Icons.Rounded.Close, "Back")
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = currentMediaItem?.mediaMetadata?.title?.toString() ?: "暂未播放",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = currentMediaItem?.mediaMetadata?.artist?.toString() ?: "暂未播放",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
                IconButton(onClick = {

                }) {
                    Icon(Icons.Rounded.Menu, null)
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val percent = progress?.let { (it.first * 100f / it.second) / 100f } ?: 0f
                    Text(text = progress?.first?.formatAsPlayerTime() ?: "00:00")
                    var valueChanger by remember(percent) {
                        mutableStateOf(percent)
                    }
                    Slider(
                        modifier = Modifier.weight(1f),
                        value = valueChanger,
                        onValueChange = {
                            valueChanger = it
                        },
                        onValueChangeFinished = {
                            player.seekTo(
                                (valueChanger * (progress?.second ?: 0L))
                                    .roundToLong()
                                    .coerceAtLeast(0L)
                            )
                        }
                    )
                    Text(text = progress?.second?.formatAsPlayerTime() ?: "00:00")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    var show by remember {
                        mutableStateOf(false)
                    }
                    DropdownMenu(
                        expanded = show,
                        onDismissRequest = { show = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            show = false
                            player.repeatMode = Player.REPEAT_MODE_ONE
                        }, text = {
                            Text(text = "单曲循环")
                        })
                        DropdownMenuItem(onClick = {
                            show = false
                            player.repeatMode = Player.REPEAT_MODE_ALL
                        }, text = {
                            Text(text = "列表循环")
                        })
                    }
                    IconButton(onClick = {
                        show = true
                    }) {
                        Icon(Icons.Rounded.Repeat, null)
                    }

                    IconButton(onClick = {
                        player.seekToPreviousMediaItem()
                    }) {
                        Icon(Icons.Rounded.SkipPrevious, null)
                    }

                    IconButton(
                        onClick = {
                            if (player.isPlaying) {
                                player.pause()
                            } else {
                                player.play()
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(60.dp),
                            imageVector = if (isPlaying == true) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = null
                        )
                    }

                    IconButton(onClick = {
                        player.seekToNextMediaItem()
                    }) {
                        Icon(Icons.Rounded.SkipNext, null)
                    }

                    val likeList by viewModel.likeList.collectAsState()
                    val userData by viewModel.userData.collectAsState()
                    IconButton(onClick = {
                        viewModel.like(userData.account.id)
                    }) {
                        Icon(
                            imageVector = if (likeList.ids.contains(currentMediaItem?.mediaId?.toLong())) {
                                Icons.Rounded.Favorite
                            } else {
                                Icons.Rounded.FavoriteBorder
                            },
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) {
        HorizontalPager(
            count = 2,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) { page ->
            when (page) {
                // 封面
                0 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .rotate(rotator)
                                .clip(CircleShape)
                                .fillMaxWidth(0.5f)
                                .aspectRatio(1f)
                                .background(Color.Black),
                            painter = painter,
                            contentDescription = null
                        )
                    }
                }

                // 歌词
                1 -> {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val lyric by viewModel.lyric.collectAsState()
                        val lyricLines = lyric.parse()
                        val listState = rememberLazyListState()

                        var currentLyricIndex by remember {
                            mutableStateOf(0)
                        }

                        LaunchedEffect(progress) {
                            val currentLyric = (progress?.first?.div(1000) ?: 0).toInt()
                            val index = lyricLines.indexOfLast { lyric ->
                                lyric.time <= currentLyric
                            }
                            index.takeIf { i -> i >= 0 }?.let { i ->
                                if (listState.firstVisibleItemIndex < i) {
                                    listState.animateScrollToItem(i)
                                }
                                currentLyricIndex = i
                            }
                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            state = listState
                        ) {
                            if (lyricLines.isEmpty()) {
                                item {
                                    Text(text = "没有歌词")
                                }
                            }
                            itemsIndexed(lyricLines) { index, item ->
                                LyricItem(item, currentLyricIndex == index)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LyricItem(lyricLine: LyricLine, currentLyric: Boolean = false) {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = lyricLine.lyric,
            fontWeight = if (currentLyric) FontWeight.Bold else LocalTextStyle.current.fontWeight,
            style = if (currentLyric) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium
        )
        lyricLine.translation?.let {
            Text(
                text = it,
                fontWeight = if (currentLyric) FontWeight.Bold else LocalTextStyle.current.fontWeight,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}