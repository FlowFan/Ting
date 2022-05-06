package com.example.ting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.ting.databinding.FragmentUserCenterBinding
import com.example.ting.model.UserPlaylist
import com.example.ting.other.sharedPreferencesOf
import com.example.ting.ui.theme.TingTheme
import com.example.ting.viewmodel.TingViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserCenterFragment : Fragment() {
    private var _binding: FragmentUserCenterBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<TingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUserCenterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TingTheme(false) {
                    RequireLoginVisible(viewModel, findNavController())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalCoilApi::class
)
@Composable
fun RequireLoginVisible(
    viewModel: TingViewModel,
    navController: NavController
) {
    val userData by viewModel.userData.collectAsState()
    val playlists by viewModel.userPlaylist.collectAsState()
    if (userData.account.id != 0L) {
        LaunchedEffect(userData) {
            if (playlists.playlist.isEmpty()) {
                viewModel.refreshLibraryPage(userData.account.id)
            }
        }
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    modifier = Modifier.padding(
                        rememberInsetsPaddingValues(
                            insets = LocalWindowInsets.current.statusBars,
                            applyBottom = false
                        )
                    ),
                    title = {
                        Text(text = userData.profile.nickname)
                    },
                    navigationIcon = {
                        val painter = rememberImagePainter(data = userData.profile.avatarUrl)
                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .clip(CircleShape)
                                .placeholder(
                                    visible = painter.state is ImagePainter.State.Loading,
                                    highlight = PlaceholderHighlight.shimmer()
                                )
                                .size(50.dp),
                            painter = painter,
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    },
                    actions = {
                        Icon(
                            modifier = Modifier
                                .clickable {
                                    sharedPreferencesOf("cookie").edit {
                                        clear()
                                    }
                                    viewModel.init()
                                }
                                .padding(8.dp),
                            imageVector = Icons.Rounded.Logout,
                            contentDescription = "Search"
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors()
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                playlists.playlist.groupBy {
                    it.creator.userId == userData.account.id
                }.forEach {
                    stickyHeader {
                        Surface(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = if (it.component1()) "创建的声音单" else "收藏的声音单",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    items(it.component2()) { item ->
                        PlayListItem(item, navController)
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "点击此处进行登录",
                modifier = Modifier.clickable {
                    navController.navigate(MainFragmentDirections.actionMainFragmentToLoginFragment())
                }
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PlayListItem(
    playlist: UserPlaylist.Playlist,
    navController: NavController
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate(
                    MainFragmentDirections.actionMainFragmentToSongListFragment(
                        playlist.id
                    )
                )
            },
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val painter = rememberImagePainter(data = playlist.coverImgUrl)
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(10))
                    .aspectRatio(1f)
                    .heightIn(min = 100.dp)
                    .fillMaxHeight()
                    .placeholder(
                        visible = painter.state is ImagePainter.State.Loading,
                        highlight = PlaceholderHighlight.shimmer()
                    ),
                contentScale = ContentScale.FillHeight
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = "${playlist.trackCount} 首声音 ${playlist.playCount} 次播放",
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
            }

            IconButton(onClick = {
                navController.navigate(
                    MainFragmentDirections.actionMainFragmentToSongListFragment(
                        playlist.id
                    )
                )
            }) {
                Icon(Icons.Rounded.Menu, null)
            }
        }
    }
}