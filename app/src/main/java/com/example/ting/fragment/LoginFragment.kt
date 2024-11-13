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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil3.compose.rememberAsyncImagePainter
import com.example.ting.R
import com.example.ting.databinding.FragmentLoginBinding
import com.example.ting.other.toast
import com.example.ting.ui.theme.TingTheme
import com.example.ting.viewmodel.TingViewModel

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
            TopAppBar(
                title = {
                    Text(text = "登录")
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
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
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val isSend by viewModel.isSend.collectAsStateWithLifecycle()

    AnimatedVisibility(showDialog) {
        AlertDialog(
            onDismissRequest = {
                setShowDialog(false)
            },
            confirmButton = {
                TextButton(onClick = {
                    setShowDialog(false)
                }) {
                    Text(text = "关闭")
                }
            },
            title = {
                Text(text = "登录")
            },
            text = {
                Text(
                    when (loginState) {
                        0 -> "登录中, 请稍等..."
                        200 -> "登录成功！"
                        400 -> "没有此账号!"
                        502 -> "密码错误!"
                        501 -> "登录时发生错误，请检查你的网络连接！"
                        509 -> "请求频繁，请稍后重试！"
                        else -> "未知错误: $loginState"
                    }
                )
            }
        )
    }

    LaunchedEffect(loginState) {
        setShowDialog(loginState != 0)
        if (loginState == 200) {
            // 登录成功
            "登录成功".toast()
            viewModel.refreshUserData()
            navController.navigateUp()
        }
    }

    val (username, setUsername) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
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
                if (it.length <= 11) {
                    setUsername(it)
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

        val (passwordVisible, setPasswordVisible) = remember { mutableStateOf(false) }
        if (!isSend) {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    if (it.length <= 16) {
                        setPassword(it)
                    }
                },
                label = {
                    Text(text = "密码")
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = {
                        setPasswordVisible(!passwordVisible)
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
                    if (it.length <= 4) {
                        setPassword(it)
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
                setShowDialog(true)
                viewModel.login(username, password, isSend)
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