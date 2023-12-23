package com.example.ting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DashboardCustomize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.ting.databinding.FragmentTypeBinding
import com.example.ting.other.toast
import com.example.ting.ui.theme.TingTheme
import com.example.ting.viewmodel.TingViewModel
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun DiscoverPage(
    viewModel: TingViewModel,
    navController: NavController
) {
    val (editing, setEditing) = remember { mutableStateOf(false) }
    val category = listOf("全部", "官方", "精品")
    val pagerState = rememberPagerState { category.size }
    val coroutineScope = rememberCoroutineScope()

    Crossfade(
        targetState = editing,
        label = "editing"
    ) { edit ->
        if (edit) {
            Scaffold(topBar = {
                TopAppBar(
                    title = { Text(text = "自定义声音类型") },
                    navigationIcon = {
                        IconButton(onClick = { setEditing(false) }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        Button(onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(0)
                            }
                            "保存成功".toast()
                            setEditing(false)
                        }) {
                            Text(text = "保存")
                        }
                    }
                )
            }) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {

                }
            }
        } else {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        modifier = Modifier.weight(1f),
                        containerColor = Color.Transparent,
                        edgePadding = 0.dp
                    ) {
                        category.forEachIndexed { index, s ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            ) {
                                Text(
                                    text = s,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }
                    IconButton(onClick = { setEditing(true) }) {
                        Icon(Icons.Rounded.DashboardCustomize, null)
                    }
                }

                HorizontalPager(state = pagerState) {
                    LazyVerticalGrid(columns = GridCells.Fixed(3)) {

                    }
                }
            }
        }
    }
}