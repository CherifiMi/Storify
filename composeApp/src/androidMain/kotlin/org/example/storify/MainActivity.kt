package org.example.storify


import SplashScreen
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.di.appModule
import core.theme.RPTSTheme
import org.koin.android.ext.koin.androidLogger
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import org.koin.dsl.binds
import org.koin.dsl.module
import storify.AppEvent
import storify.MainViewModel
import data.Strings.localized
import storify.components.AddItemDialog
import storify.components.ItemGrid
import storify.components.SearchBar
import storify.components.SideBar


class MyApp() : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            module {
                single { this@MyApp } binds arrayOf(Context::class, Application::class)
            }
            modules(appModule)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidApp()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun AndroidApp(viewModel: MainViewModel = koinInject()) {
    val state = viewModel.state.value

    val layoutDirection = if (state.lang == "ar") {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    val swipeableState = rememberSwipeableState(initialValue = 0)
    val sidebarWidthPx = with(LocalDensity.current) { 300.dp.toPx() }
    val anchors = mapOf(0f to 0, sidebarWidthPx to 1)
    val isSidebarVisible by remember { derivedStateOf { swipeableState.offset.value > sidebarWidthPx / 2 } }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        RPTSTheme {
            Box(
                Modifier
                    .fillMaxSize()
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        orientation = Orientation.Horizontal,
                        thresholds = { _, _ -> FractionalThreshold(0.5f) }
                    )
            ) {
                Row(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                    Column(Modifier.fillMaxSize()) {
                        SearchBar(show = false)
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Storify".localized,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colors.onBackground
                            )
                            Box(
                                modifier = Modifier.padding(top = 16.dp).fillMaxSize()
                                    .background(MaterialTheme.colors.surface).padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = SpaceBetween
                                ) {
                                    ItemGrid()
                                }
                            }
                        }

                    }
                }





                AnimatedVisibility(
                    visible = isSidebarVisible,
                    enter = slideInHorizontally(initialOffsetX = { it }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    SideBar()
                }
                if (state.showAddItem) {
                    AddItemDialog(
                        onDismiss = { viewModel.onEvent(AppEvent.ShowAddItem(false)) },
                        onSave = { item ->
                            viewModel.onEvent(AppEvent.AddItem(item))
                        }
                    )
                }
                if (state.showSplashScreen) {
                    SplashScreen()
                }
            }
        }
    }
}
