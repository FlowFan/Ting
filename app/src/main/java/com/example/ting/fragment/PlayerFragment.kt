package com.example.ting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import coil.compose.rememberImagePainter
import com.example.ting.databinding.FragmentPlayerBinding
import com.example.ting.ui.theme.TingTheme
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToLong

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TingTheme(false) {
                    PlayerUI()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@OptIn(ExperimentalPagerApi::class)
@ExperimentalMaterial3Api
@Composable
private fun PlayerUI(
//    player: Player,
//    playerScreenViewModel: PlayerScreenViewModel
) {
    val context = LocalContext.current
//    val pagerState = rememberPagerState()
//    val currentMediaItem = rememberCurrentMediaItem(player)
//    val progress = rememberPlayProgress(player)
//    val isPlaying = rememberPlayState(player)
//    val rotator by produceState(
//        initialValue = 0f,
//        key1 = isPlaying
//    ) {
//        while (isActive && isPlaying == true) {
//            value = (value + 1f) % 360f
//            delay(12L)
//        }
//    }

//    LaunchedEffect(currentMediaItem) {
//        playerScreenViewModel.loadMusicDetail(currentMediaItem?.mediaId?.toLong() ?: 0L)
//    }
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
                    // TODO:
                }) {
                    Icon(Icons.Rounded.Close, "Back")
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "暂未播放",
//                        text = currentMediaItem?.mediaMetadata?.title?.toString() ?: "暂未播放",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "暂未播放",
//                        text = currentMediaItem?.mediaMetadata?.artist?.toString() ?: "暂未播放",
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
                    val percent: Float = 0f
//                        progress?.let { (it.first * 100f / it.second) / 100f } ?: 0f
                    Text(text = "00:00")
//                    Text(text = progress?.first?.formatAsPlayerTime() ?: "00:00")
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
//                            player.seekTo(
//                                (valueChanger * (progress?.second ?: 0L))
//                                    .roundToLong()
//                                    .coerceAtLeast(0L)
//                            )
                        }
                    )
                    Text(text = "00:00")
//                    Text(text = progress?.second?.formatAsPlayerTime() ?: "00:00")
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
//                        DropdownMenuItem(onClick = {
//                            show = false
//                            player.repeatMode = Player.REPEAT_MODE_ONE
//                        }) {
//                            Text(text = "单曲循环")
//                        }
//                        DropdownMenuItem(onClick = {
//                            show = false
//                            player.repeatMode = Player.REPEAT_MODE_ALL
//                        }) {
//                            Text(text = "列表循环")
//                        }
                    }
                    IconButton(onClick = {
                        show = true
                    }) {
                        Icon(Icons.Rounded.Repeat, null)
                    }

                    IconButton(onClick = {
//                        player.seekToPreviousMediaItem()
                    }) {
                        Icon(Icons.Rounded.SkipPrevious, null)
                    }

                    IconButton(
                        onClick = {
//                            if (player.isPlaying) {
//                                player.pause()
//                            } else {
//                                player.play()
//                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(60.dp),
                            imageVector = Icons.Rounded.PlayArrow,
//                            imageVector = if (isPlaying == true) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = null
                        )
                    }

                    IconButton(onClick = {
//                        player.seekToNextMediaItem()
                    }) {
                        Icon(Icons.Rounded.SkipNext, null)
                    }

//                    val likeList by playerScreenViewModel.likeList.collectAsState()
                    IconButton(onClick = {
//                        playerScreenViewModel.like(userData.id)
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.FavoriteBorder,
//                            imageVector = if (likeList.readSafely()?.ids?.contains(
//                                    currentMediaItem?.mediaId?.toLong() ?: 0
//                                ) == true
//                            ) {
//                                Icons.Rounded.Favorite
//                            } else {
//                                Icons.Rounded.FavoriteBorder
//                            },
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) {
        HorizontalPager(
            count = 2,
//            state = pagerState,
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
//                                .rotate(rotator)
                                .clip(CircleShape)
                                .fillMaxWidth(0.5f)
                                .aspectRatio(1f)
                                .background(Color.Black),
                            painter = rememberImagePainter(""),
//                            painter = rememberImagePainter(
//                                data = musicDetail.readSafely()?.songs?.get(0)?.al?.picUrl
//                            ),
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
//                        val lyric by playerScreenViewModel.lyric.collectAsState()
//                        val lyricLines = lyric.readSafely()?.parse()
                        val listState = rememberLazyListState()

                        var currentLyricIndex by remember {
                            mutableStateOf(0)
                        }

//                        LaunchedEffect(progress) {
//                            lyricLines?.let { lines ->
//                                val currentLyric = (progress?.first?.div(1000) ?: 0).toInt()
//                                val index = lines.indexOfLast { lyric ->
//                                    lyric.time <= currentLyric
//                                }
//                                index.takeIf { i -> i >= 0 }?.let { i ->
//                                    if (listState.firstVisibleItemIndex < i) {
//                                        listState.animateScrollToItem(i)
//                                    }
//                                    currentLyricIndex = i
//                                }
//                            }
//                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            state = listState
                        ) {
//                            if (lyricLines?.isEmpty() != false) {
//                                item {
//                                    Text(text = "没有歌词")
//                                }
//                            }

//                            lyric.readSafely()?.parse()?.let { lines ->
//                                itemsIndexed(lines) { index, item ->
//                                    LyricItem(item, currentLyricIndex == index)
//                                }
//                            }
                        }
                    }
                }
            }
        }
    }
}