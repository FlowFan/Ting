package com.example.ting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil.compose.rememberAsyncImagePainter
import com.example.ting.R
import com.example.ting.databinding.FragmentLoginBinding
import com.example.ting.other.toast
import com.example.ting.ui.theme.TingTheme
import com.example.ting.viewmodel.TingViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.burnoo.compose.rememberpreference.rememberStringPreference

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<TingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TingTheme(false) {
                    LoginScreen(viewModel, findNavController())
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
private fun LoginScreen(
    viewModel: TingViewModel,
    navController: NavController
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.padding(
                    WindowInsets.statusBars.asPaddingValues()
                ),
                title = {
                    Text(text = "登录")
                },
                colors = TopAppBarDefaults.smallTopAppBarColors()
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding()
                .navigationBarsPadding()
                .imePadding()
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Body(viewModel, navController)
        }
    }
}

@Composable
private fun Body(
    viewModel: TingViewModel,
    navController: NavController
) {
    val loginState by viewModel.loginState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val isSend by viewModel.isSend.collectAsState()

    AnimatedVisibility(showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = "关闭")
                }
            },
            title = {
                Text(text = "登录")
            },
            text = {
                Text(
                    when (loginState.code) {
                        0 -> "登录中, 请稍等..."
                        200 -> "登录成功！"
                        400 -> "没有此账号!"
                        502 -> "密码错误!"
                        501 -> "登录时发生错误，请检查你的网络连接！"
                        509 -> "请求频繁，请稍后重试！"
                        else -> "未知错误: ${loginState.code}"
                    }
                )
            }
        )
    }

    LaunchedEffect(loginState) {
        showDialog = loginState.code != 0
        if (loginState.code == 200) {
            // 登录成功
            "登录成功".toast()
            viewModel.init()
            navController.navigateUp()
        }
    }

    var username by rememberStringPreference(
        keyName = "login.phone",
        defaultValue = "",
        initialValue = ""
    )
    var password by rememberStringPreference(
        keyName = "login.password",
        defaultValue = "",
        initialValue = ""
    )
    Column(
        modifier = Modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.mipmap.ic_launcher),
            contentDescription = null,
            modifier = Modifier
                .padding(32.dp)
                .size(100.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = if (it.length > 11) {
                    it.substring(0..10)
                } else {
                    it
                }
            },
            singleLine = true,
            label = {
                Text(text = "手机号")
            },
            leadingIcon = {
                Text(
                    text = "+86",
                    color = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            )
        )

        var passwordVisible by remember {
            mutableStateOf(false)
        }
        if (!isSend) {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = if (it.length > 16) {
                        it.substring(0..15)
                    } else {
                        it
                    }
                },
                label = {
                    Text(text = "密码")
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                    }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )
        } else {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = if (it.length > 4) {
                        it.substring(0..3)
                    } else {
                        it
                    }
                },
                label = {
                    Text(text = "验证码")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                showDialog = true
                if (!isSend) {
                    viewModel.loginCellPhone(
                        phone = username,
                        password = password
                    )
                } else {
                    viewModel.loginCaptcha(username, password)
                }
            }
        ) {
            Text(text = "登录")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.sendCaptcha(username)
            }
        ) {
            Text(text = "获取验证码")
        }
    }
}