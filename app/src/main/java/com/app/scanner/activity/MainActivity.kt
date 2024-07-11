package com.app.scanner.activity

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.scanner.R
import com.app.scanner.repository.Repository
import com.app.scanner.ui.component.CustomDialog
import com.app.scanner.ui.component.InputContent
import com.app.scanner.ui.routes.HomeScreen
import com.app.scanner.ui.routes.SettingScreen
import com.app.scanner.ui.screens.HomeScreen
import com.app.scanner.ui.screens.SettingScreen
import com.app.scanner.ui.screens.WelcomeScreen
import com.app.scanner.ui.screens.scanDoc
import com.app.scanner.ui.theme.AppTheme
import com.app.scanner.util.Preferences
import com.app.scanner.util.checkAndCreateDirectory
import com.app.scanner.util.checkAndCreateInternalDirectory
import com.app.scanner.util.checkPermission
import com.app.scanner.util.getTodayDate
import com.app.scanner.util.getVersionName
import com.app.scanner.util.renameFileInDirectory
import com.app.scanner.util.saveFileInDirectory
import com.app.scanner.util.showPermissionDialogFrequency
import com.app.scanner.viewModel.MainViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

class MainActivity : ComponentActivity() {

    private lateinit var repository: Repository
    private lateinit var viewModel: MainViewModel
    private var isAllowed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = Repository(this@MainActivity)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(repository) as T
            }
        })[MainViewModel::class.java]

        Preferences.getInstance(applicationContext)

        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isAllowed = checkPermission(this)
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun MainScreen() {
        val categoryList by viewModel.categoryList.collectAsState()
        var recentSavedFileUri by remember { mutableStateOf("".toUri()) }
        val navController = rememberNavController()
        var isOnboarded by remember { mutableStateOf(viewModel.getOnboarded()) }
        val selectedItem = remember { mutableIntStateOf(1) }
        var showDialog by remember { mutableIntStateOf(0) }

        LaunchedEffect(Unit) {
            viewModel.getCategories()
        }

        if (showDialog == 1) {
            CustomDialog(onDismissRequest = {}) {
                InputContent(
                    categoryList = categoryList,
                    oldFileName = "Pro scanner ${getTodayDate()}",
                    onNegativeClick = { fileName ->
                        showDialog = 0
                        saveFile(fileName, recentSavedFileUri, isAllowed)
                    },
                    onPositiveClick = { fileName ->
                        showDialog = 0
                        saveFile(fileName, recentSavedFileUri, isAllowed)
                    })
            }
        } else if (showDialog == 2) {
            CustomDialog(onDismissRequest = {}) {
                InputContent(
                    categoryList = categoryList,
                    oldFileName = recentSavedFileUri.toFile().name,
                    onNegativeClick = { showDialog = 0 },
                    onPositiveClick = { fileName ->
                        showDialog = 0
                        renameFile(fileName, recentSavedFileUri, isAllowed)
                    })
            }
        }

        val scannerLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = {
                    if (it.resultCode == RESULT_OK) {
                        val scanningResult =
                            GmsDocumentScanningResult.fromActivityResultIntent(it.data)
                        scanningResult?.pdf?.let { pdf ->
                            recentSavedFileUri = pdf.uri
                            showDialog = 1
                        }
                    }
                })
        if (!isOnboarded) {
            WelcomeScreen(viewModel = viewModel, onFinish = { isOnboarded = true })
        } else {
            Scaffold(bottomBar = {
                BottomNavigationBar(
                    onFilesClick = {
                        if (selectedItem.intValue != 1) navController.navigateUp()
                    },
                    onSettingClick = {
                        if (selectedItem.intValue != 2) navController.navigate(SettingScreen)
                    },
                    onScanDocClick = {
                        scanDoc(this@MainActivity, scannerLauncher)
                    },
                    selectedItem = selectedItem.intValue
                )
            }) { innerPadding ->
                NavHost(
                    navController = navController, startDestination = HomeScreen
                ) {
                    composable<HomeScreen>(
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(300)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300)
                            )
                        },
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(300)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300)
                            )
                        }) {
                        selectedItem.intValue = 1
                        HomeScreen(
                            viewModel,
                            context = this@MainActivity,
                            innerPadding,
                            if (showPermissionDialogFrequency(context = this@MainActivity)) 1 else 0,
                            viewModel.getIsSwipeToDeleteEnable(),
                            onEditClick = {
                                showDialog = 2
                                recentSavedFileUri = it
                            }
                        )
                    }
                    composable<SettingScreen>(
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300)
                            )
                        },
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(300)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(300)
                            )
                        }) {
                        selectedItem.intValue = 2
                        SettingScreen(
                            viewModel,
                            innerPadding,
                            getVersionName(this@MainActivity),
                            isAllowed
                        )
                    }
                }
            }
        }
    }

    private fun renameFile(newFileName: String, uri: Uri, isAllowed: Boolean) {
        viewModel.removeDocument(uri)
        val file = if (isAllowed) {
            renameFileInDirectory(
                checkAndCreateDirectory(),
                newFileName,
                uri.toFile()
            )
        } else {
            renameFileInDirectory(
                checkAndCreateInternalDirectory(this@MainActivity),
                newFileName,
                uri.toFile()
            )
        }
        viewModel.addDocument(file)
    }

    private fun saveFile(fileName: String, uri: Uri, isAllowed: Boolean) {
        val file = if (isAllowed) {
            saveFileInDirectory(
                checkAndCreateDirectory(),
                fileName,
                uri.toFile()
            )
        } else {
            saveFileInDirectory(
                checkAndCreateInternalDirectory(this@MainActivity),
                fileName,
                uri.toFile()
            )
        }
        viewModel.addDocument(file)
    }

    @Composable
    fun BottomNavigationBar(
        selectedItem: Int,
        onFilesClick: () -> Unit,
        onScanDocClick: () -> Unit,
        onSettingClick: () -> Unit
    ) {
        BottomAppBar(content = {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = onFilesClick, colors = IconButtonDefaults.iconButtonColors(
                        contentColor = if (selectedItem == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        modifier = Modifier
                            .height(40.dp)
                            .width(40.dp)
                            .rotate(90f)
                            .padding(8.dp),
                        painter = painterResource(id = R.drawable.ic_folder),
                        contentDescription = "Folder"
                    )
                }

                Button(
                    onClick = onScanDocClick,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier.padding(end = 8.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface),
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "Scan"
                        )
                        Text(
                            text = "SCAN"
                        )
                    }
                }

                IconButton(
                    onClick = onSettingClick, colors = IconButtonDefaults.iconButtonColors(
                        contentColor = if (selectedItem == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        modifier = Modifier
                            .height(40.dp)
                            .width(40.dp)
                            .padding(8.dp),
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Settings"
                    )
                }
            }
        })
    }
}
