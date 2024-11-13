package com.example.ting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.ting.databinding.FragmentSongListBinding
import com.example.ting.model.SongList
import com.example.ting.other.Constants
import com.example.ting.other.toast
import com.example.ting.ui.theme.TingTheme
import com.example.ting.viewmodel.TingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class SongListFragment : Fragment() {
    private var _binding: FragmentSongListBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<SongListFragmentArgs>()
    private val viewModel by viewModels<TingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setDetailId(args.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSongListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TingTheme(false) {
                    val lazyListState = rememberLazyListState()
                    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
                    val songList by viewModel.songList.collectAsStateWithLifecycle()
                    val scope = rememberCoroutineScope()
                    Scaffold(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(text = "声音详情")
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        findNavController().navigateUp()
                                    }) {
                                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    scrolledContainerColor = MaterialTheme.colorScheme.background
                                ),
                                scrollBehavior = scrollBehavior
                            )
                        },
                        floatingActionButton = {
                            if (remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }.value > 10) {
                                FloatingActionButton(
                                    modifier = Modifier.navigationBarsPadding(),
                                    onClick = {
                                        scope.launch {
                                            lazyListState.scrollToItem(0)
                                        }
                                    }
                                ) {
                                    Icon(Icons.Rounded.ArrowUpward, null)
                                }
                            }
                        }
                    ) { innerPadding ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = lazyListState,
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                top = innerPadding.calculateTopPadding() + 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                SongInfo(songList.playlist)
                            }
                            item {
                                SongIcon(viewModel, songList.playlist)
                            }
                            itemsIndexed(
                                items = songList.playlist.tracks,
                                key = { _, item -> item.id }
                            ) { index, item ->
                                SongList(
                                    viewModel = viewModel,
                                    index = index,
                                    track = item
                                )
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
private fun SongInfo(
    playlist: SongList.Playlist
) {
    val (showPlaylistDetailDialog, setShowPlaylistDetailDialog) = remember { mutableStateOf(false) }
    if (showPlaylistDetailDialog) {
        AlertDialog(
            onDismissRequest = {
                setShowPlaylistDetailDialog(false)
            },
            title = {
                Text(text = playlist.name.ifBlank { "歌单信息" })
            },
            text = {
                SelectionContainer {
                    Text(text = playlist.description.ifBlank { "加载中" })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    setShowPlaylistDetailDialog(false)
                }) {
                    Text(text = "关闭")
                }
            }
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(playlist.coverImgUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    setShowPlaylistDetailDialog(true)
                },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = playlist.name.ifBlank { "加载中" },
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = playlist.creator.nickname.ifBlank { "加载中" },
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = playlist.description.ifBlank { "歌单描述" },
                overflow = TextOverflow.Ellipsis,
                maxLines = 4,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun SongIcon(
    viewModel: TingViewModel,
    playlist: SongList.Playlist
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = {
            viewModel.play(
                *playlist.tracks.map {
                    MediaItem
                        .Builder()
                        .setMediaId("${it.id}")
                        .setUri("${Constants.TING_PROTOCOL}://${Constants.DOMAIN}?id=${it.id}".toUri())
                        .setMediaMetadata(
                            MediaMetadata
                                .Builder()
                                .setTitle(it.name)
                                .setArtist(it.ar.joinToString(", ") { ar -> ar.name })
                                .setArtworkUri(it.al.picUrl.toUri())
                                .build()
                        )
                        .build()
                }.toTypedArray()
            )
            "开始播放该声音单".toast()
        }) {
            Text(text = "播放")
        }

        IconButton(onClick = {
//            viewModel.subscribe(playlist.id, playlist.subscribed)
        }) {
            Icon(
                imageVector = if (playlist.subscribed) {
                    Icons.Rounded.Favorite
                } else {
                    Icons.Rounded.FavoriteBorder
                },
                contentDescription = null
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = "共 ${playlist.trackCount} 首声音",
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun SongList(
    viewModel: TingViewModel,
    index: Int,
    track: SongList.Playlist.Track
) {
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
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(track.al.picUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .size(50.dp),
                contentScale = ContentScale.FillBounds
            )
            Column(modifier = Modifier.weight(1f)) {
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
                viewModel.play(
                    MediaItem
                        .Builder()
                        .setMediaId("${track.id}")
                        .setUri("${Constants.TING_PROTOCOL}://${Constants.DOMAIN}?id=${track.id}".toUri())
                        .setMediaMetadata(
                            MediaMetadata
                                .Builder()
                                .setTitle(track.name)
                                .setArtist(track.ar.joinToString(", ") { ar -> ar.name })
                                .setArtworkUri(track.al.picUrl.toUri())
                                .build()
                        )
                        .build()
                )
            }) {
                Icon(Icons.Rounded.PlayArrow, null)
            }
        }
    }
}