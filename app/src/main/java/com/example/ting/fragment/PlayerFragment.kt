package com.example.ting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.ting.R
import com.example.ting.databinding.FragmentPlayerBinding
import com.example.ting.ui.theme.TingTheme
import com.example.ting.viewmodel.TingViewModel
import kotlin.math.roundToLong

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
                    PlayerUI(
                        viewModel = viewModel,
                        navController = findNavController()
                    )
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
private fun PlayerUI(
    viewModel: TingViewModel,
    navController: NavController
) {
    val pagerState = rememberPagerState { 2 }
    val currentMediaItem by viewModel.currentMediaItem.collectAsStateWithLifecycle()
    val mediaController by viewModel.mediaController.collectAsStateWithLifecycle()
    val playProgress by viewModel.playProgress.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentMediaItem.mediaMetadata.title?.toString() ?: "暂未播放",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = currentMediaItem.mediaMetadata.artist?.toString() ?: "暂无作者",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Rounded.Menu, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.format_time, playProgress.first / 60000, playProgress.first % 60000 / 1000))
                    val interactionSource = remember { MutableInteractionSource() }
                    Slider(
                        value = playProgress.first.toFloat(),
                        onValueChange = {
                            mediaController?.seekTo(it.roundToLong())
                        },
                        modifier = Modifier.weight(1f),
                        interactionSource = interactionSource,
                        thumb = {
                            SliderDefaults.Thumb(
                                interactionSource = interactionSource,
                                modifier = Modifier
                                    .size(20.dp, 20.dp)
                                    .indication(
                                        interactionSource = interactionSource,
                                        indication = ripple(
                                            bounded = false,
                                            radius = 20.dp
                                        )
                                    )
                            )
                        },
                        track = { sliderState ->
                            SliderDefaults.Track(
                                sliderState = sliderState,
                                modifier = Modifier.height(4.dp),
                                drawStopIndicator = null,
                                thumbTrackGapSize = 0.dp,
                                trackInsideCornerSize = 0.dp
                            )
                        },
                        valueRange = 0f..playProgress.second.toFloat()
                    )
                    Text(text = stringResource(id = R.string.format_time, playProgress.second / 60000, playProgress.second % 60000 / 1000))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (show, setShow) = remember { mutableStateOf(false) }
                    IconButton(onClick = { setShow(true) }) {
                        DropdownMenu(expanded = show, onDismissRequest = { setShow(false) }) {
                            DropdownMenuItem(
                                text = {
                                    Text(text = "单曲循环")
                                },
                                onClick = {
                                    setShow(false)
                                    mediaController?.repeatMode = Player.REPEAT_MODE_ONE
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = "列表循环")
                                },
                                onClick = {
                                    setShow(false)
                                    mediaController?.repeatMode = Player.REPEAT_MODE_ALL
                                }
                            )
                        }
                        Icon(imageVector = Icons.Rounded.Repeat, contentDescription = null)
                    }
                    IconButton(onClick = { mediaController?.seekToPreviousMediaItem() }) {
                        Icon(imageVector = Icons.Rounded.SkipPrevious, contentDescription = null)
                    }
                    IconButton(onClick = {
                        mediaController?.let {
                            if (it.isPlaying) {
                                it.pause()
                            } else {
                                it.play()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (isPlaying) {
                                Icons.Rounded.Pause
                            } else {
                                Icons.Rounded.PlayArrow
                            },
                            contentDescription = null,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                    IconButton(onClick = { mediaController?.seekToNextMediaItem() }) {
                        Icon(imageVector = Icons.Rounded.SkipNext, contentDescription = null)
                    }
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Rounded.Favorite, contentDescription = null)
                    }
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (it) {
                0 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(currentMediaItem.mediaMetadata.artworkUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .rotate(playProgress.first.toFloat() * 3600 / playProgress.second)
                                .clip(CircleShape)
                                .fillMaxWidth(0.5f)
                                .aspectRatio(1f)
                                .background(Color.Black)
                        )
                    }
                }

                1 -> {}
            }
        }
    }
}