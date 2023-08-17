package io.ecosed.libecosed.ui.layout

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.twotone.OpenInNew
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import io.ecosed.libecosed.R
import io.ecosed.libecosed.ui.navigation.ItemType
import io.ecosed.libecosed.ui.navigation.ScreenType
import io.ecosed.libecosed.ui.preview.ScreenPreviews
import io.ecosed.libecosed.ui.screen.Screen
import io.ecosed.libecosed.ui.theme.LibEcosedTheme
import io.ecosed.libecosed.ui.window.ContentType
import io.ecosed.libecosed.ui.window.DevicePosture
import io.ecosed.libecosed.ui.window.NavigationType
import io.ecosed.libecosed.ui.window.isBookPosture
import io.ecosed.libecosed.ui.window.isSeparating
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ActivityMain(
    windowSize: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    subNavController: NavController,
    configuration: AppBarConfiguration,
    container: FragmentContainerView,
    flutter: FrameLayout,
    appsUpdate: (RecyclerView) -> Unit,
    topBarVisible: Boolean,
    topBarUpdate: (MaterialToolbar) -> Unit,
    preferenceUpdate: (ViewPager2) -> Unit,
    androidVersion: String,
    shizukuVersion: String,
    current: (item: Int) -> Unit,
    toggle: () -> Unit,
    taskbar: () -> Unit
) {
    val pages = listOf(
        Screen.ComposeTitle,
        Screen.Overview,
        Screen.Container,
        //   Screen.Apps,
        Screen.Flutter,
        //       Screen.Manager,
        Screen.Settings,
        Screen.Divider,
        //Screen.Preference,
        Screen.About,
        Screen.FragmentTitle,
    )
    val items = listOf(
        Screen.Overview,
        Screen.Container,
        //Screen.Apps,
        Screen.Flutter,
        //     Screen.Manager,
        Screen.Settings
    )

    val navControllerCompose: NavHostController = rememberNavController()

    val scope = rememberCoroutineScope()
    val navBackStackEntry by navControllerCompose.currentBackStackEntryAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val currentDestination = navBackStackEntry?.destination
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val drawerState: DrawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val navigationType: NavigationType
    val contentType: ContentType

    val foldingFeature =
        displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()

    val foldingDevicePosture = when {
        isBookPosture(
            foldFeature = foldingFeature
        ) -> DevicePosture.BookPosture(
            hingePosition = foldingFeature.bounds
        )

        isSeparating(
            foldFeature = foldingFeature
        ) -> DevicePosture.Separating(
            hingePosition = foldingFeature.bounds,
            orientation = foldingFeature.orientation
        )

        else -> DevicePosture.NormalPosture
    }

    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            navigationType = NavigationType.BottomNavigation
            contentType = ContentType.Single
        }

        WindowWidthSizeClass.Medium -> {
            navigationType = NavigationType.NavigationRail
            contentType = if (foldingDevicePosture != DevicePosture.NormalPosture) {
                ContentType.Dual
            } else {
                ContentType.Single
            }
        }

        WindowWidthSizeClass.Expanded -> {
            navigationType = if (foldingDevicePosture is DevicePosture.BookPosture) {
                NavigationType.NavigationRail
            } else {
                NavigationType.PermanentNavigationDrawer
            }
            contentType = ContentType.Dual
        }

        else -> {
            navigationType = NavigationType.BottomNavigation
            contentType = ContentType.Single
        }
    }

    @Composable
    fun nav() {
        Column {
            // 头布局
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            all = 16.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.lib_description
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    AnimatedVisibility(
                        visible = navigationType != NavigationType.PermanentNavigationDrawer
                    ) {
                        IconButton(
                            onClick = {
                                if (
                                    navigationType != NavigationType.PermanentNavigationDrawer
                                ) {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuOpen,
                                contentDescription = null
                            )
                        }
                    }
                }
                ExtendedFloatingActionButton(
                    text = {
                        Text(text = "应用名称")
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.TwoTone.OpenInNew,
                            contentDescription = null
                        )
                    },
                    onClick = {

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 16.dp
                        )
                        .padding(
                            paddingValues = NavigationDrawerItemDefaults.ItemPadding
                        )
                )
            }
            // 导航列表
            Column(
                modifier = Modifier.verticalScroll(
                    state = rememberScrollState()
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(
                    modifier = Modifier.height(
                        height = 12.dp
                    )
                )
                pages.forEach { item ->
                    when (item.item) {
                        ItemType.Default -> NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = item.imageVector,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(
                                        id = item.title
                                    )
                                )
                            },
                            selected = currentDestination?.hierarchy?.any {
                                it.route == item.route
                            } == true,
                            onClick = {
                                when (item.type) {
                                    ScreenType.Compose -> navControllerCompose.navigate(
                                        route = item.route
                                    ) {
                                        popUpTo(
                                            id = navControllerCompose.graph.findStartDestination().id
                                        ) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }.also {
                                        if (
                                            navigationType != NavigationType.PermanentNavigationDrawer
                                        ) {
                                            scope.launch {
                                                drawerState.close()
                                            }
                                        }
                                    }

                                    ScreenType.Fragment -> current(
                                        item.route.toInt()
                                    ).also {
                                        navControllerCompose.navigate(
                                            route = Screen.Flutter.route
                                        ) {
                                            popUpTo(
                                                id = navControllerCompose.graph.findStartDestination().id
                                            ) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }.also {
                                            if (
                                                navigationType != NavigationType.PermanentNavigationDrawer
                                            ) {
                                                scope.launch {
                                                    drawerState.close()
                                                }
                                            }
                                        }
                                    }

                                    ScreenType.Title -> if (
                                        navigationType != NavigationType.PermanentNavigationDrawer
                                    ) {
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    }

                                    ScreenType.Divider -> if (
                                        navigationType != NavigationType.PermanentNavigationDrawer
                                    ) {
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.padding(
                                paddingValues = NavigationDrawerItemDefaults.ItemPadding
                            )
                        )

                        ItemType.Title -> Text(
                            text = stringResource(
                                id = item.title
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    paddingValues = NavigationDrawerItemDefaults.ItemPadding
                                )
                                .padding(
                                    vertical = 5.dp
                                ),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.titleMedium
                        )

                        ItemType.Divider -> Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    paddingValues = NavigationDrawerItemDefaults.ItemPadding
                                )
                                .padding(
                                    vertical = 5.dp
                                )
                        )
                    }
                }
                Spacer(
                    modifier = Modifier.height(
                        height = 12.dp
                    )
                )
            }
        }
    }

    @Composable
    fun content() {


        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AnimatedVisibility(
                    visible = true
                ) {
                    LargeTopAppBar(
                        title = {
                            Text(
                                text = stringResource(
                                    id = R.string.lib_name
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        navigationIcon = {
                            AnimatedVisibility(
                                visible = navigationType != NavigationType.PermanentNavigationDrawer
                            ) {
                                IconButton(
                                    onClick = {
                                        if (
                                            navigationType != NavigationType.PermanentNavigationDrawer
                                        ) {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    navControllerCompose.navigate(
                                        route = Screen.Manager.route
                                    ) {
                                        popUpTo(
                                            id = navControllerCompose.graph.findStartDestination().id
                                        ) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = null
                                )
                            }
                        },
                        colors = TopAppBarDefaults.largeTopAppBarColors(),
                        scrollBehavior = scrollBehavior
                    )
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = (navigationType == NavigationType.BottomNavigation)
                ) {
                    NavigationBar(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items.forEach { item ->
                            NavigationBarItem(
                                selected = currentDestination?.hierarchy?.any {
                                    it.route == item.route
                                } == true,
                                onClick = {
                                    if (item.type == ScreenType.Compose) navControllerCompose.navigate(
                                        route = item.route
                                    ) {
                                        popUpTo(
                                            id = navControllerCompose.graph.findStartDestination().id
                                        ) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.imageVector,
                                        contentDescription = null
                                    )
                                },
                                enabled = true,
                                label = {
                                    Text(
                                        stringResource(
                                            id = item.title
                                        )
                                    )
                                },
                                alwaysShowLabel = true
                            )
                        }
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState
                )
            },
//            floatingActionButton = {
//                FloatingActionButton(onClick = { /*TODO*/ }) {
//                    Icon(
//                        imageVector = Icons.Outlined.Category,
//                        contentDescription = null
//                    )
//
//
//                }
//            },
//            floatingActionButtonPosition = FabPosition.End,
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        paddingValues = innerPadding
                    )
            ) {
                AnimatedVisibility(
                    visible = navigationType == NavigationType.NavigationRail
                ) {
                    NavigationRail(
                        modifier = Modifier.fillMaxHeight(),
                        header = {
                            FloatingActionButton(
                                onClick = {

                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Terminal,
                                    contentDescription = null
                                )
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier.verticalScroll(
                                state = rememberScrollState()
                            )
                        ) {
                            items.forEach { item ->
                                NavigationRailItem(
                                    selected = currentDestination?.hierarchy?.any {
                                        it.route == item.route
                                    } == true,
                                    onClick = {
                                        if (item.type == ScreenType.Compose) navControllerCompose.navigate(
                                            route = item.route
                                        ) {
                                            popUpTo(
                                                id = navControllerCompose.graph.findStartDestination().id
                                            ) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = item.imageVector,
                                            contentDescription = null
                                        )
                                    },
                                    enabled = true,
                                    label = {
                                        Text(
                                            stringResource(
                                                id = item.title
                                            )
                                        )
                                    },
                                    alwaysShowLabel = false
                                )
                            }
                        }
                    }
                }
                NavHost(
                    navController = navControllerCompose,
                    startDestination = Screen.Overview.route,
                    modifier = when (navigationType) {
                        NavigationType.PermanentNavigationDrawer -> Modifier.fillMaxSize()
                        else -> Modifier
                            .fillMaxSize()
                            .nestedScroll(
                                connection = scrollBehavior.nestedScrollConnection
                            )
                    }
                ) {
                    composable(
                        route = Screen.Overview.route
                    ) {
                        ScreenOverview(
                            subNavController = subNavController,
                            configuration = configuration,
                            topBarVisible = topBarVisible,
                            topBarUpdate = topBarUpdate,
                            navController = navControllerCompose,
                            shizukuVersion = shizukuVersion
                        )
                    }
                    composable(
                        route = Screen.Container.route
                    ) {
                        ScreenContainer(
                            subNavController = subNavController,
                            configuration = configuration,
                            topBarVisible = topBarVisible,
                            topBarUpdate = topBarUpdate,
                            container = container
                        )
                    }
                    composable(
                        route = Screen.Apps.route
                    ) {
                        ScreenApps(appsUpdate = appsUpdate)
                    }
                    composable(
                        route = Screen.Flutter.route
                    ) {
                        ScreenFlutter(
                            rootLayout = flutter,
                            search = {},
                            subNavController = subNavController
                        )
                    }
                    composable(
                        route = Screen.Manager.route
                    ) {
                        ScreenManager(
                            navController = navControllerCompose,
                            toggle = toggle,
                            current = current,
                            targetAppName = stringResource(id = R.string.lib_name),
                            targetAppPackageName = "BuildConfig.APPLICATION_ID",
                            targetAppDescription = stringResource(id = R.string.lib_name),
                            targetAppVersionName = "BuildConfig.VERSION_NAME",
                            NavigationOnClick = {},
                            MenuOnClick = {},
                            SearchOnClick = {},
                            SheetOnClick = {},
                            AppsOnClick = {},
                            SelectOnClick = {},
                            onNavigateToApps = {}
                        )
                    }
                    composable(
                        route = Screen.Settings.route
                    ) {
                        ScreenSettings(
                            navControllerCompose = navControllerCompose,
                            navControllerFragment = subNavController,
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            current = current,
                            onTaskBarSettings = taskbar,
                            onSystemSettings = {},
                            onDefaultLauncherSettings = {}
                        )
                    }
//                    composable(
//                        route = Screen.Preference.route
//                    ) {
//                        ScreenPreference(
//                            preferenceUpdate = preferenceUpdate
//                        )
//                    }
                    composable(
                        route = Screen.About.route
                    ) {
                        ScreenAbout(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            onEasterEgg = {},
                            onNotice = {},
                            onSource = {},
                            onDevGitHub = {},
                            onDevTwitter = {},
                            onTeamGitHub = {}
                        )
                    }
                }

            }
        }
    }

    when (navigationType) {
        NavigationType.PermanentNavigationDrawer -> PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet {
                    nav()
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            content()
        }

        NavigationType.NavigationRail,
        NavigationType.BottomNavigation -> ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    nav()
                }
            },
            modifier = Modifier.fillMaxSize(),
            drawerState = drawerState,
            gesturesEnabled = true
        ) {
            content()
        }
    }
}

@ScreenPreviews
@Composable
internal fun ActivityMainPreview() {
    LibEcosedTheme {

    }
}