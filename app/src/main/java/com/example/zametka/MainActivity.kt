package com.example.zametka

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import com.example.zametka.UserStore.Companion.dataStore
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : ComponentActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {


            val systemUiController = rememberSystemUiController()
                // status bar

                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = Color.Black,
                        darkIcons = false
                    )
                }


                GlavScreen()
        }
    }
}

@Composable
fun GlavScreen() {
    val context = LocalContext.current
    val store = UserStore(context)


    var opis by remember { mutableStateOf("") }
    val zametkiValue by store.getAccessToken_1.collectAsState(initial = emptyList())

    var open_dialog by remember { mutableStateOf(false) }
    val updatedList = zametkiValue.toMutableList()

    var openThemeValue by remember {
        mutableStateOf(store.openThemeValue)
    }

    LaunchedEffect(openThemeValue) {
        suspendCoroutine<Unit> { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                store.updateOpenThemeValue(openThemeValue)
                continuation.resume(Unit)
            }
        }
    }



    Box {

        Column(
            Modifier
                .fillMaxSize()
                .background(if (openThemeValue) Color(0xFFFFFFFF) else Color(0xFF171717))
        ) {
            TopAppBar(
                backgroundColor = if (openThemeValue) Color(0xFFCEC9CE) else Color(0xFF292929),
                modifier = Modifier.fillMaxWidth(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(start = 16.dp)

                ) {
                    Text(
                        text = "Заметки",
                        color = if (openThemeValue) Color.Black else Color.White,
                        fontWeight = FontWeight.W500,
                        fontSize = 20.sp,
                        modifier = Modifier.clickable {

                        }
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxWidth(1f)
//  Toast.makeText(context, "Перезагрузите приложение, чтобы поменялась тема", Toast.LENGTH_SHORT).show()
                ) {
                    IconToggleButton(
                        checked = openThemeValue,
                        onCheckedChange = { newValue ->
                            openThemeValue = newValue
                            // Сохранение значения в DataStore
                            CoroutineScope(Dispatchers.IO).launch {
                                store.updateOpenThemeValue(newValue)
                            }


                        }
                    ) {
                        if (openThemeValue) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_wb_sunny_24),
                                contentDescription = "",
                                tint = Color.Black
                            )


                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_nightlight_24),
                                contentDescription = "",
                                tint = Color.White
                            )


                        }
                    }

                }

            }



            if (updatedList.isEmpty()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "У вас нет заметок",
                        textAlign = TextAlign.Center,
                        fontSize = 25.sp,
                        color = if (openThemeValue) Color.Black else Color.White,
                    )
                }

            } else {

            }



            LazyColumn() {
                items(zametkiValue) { zametka ->
                    val start = SwipeAction(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_edit_24),
                                contentDescription = "",
                                tint = Color.White
                            )
                        },
                        background = Color(0xFF44B94A),
                        onSwipe = { /* тут написать реализацию редактирование элемента*/ }
                    )
                    val end = SwipeAction(
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_delete_24),
                                contentDescription = "",
                                tint = Color.White
                            )
                        },
                        background = Color(0xFFC43F3F),
                        onSwipe = {
                            CoroutineScope(Dispatchers.IO).launch {

                                updatedList.remove(zametka)

                                if (updatedList.isEmpty()) {
                                    store.clearToken_1() // Очистить значение в DataStore
                                } else {
                                    store.saveToken_1(updatedList) // Обновить значение в DataStore
                                }

                            }
                        }
                    )
                    // Иницализация  подсказки
                    val build = rememberBalloonBuilder {
                        setArrowSize(10)
                        setArrowPosition(0.5f)
                        setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                        setWidth(BalloonSizeSpec.WRAP)
                        setHeight(BalloonSizeSpec.WRAP)
                        setPadding(12)
                        setMarginHorizontal(80)
                        setCornerRadius(10f)
                        setBackgroundColorResource(R.color.purple_200)
                        setBalloonAnimation(BalloonAnimation.ELASTIC)
                        setAutoDismissDuration(1800L)
                        setArrowOrientation(ArrowOrientation.TOP)

                    }
                    var open_todo by remember {
                        mutableStateOf(false)
                    }
                    // сохранение
                    build.setPreferenceName("long")
                    build.setShowCounts(1)


                    SwipeableActionsBox(
                        startActions = listOf(start),
                        endActions = listOf(end),

                        ) {
                        if (zametka.length >= 100) {

                            Balloon(
                                builder = build,
                                balloonContent = {
                                    Text(text = "Нажми чтобы развернуть!")
                                },
                            ) { balloonWindows ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(1f)
                                        .padding(10.dp)
                                        .clickable {
                                            open_todo = !open_todo
                                            /* CoroutineScope(Dispatchers.IO).launch {
                                                balloonWindows.awaitAlignBottom()
                                            }*/
                                        },
                                    backgroundColor = if (openThemeValue) Color(0xFFF7F6F8) else Color(0xFF2f2f2f)
                                ) {
                                    if (zametka.length >= 100) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            balloonWindows.awaitAlignBottom()
                                        }
                                    }


                                    Text(
                                        text =
                                        if (!open_todo) {
                                            zametka.take(99).plus("......")
                                        } else {
                                            zametka
                                        },
                                        modifier = Modifier
                                            .padding(10.dp),
                                        color = if (openThemeValue) Color.Black else Color(0xFFE0D8D8)
                                    )
                                }
                            }

                        } else {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .padding(10.dp),
                                backgroundColor = if (openThemeValue) Color(0xFFF7F6F8) else Color(0xFF2f2f2f)
                            ) {
                                Text(
                                    text = zametka,
                                    modifier = Modifier
                                        .padding(10.dp),
                                    color = if (openThemeValue) Color.Black else Color(0xFFE0D8D8)
                                )
                            }
                        }

                    }
                }
            }
        }

        val builder = rememberBalloonBuilder {
            setArrowSize(10)
            setArrowPosition(1f)
            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            setWidth(BalloonSizeSpec.WRAP)
            setHeight(BalloonSizeSpec.WRAP)
            setPadding(12)
            setMarginHorizontal(80)
            setCornerRadius(10f)
            setBackgroundColorResource(R.color.purple_200)
            setBalloonAnimation(BalloonAnimation.ELASTIC)
            setAutoDismissDuration(1500L)
            setArrowOrientation(ArrowOrientation.END)
        }
        builder.setPreferenceName("plus")
        builder.setShowCounts(1)
        builder.runIfReachedShowCounts {
            open_dialog = true
        }

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)

        ) {
            Balloon(
                modifier = Modifier.align(Alignment.End),

                builder = builder,
                balloonContent = {
                    Text(text = "Сделай заметку!")
                },
            ) { balloonWindow ->

                FloatingActionButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            balloonWindow.awaitAlignEnd()
                        }
                    },
                    backgroundColor = if (openThemeValue) Color(0xFFBEBEBB) else Color(0xFFf2bf30)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_24),
                        contentDescription = "",
                        tint = if (openThemeValue) Color.White else Color.White
                    )
                }
            }
        }

        if (open_dialog) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val focusRequester = remember { FocusRequester() }
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                    TextField(
                        value = opis,
                        onValueChange = { opis = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (openThemeValue) Color(0xFFFFFFFF) else Color(
                                    0xFF171717
                                )
                            )
                            .focusRequester(focusRequester),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        colors = TextFieldDefaults.textFieldColors(if (openThemeValue) Color.Black else Color.White)
                    )

                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Button(
                            onClick = {
                                if (opis.isNotEmpty()) {
                                    val newZametka = "$opis"
                                    val updatedZametki = zametkiValue + listOf(newZametka)
                                    opis = ""
                                    open_dialog = false
                                    CoroutineScope(Dispatchers.IO).launch {
                                        store.saveToken_1(updatedZametki)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(16.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                if (openThemeValue) Color(0xFFBEBEBB) else Color(
                                    0xFFf2bf30
                                )
                            )
                        ) {
                            Text(
                                text = "Добавить",
                                color = if (openThemeValue) Color.Black else Color.White
                            )

                        }
                    }
                }
            }
        }
    }
}


