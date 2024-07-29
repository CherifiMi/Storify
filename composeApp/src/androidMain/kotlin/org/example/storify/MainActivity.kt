package org.example.storify


import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import storify.components.SplashScreen
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import core.di.appModule
import core.theme.RPTSTheme
import org.koin.android.ext.koin.androidLogger
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import org.koin.dsl.binds
import org.koin.dsl.module
import storify.AppEvent
import storify.MainViewModel
import core.model.Strings.localized
import data.MongoDBService.getItems
import domain.model.Item
import getFilePath
import kotlinx.coroutines.runBlocking
import storify.components.AddItemDialog
import storify.components.EditItemDialog
import storify.components.ItemGrid
import storify.components.SearchBar
import storify.components.SideBar
import storify.saveAppState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MyApp() : Application() {
    private val service = Executors.newSingleThreadScheduledExecutor()
    private val handler = Handler(Looper.getMainLooper())

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            module {
                single { this@MyApp } binds arrayOf(Context::class, Application::class)
            }
            modules(appModule)
        }

        service.scheduleWithFixedDelay({
            handler.post {
                Log.d("MITOTEST", "work manager stuff")
                val items = runBlocking { getItems() }
                checkExpiringItems(items, this)
            }
        }, 0, 12, TimeUnit.HOURS)


    }
}

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureAmplify()

        if (ActivityCompat.checkSelfPermission(
                this,
                POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(this, arrayOf(POST_NOTIFICATIONS), 0)
        }

        setContent {
            AndroidApp()
        }
    }

    private fun configureAmplify() {
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(applicationContext)

            Log.i("MITOTEST", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("MITOTEST", "Could not initialize Amplify", error)
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@org.jetbrains.compose.ui.tooling.preview.Preview
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

    LaunchedEffect(state) {
        println("done")
        saveAppState(state, getFilePath("state.json"))
    }


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
                Row(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                ) {
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
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .fillMaxSize()
                                    .background(MaterialTheme.colors.surface)
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = SpaceBetween
                                ) {
                                    ItemGrid(columns = 2)
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
                if (state.showEditItem) {
                    EditItemDialog(
                        onDismiss = { viewModel.onEvent(AppEvent.ShowEditItem(false)) },
                        onEdit = { item ->
                            viewModel.onEvent(AppEvent.EditItem(item))
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
///////////////////////////

@RequiresApi(Build.VERSION_CODES.O)
fun parseDate(dateStr: String): LocalDate? {
    val formatters = listOf(
        DateTimeFormatter.ofPattern("dd,MM,yyyy"),
        DateTimeFormatter.ofPattern("d,MM,yyyy"),
        DateTimeFormatter.ofPattern("dd,M,yyyy"),
        DateTimeFormatter.ofPattern("d,M,yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("d/MM/yyyy"),
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("dd/M/yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("d-MM-yyyy")
    )

    for (formatter in formatters) {
        try {
            return LocalDate.parse(dateStr, formatter)
        } catch (e: DateTimeParseException) {
            // Continue to the next formatter
        }
    }
    return null
}

@RequiresApi(Build.VERSION_CODES.O)
fun checkExpiringItems(items: List<Item>, context: Context) {
    val today = LocalDate.now()
    val weekLater = today.plusWeeks(1)

    for (item in items) {
        val expirationDate = parseDate(item.expirationDate)
        if (expirationDate != null && expirationDate.isBefore(weekLater)) {
            sendNotification(context, item)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun parseD(dateStr: String): LocalDate? {
    val formatters = listOf(
        DateTimeFormatter.ofPattern("dd,MM,yyyy"),
        DateTimeFormatter.ofPattern("d,MM,yyyy"),
        DateTimeFormatter.ofPattern("dd,M,yyyy"),
        DateTimeFormatter.ofPattern("d,M,yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("d/MM/yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("d-MM-yyyy")
    )

    for (formatter in formatters) {
        try {
            return LocalDate.parse(dateStr, formatter)
        } catch (e: DateTimeParseException) {
            // Continue to the next formatter
        }
    }
    return null
}

@RequiresApi(Build.VERSION_CODES.O)
fun sendNotification(context: Context, item: Item) {
    val expirationDate = parseD(item.expirationDate)
    if (expirationDate != null) {
        val today = LocalDate.now()
        val daysUntilExpiration = ChronoUnit.DAYS.between(today, expirationDate)

        val message = when {
            daysUntilExpiration > 0 -> {
                "${item.name} will expire in $daysUntilExpiration days."
            }

            daysUntilExpiration.toInt() == 0 -> {
                "${item.name} expires today!"
            }

            else -> {
                "${item.name} expired ${-daysUntilExpiration} days ago."
            }
        }

        // Log the details
        Log.d("MITOTEST", message)

        // Show notification
        showNotification(context, "Item Expiration", message)
    }
}

@SuppressLint("MissingPermission")
fun showNotification(context: Context, title: String, message: String) {
    val channelId = "expiration_channel"
    val notificationId = System.currentTimeMillis().toInt()

    // Create notification channel (for API 26+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Expiration Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for item expiration alerts"
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Create the notification
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    // Show the notification
    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, builder.build())
    }
}
