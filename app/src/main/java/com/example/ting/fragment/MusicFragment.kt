package com.example.ting.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.ting.databinding.FragmentMusicBinding
import com.example.ting.exoplayer.MusicService
import com.example.ting.model.NewSong
import com.example.ting.model.PlayList
import com.example.ting.model.TopList
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
class MusicFragment : Fragment() {
    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<TingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TingTheme(false) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            PlayList(viewModel, findNavController())
                        }
                        item {
                            LargeButton(findNavController())
                        }
                        item {
                            DailyWord(viewModel)
                        }
                        item {
                            NewSong(viewModel)
                        }
                        item {
                            TopList(viewModel, findNavController())
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
private fun PlayList(
    viewModel: TingViewModel,
    navController: NavController
) {
    val playList by viewModel.playList.observeAsState(PlayList())
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "推荐声音",
            style = MaterialTheme.typography.headlineSmall
        )
        ElevatedCard(
            shape = RoundedCornerShape(12.dp)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (playList.result.isEmpty()) {
                    items(5) {
                        Box(
                            modifier = Modifier
                                .placeholder(
                                    visible = true,
                                    highlight = PlaceholderHighlight.shimmer()
                                )
                                .size(100.dp)
                        )
                    }
                } else {
                    items(playList.result) {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(13.dp))
                                .clickable {
                                    navController.navigate(
                                        MainFragmentDirections.actionMainFragmentToSongListFragment(
                                            it.id
                                        )
                                    )
                                }
                                .padding(8.dp)
                                .width(IntrinsicSize.Min),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val painter = rememberAsyncImagePainter(model = it.picUrl)
                            Image(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .placeholder(
                                        visible = painter.state is AsyncImagePainter.State.Loading,
                                        highlight = PlaceholderHighlight.shimmer()
                                    )
                                    .size(100.dp),
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds
                            )
                            Text(
                                text = it.name,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 2,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LargeButton(
    navController: NavController
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "个性功能",
            style = MaterialTheme.typography.headlineSmall
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        navController.navigate(MainFragmentDirections.actionMainFragmentToTypeFragment())
                    },
                shape = RoundedCornerShape(8.dp),
                tonalElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(Icons.Rounded.Album, null)
                    Text(
                        text = "声音分类",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        navController.navigate(MainFragmentDirections.actionMainFragmentToDailyListFragment())
                    },
                shape = RoundedCornerShape(8.dp),
                tonalElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(Icons.Rounded.Today, null)
                    Text(
                        text = "每日推荐",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyWord(
    viewModel: TingViewModel
) {
    val dailyWord by viewModel.dailyWord.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "每日一言",
            style = MaterialTheme.typography.headlineSmall
        )
        ElevatedCard(
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 0.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (dailyWord.hitokoto.isBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "加载中 ~")
                    }
                } else {
                    Text(
                        text = dailyWord.hitokoto,
                        maxLines = 7
                    )
                    Text(
                        text = "《${dailyWord.from}》",
                        modifier = Modifier.align(Alignment.End)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
                ) {
                    TextButton(onClick = {
                        clipboardManager.setText(AnnotatedString(dailyWord.hitokoto))
                    }) {
                        Text(text = "复制")
                    }
                    TextButton(onClick = {
                        viewModel.refreshDailyWord()
                    }) {
                        Text(text = "下一个")
                    }
                }
            }
        }

    }
}

@Composable
private fun NewSong(
    viewModel: TingViewModel
) {
    val newSong by viewModel.newSong.observeAsState(NewSong())
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "新歌速递",
            style = MaterialTheme.typography.headlineSmall
        )
        ElevatedCard(
            shape = RoundedCornerShape(12.dp)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (newSong.result.isEmpty()) {
                    items(5) {
                        Box(
                            modifier = Modifier
                                .placeholder(
                                    visible = true,
                                    highlight = PlaceholderHighlight.shimmer()
                                )
                                .size(100.dp)
                        )
                    }
                } else {
                    items(newSong.result) { songList ->
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(13.dp))
                                .clickable {
                                    context.asyncGetSessionPlayer(MusicService::class.java) {
                                        it.apply {
                                            stop()
                                            clearMediaItems()
                                            addMediaItem(buildMediaItem(songList.id.toString()) {
                                                metadata {
                                                    setTitle(songList.name)
                                                    setArtist(songList.song.artists.joinToString(",") { ar -> ar.name })
                                                    setRequestMetadata(
                                                        MediaItem.RequestMetadata
                                                            .Builder()
                                                            .setMediaUri(Uri.parse("$TING_PROTOCOL://music?id=${songList.id}"))
                                                            .build()
                                                    )
                                                    setArtworkUri(Uri.parse(songList.picUrl))
                                                }
                                            })
                                            prepare()
                                            play()
                                        }
                                    }
                                }
                                .padding(8.dp)
                                .width(IntrinsicSize.Min),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val painter = rememberAsyncImagePainter(model = songList.picUrl)
                            Image(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .placeholder(
                                        visible = painter.state is AsyncImagePainter.State.Loading,
                                        highlight = PlaceholderHighlight.shimmer()
                                    )
                                    .size(100.dp),
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds
                            )
                            Text(
                                text = songList.name,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = songList.song.artists.joinToString(", ") { it.name },
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopList(
    viewModel: TingViewModel,
    navController: NavController
) {
    val topList by viewModel.topList.observeAsState(TopList())
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "声音榜单",
            style = MaterialTheme.typography.headlineSmall
        )
        ElevatedCard(
            shape = RoundedCornerShape(12.dp)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (topList.list.isEmpty()) {
                    items(5) {
                        Box(
                            modifier = Modifier
                                .placeholder(
                                    visible = true,
                                    highlight = PlaceholderHighlight.shimmer()
                                )
                                .size(100.dp)
                        )
                    }
                } else {
                    items(topList.list) {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    navController.navigate(
                                        MainFragmentDirections.actionMainFragmentToSongListFragment(
                                            it.id
                                        )
                                    )
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val painter = rememberAsyncImagePainter(model = it.coverImgUrl)
                            Image(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .placeholder(
                                        visible = painter.state is AsyncImagePainter.State.Loading,
                                        highlight = PlaceholderHighlight.shimmer()
                                    )
                                    .size(100.dp),
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds
                            )
                            Text(
                                text = it.name,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}