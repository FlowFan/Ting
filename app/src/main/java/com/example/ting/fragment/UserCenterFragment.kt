package com.example.ting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.ting.databinding.FragmentUserCenterBinding
import com.example.ting.model.UserPlaylist
import com.example.ting.other.cookieDataStore
import com.example.ting.ui.theme.TingTheme
import com.example.ting.viewmodel.TingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun RequireLoginVisible(
    viewModel: TingViewModel,
    navController: NavController
) {
    val userData by viewModel.userData.collectAsStateWithLifecycle()
    if (userData.account.id != 0L) {
        val playlists by viewModel.userPlaylist.collectAsStateWithLifecycle()
        LaunchedEffect(userData) {
            if (playlists.playlist.isEmpty()) {
                viewModel.refreshUserPlaylist(userData.account.id)
            }
        }
        val context = LocalContext.current
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = userData.profile.nickname)
                    },
                    navigationIcon = {
                        val painter = rememberAsyncImagePainter(model = userData.profile.avatarUrl)
                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .clip(CircleShape)
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
                                    viewModel.viewModelScope.launch {
                                        context.cookieDataStore.edit {
                                            it.clear()
                                        }
                                        viewModel.logout()
                                    }
                                }
                                .padding(8.dp),
                            imageVector = Icons.AutoMirrored.Rounded.Logout,
                            contentDescription = "Search"
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors()
                )
            }
        ) { innerPadding ->
            var isRefreshing by remember { mutableStateOf(false) }
            val state = rememberPullToRefreshState()
            val coroutineScope = rememberCoroutineScope()
            val onRefresh: () -> Unit = {
                isRefreshing = true
                coroutineScope.launch {
                    viewModel.refreshUserPlaylist(userData.account.id)
                    delay(1000)
                    isRefreshing = false
                }
            }
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.padding(innerPadding),
                state = state,
                contentAlignment = Alignment.TopCenter
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

                        items(
                            items = it.component2(),
                            key = { item -> item.id }
                        ) { item ->
                            PlayListItem(item, navController)
                        }
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

@Composable
private fun PlayListItem(
    playlist: UserPlaylist.Playlist,
    navController: NavController
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate(MainFragmentDirections.actionMainFragmentToSongListFragment(playlist.id))
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
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(playlist.coverImgUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(10))
                    .aspectRatio(1f)
                    .heightIn(min = 100.dp)
                    .fillMaxHeight(),
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
                navController.navigate(MainFragmentDirections.actionMainFragmentToSongListFragment(playlist.id))
            }) {
                Icon(Icons.Rounded.Menu, null)
            }
        }
    }
}