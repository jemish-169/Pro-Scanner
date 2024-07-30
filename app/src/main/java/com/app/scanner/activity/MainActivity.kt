package com.app.scanner.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.app.scanner.ui.component.InputFilename
import com.app.scanner.ui.routes.HomeScreen
import com.app.scanner.ui.routes.SettingScreen
import com.app.scanner.ui.screens.HomeScreen
import com.app.scanner.ui.screens.SettingScreen
import com.app.scanner.ui.screens.WelcomeScreen
import com.app.scanner.ui.theme.AppTheme
import com.app.scanner.util.Preferences
import com.app.scanner.util.askPermission
import com.app.scanner.util.checkAndCreateExternalParentDir
import com.app.scanner.util.checkAndCreateInternalParentDir
import com.app.scanner.util.checkPermission
import com.app.scanner.util.getTodayDate
import com.app.scanner.util.getVersionName
import com.app.scanner.util.renameAndMoveFile
import com.app.scanner.util.saveFileInDirectory
import com.app.scanner.util.saveFileToSelectedLocation
import com.app.scanner.util.scanDoc
import com.app.scanner.util.showPermissionDialogFrequency
import com.app.scanner.viewModel.MainViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repository: Repository
    private lateinit var viewModel: MainViewModel
    private var isAllowed = false
    private lateinit var originalFile: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Scanner)

        repository = Repository(context = this@MainActivity)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(repository) as T
            }
        })[MainViewModel::class.java]


        val saveFileToSelectedLocLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.also { uri ->
                        saveFileToSelectedLocation(
                            applicationContext,
                            uri,
                            originalFile
                        )
                    }
                }
            }


        Preferences.getInstance(applicationContext)

        setContent {
            val theme = viewModel.theme.collectAsState().value
            AppTheme(theme) {
                MainScreen(saveFileToSelectedLocLauncher)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isAllowed = checkPermission(this)
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun MainScreen(saveFileToSelectedLocLauncher: ActivityResultLauncher<Intent>) {
        var selectedCategory by remember { mutableStateOf("Other") }
        val categoryList by viewModel.categoryList.collectAsState()
        var recentSavedFileUri by remember { mutableStateOf("".toUri()) }
        val navController = rememberNavController()
        var isOnboarded by remember { mutableStateOf(viewModel.getOnboarded()) }
        val selectedItem = remember { mutableIntStateOf(1) }
        var showDialog by remember { mutableIntStateOf(0) }
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            viewModel.getCategories()
        }

        if (showDialog == 1) {
            SaveFileDialog(
                isNewFile = true,
                categoryList = categoryList,
                oldFileName = "Pro scan ${getTodayDate()}",
                category = selectedCategory,
                onDismiss = { showDialog = 0 },
                onSave = { fileName, category ->
                    showDialog = 0
                    saveFile(fileName, recentSavedFileUri, isAllowed, category)
                }
            )
        } else if (showDialog == 2) {
            SaveFileDialog(
                isNewFile = false,
                categoryList = categoryList,
                oldFileName = recentSavedFileUri.lastPathSegment.toString().dropLast(4),
                category = selectedCategory,
                onDismiss = { showDialog = 0 },
                onSave = { fileName, category ->
                    showDialog = 0
                    renameFile(fileName, recentSavedFileUri, isAllowed, selectedCategory, category)
                }
            )
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
            Scaffold(
                snackbarHost = { SnackbarHost(snackBarHostState) },
                bottomBar = {
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
                            onEditClick = { uri ->
                                selectedCategory = uri.second
                                recentSavedFileUri = uri.first
                                showDialog = 2
                            },
                            duplicateFile = { item ->
                                scope.launch {
                                    saveFile(
                                        getString(R.string.pro_scan_copy, getTodayDate()),
                                        item.first,
                                        isAllowed,
                                        item.second
                                    )
                                    snackBarHostState.showSnackbar(getString(R.string.file_copied_successfully))
                                }
                            },
                            askFileSaveLocation = { originalUri ->
                                if (isAllowed) {
                                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                                        type = "application/pdf"
                                        putExtra(Intent.EXTRA_TITLE, originalUri.lastPathSegment)
                                        addCategory(Intent.CATEGORY_OPENABLE)
                                    }
                                    originalFile = originalUri
                                    saveFileToSelectedLocLauncher.launch(intent)
                                    true
                                } else {
                                    false
                                }
                            },
                            saveFileAsImages = { image ->
                                if (isAllowed) {
                                    scope.launch {
                                        snackBarHostState.showSnackbar(getString(R.string.saving_images))
                                        val count = viewModel.saveFileAsImages(image)
                                        if (count == 0) snackBarHostState.showSnackbar(getString(R.string.failed_to_save_images_try_again))
                                        else snackBarHostState.showSnackbar("$count ${if (count == 1) "Image" else "Images"} saved.")
                                    }
                                    true
                                } else false
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
                            this@MainActivity,
                            viewModel,
                            innerPadding,
                            getVersionName(this@MainActivity),
                            isAllowed,
                            askPermission = { askPermission(this@MainActivity) }
                        )
                    }
                }
            }
        }
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
                        painter = painterResource(id = R.drawable.folder),
                        contentDescription = stringResource(R.string.folder)
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
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = stringResource(R.string.scan)
                        )
                        Text(
                            text = stringResource(R.string.scan_cap)
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
                        painter = painterResource(id = R.drawable.settings),
                        contentDescription = stringResource(R.string.settings)
                    )
                }
            }
        })
    }

    @Composable
    fun SaveFileDialog(
        isNewFile: Boolean,
        categoryList: List<String>,
        oldFileName: String,
        category: String,
        onDismiss: () -> Unit,
        onSave: (String, String) -> Unit
    ) {
        CustomDialog(onDismissRequest = onDismiss) {
            InputFilename(
                categoryList = categoryList,
                oldFileName = oldFileName,
                category = category,
                onNegativeClick = { fileName, category ->
                    if (isNewFile)
                        onSave(fileName, category)
                    else onDismiss()
                },
                onPositiveClick = { fileName, category ->
                    onSave(fileName, category)
                }
            )
        }
    }

    private fun renameFile(
        newFileName: String,
        uri: Uri,
        isAllowed: Boolean,
        oldCategory: String,
        category: String
    ) {
        viewModel.removeDocument(uri, oldCategory)
        val file = if (isAllowed) {
            renameAndMoveFile(
                checkAndCreateExternalParentDir(),
                newFileName,
                uri.toFile(),
                category
            )
        } else {
            renameAndMoveFile(
                checkAndCreateInternalParentDir(this),
                newFileName,
                uri.toFile(),
                category
            )
        }
        viewModel.addDocument(file, category)
    }

    private fun saveFile(fileName: String, uri: Uri, isAllowed: Boolean, category: String) {
        val file = if (isAllowed) {
            saveFileInDirectory(
                checkAndCreateExternalParentDir(),
                fileName,
                uri.toFile(),
                category
            )
        } else {
            saveFileInDirectory(
                checkAndCreateInternalParentDir(this@MainActivity),
                fileName,
                uri.toFile(),
                category
            )
        }
        if (file != null)
            viewModel.addDocument(file, category)
        else Toast.makeText(this, getString(R.string.file_already_exists), Toast.LENGTH_SHORT)
            .show()
    }
}
