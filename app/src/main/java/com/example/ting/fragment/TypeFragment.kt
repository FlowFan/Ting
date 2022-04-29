package com.example.ting.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DashboardCustomize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.ting.databinding.FragmentTypeBinding
import com.example.ting.init.AppInitializer
import com.example.ting.model.HighQualityPlaylist
import com.example.ting.model.Playlists
import com.example.ting.model.TypeList
import com.example.ting.other.toast
import com.example.ting.ui.theme.TingTheme
import com.example.ting.viewmodel.TingViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import soup.compose.material.motion.MaterialFadeThrough

@AndroidEntryPoint
class TypeFragment : Fragment() {
    private var _binding: FragmentTypeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<TingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TingTheme(false) {
                    DiscoverPage(viewModel, findNavController())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
@Composable
fun DiscoverPage(indexViewModel: TingViewModel, navController: NavController) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val categoryAll by indexViewModel.categoryAll.observeAsState(TypeList())
    val categorySelect by indexViewModel.categorySelected.collectAsState()
    var editing by remember { mutableStateOf(false) }
    val category = listOf("全部", "官方", "精品") + categorySelect

    MaterialFadeThrough(targetState = editing) { edit ->
        if (edit) {
            CategoryEditor(categoryAll, categorySelect) {
                scope.launch {
                    pagerState.scrollToPage(0)
                }
                AppInitializer.mContext.getSharedPreferences(
                    "playlist_category",
                    Context.MODE_PRIVATE
                ).edit {
                    putString("data", it.distinct().joinToString(","))
                }
                indexViewModel.refreshSelectedCategory()
                "保存成功".toast()
                editing = false
            }
        } else {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScrollableTabRow(
                        modifier = Modifier.weight(1f),
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                        edgePadding = 0.dp
                    ) {
                        category.forEachIndexed { index, sub ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            ) {
                                Text(text = sub, modifier = Modifier.padding(4.dp))
                            }
                        }
                    }

                    IconButton(onClick = { editing = true }) {
                        Icon(Icons.Rounded.DashboardCustomize, null)
                    }
                }
                HorizontalPager(
                    count = category.size,
                    state = pagerState
                ) {
                    TopPlaylist(indexViewModel, category[it], navController)
                }
            }
        }
    }
}

@Composable
private fun CategoryEditor(
    categoryAll: TypeList,
    selectedCategory: List<String>,
    onSave: (List<String>) -> Unit
) {
    var category by remember(selectedCategory) {
        mutableStateOf(selectedCategory)
    }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "自定义声音类型",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineSmall
            )
            Button(
                onClick = {
                    // 保存前重排序一遍
                    val list =
                        categoryAll.sub.filter { category.contains(it.name) }.map { it.name }
                            .toList()
                    onSave(list)
                }
            ) {
                Text(text = "保存")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            categoryAll.categories.entries.forEach { (k, v) ->
                item {
                    Text(text = v)
                }

                item {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        mainAxisSpacing = 8.dp
                    ) {
                        categoryAll.sub.filter { it.category == k.toInt() }.forEach { sub ->
                            if (category.contains(sub.name)) {
                                OutlinedButton(onClick = {
                                    category = ArrayList(
                                        category.toMutableList().apply { remove(sub.name) })
                                }) {
                                    Text(text = sub.name)
                                }
                            } else {
                                TextButton(onClick = {
                                    category =
                                        ArrayList(category.toMutableList().apply { add(sub.name) })
                                }) {
                                    Text(text = sub.name)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopPlaylist(
    indexViewModel: TingViewModel,
    category: String,
    navController: NavController
) {
    if (category == "精品") {
        val highQualityPlaylist by indexViewModel.highQualityPlaylist.observeAsState(
            HighQualityPlaylist()
        )
        LazyVerticalGrid(
            cells = GridCells.Adaptive(110.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(highQualityPlaylist.playlists) { playlist ->
                PlaylistItem(playlist, navController)
            }
        }
        return
    }

    val items = (indexViewModel.playlistCatPager[category] ?: run {
        indexViewModel.getTopPlaylist(category).also {
            indexViewModel.playlistCatPager[category] = it
        }
    }).collectAsLazyPagingItems()
    LazyVerticalGrid(
        cells = GridCells.Adaptive(110.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items.itemCount) { index ->
            PlaylistItem(items[index]!!, navController)
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun PlaylistItem(playlist: Playlists, navController: NavController) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable {
                navController.navigate(
                    TypeFragmentDirections.actionTypeFragmentToSongListFragment(
                        playlist.id
                    )
                )
            }
            .padding(8.dp)
            .width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val painter = rememberImagePainter(data = playlist.coverImgUrl)
        Image(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .placeholder(
                    visible = painter.state is ImagePainter.State.Loading,
                    highlight = PlaceholderHighlight.shimmer()
                )
                .size(100.dp),
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = playlist.name,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2
        )
    }
}