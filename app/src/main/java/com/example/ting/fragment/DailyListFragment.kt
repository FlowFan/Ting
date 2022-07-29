package com.example.ting.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.ting.databinding.FragmentDailyListBinding
import com.example.ting.exoplayer.MusicService
import com.example.ting.model.DailyList
import com.example.ting.other.Constants.TING_PROTOCOL
import com.example.ting.other.asyncGetSessionPlayer
import com.example.ting.other.buildMediaItem
import com.example.ting.other.metadata
import com.example.ting.ui.theme.TingTheme
import com.example.ting.viewmodel.TingViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyListFragment : Fragment() {
    private var _binding: FragmentDailyListBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<TingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDailyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TingTheme(false) {
                    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
                    val dailyList by viewModel.dailyList.observeAsState(DailyList())
                    Scaffold(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            LargeTopAppBar(
                                modifier = Modifier.padding(
                                    WindowInsets.statusBars.asPaddingValues()
                                ),
                                title = {
                                    Text(text = "每日推荐")
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        findNavController().navigateUp()
                                    }) {
                                        Icon(Icons.Rounded.ArrowBack, "Back")
                                    }
                                },
                                actions = {
                                    IconButton(onClick = {
                                        requireContext().asyncGetSessionPlayer(MusicService::class.java) {
                                            it.apply {
                                                stop()
                                                clearMediaItems()
                                                dailyList.data.dailySongs.forEach { track ->
                                                    addMediaItem(
                                                        buildMediaItem(track.id.toString()) {
                                                            metadata {
                                                                setTitle(track.name)
                                                                setArtist(track.ar.joinToString(", ") { ar -> ar.name })
                                                                setRequestMetadata(
                                                                    MediaItem.RequestMetadata.Builder()
                                                                        .setMediaUri(Uri.parse("$TING_PROTOCOL://music?id=${track.id}"))
                                                                        .build()
                                                                )
                                                                setArtworkUri(Uri.parse(track.al.picUrl))
                                                            }
                                                        }
                                                    )
                                                }
                                                prepare()
                                                play()
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Rounded.PlayArrow, null)
                                    }
                                },
                                colors = TopAppBarDefaults.largeTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    scrolledContainerColor = MaterialTheme.colorScheme.background
                                ),
                                scrollBehavior = scrollBehavior
                            )
                        }
                    ) { innerPadding ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                top = innerPadding.calculateTopPadding() + 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(dailyList.data.dailySongs) { index, item ->
                                DailyList(index = index, track = item)
                            }
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

@Composable
private fun DailyList(
    index: Int, track: DailyList.Data.DailySong
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 16.dp,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "${index + 1}")
            val painter = rememberAsyncImagePainter(model = track.al.picUrl)
            Image(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .placeholder(
                        visible = painter.state is AsyncImagePainter.State.Loading,
                        highlight = PlaceholderHighlight.shimmer()
                    )
                    .size(50.dp),
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = track.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = track.ar.joinToString(separator = "/") { it.name } + if (track.al.name.isNotBlank()) " - ${track.al.name}" else "",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            IconButton(onClick = {
                context.asyncGetSessionPlayer(MusicService::class.java) {
                    it.apply {
                        stop()
                        clearMediaItems()
                        addMediaItem(
                            buildMediaItem(track.id.toString()) {
                                metadata {
                                    setTitle(track.name)
                                    setArtist(track.ar.joinToString(", ") { ar -> ar.name })
                                    setRequestMetadata(
                                        MediaItem.RequestMetadata.Builder()
                                            .setMediaUri(Uri.parse("$TING_PROTOCOL://music?id=${track.id}"))
                                            .build()
                                    )
                                    setArtworkUri(Uri.parse(track.al.picUrl))
                                }
                            }
                        )
                        prepare()
                        play()
                    }
                }
            }) {
                Icon(Icons.Rounded.PlayArrow, null)
            }
        }
    }
}