package com.example.zametka

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.zametka.UserStore.Companion.dataStore
import com.google.accompanist.systemuicontroller.SystemUiController
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import org.json.JSONObject
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GlavScreen(context: Context, store: UserStore, systemUiController: SystemUiController) {

    var opis by remember { mutableStateOf("") }
    val zametkiValue by store.getAccessToken_1.collectAsState(initial = emptyList())
    val updatedList = zametkiValue.toMutableList()

    var textFieldList = remember { mutableStateListOf("") }
    val zadachiValue by store.getAccessToken_2.collectAsState(initial = emptyList())
    var zadachiUpdateList = zadachiValue.toMutableList()
    var open_dialog by remember { mutableStateOf(false) }
    var openThemeValue by remember { mutableStateOf(store.openThemeValue) }
    var open_zadacha by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    LaunchedEffect(openThemeValue) {
        suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                store.updateOpenThemeValue(openThemeValue)
                continuation.resume(Unit)
            }
        }
    }
    SideEffect {
        systemUiController.setStatusBarColor(
            color = if (openThemeValue) Color(0xFFCEC9CE) else Color(0xFF292929),
            darkIcons = false
        )
    }

    val newZadacha = textFieldList.joinToString(",")
    val updatedZadacha = zadachiValue + listOf(newZadacha)


    var temperature by remember { mutableStateOf("") }
    val apiKey = ""
    val apiUrl = "https://api.weatherapi.com/v1/current.json?key=$apiKey"
    val contexting = LocalContext.current as? Activity

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {

            val locationManager =
                contexting?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val location = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            val lat = location?.latitude.toString()
            val lon = location?.longitude.toString()
            val url = "$apiUrl&q=$lat,$lon&aqi=no"

            CoroutineScope(Dispatchers.IO).launch {
                try {

                    val urlObject = URL(url)
                    val response = urlObject.readText()
                    val json = JSONObject(response)
                    val temp = json.getJSONObject("current").getString("temp_c")
                    temperature = temp

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        onDispose { }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(7),
        sheetBackgroundColor = if (openThemeValue) Color(0xFFCEC9CE) else Color(0xFF292929),
        sheetContent = {
            Column(
                Modifier
                    .padding(20.dp)
                    .fillMaxWidth(1f)
            ) {
                Text(
                    text = "Создать заметку",
                    fontSize = 23.sp,
                    modifier = Modifier
                        .clickable {
                            open_dialog = true
                            scope.launch {
                                sheetState.hide()
                            }

                        },
                    color = if (openThemeValue) Color(0xFF292929) else Color(0xFFCEC9CE),
                )
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Divider(
                    color = if (openThemeValue) Color(0xFF292929) else Color(0xFF555255)
                )

                Text(
                    text = "Создать список задач",
                    fontSize = 23.sp,
                    modifier = Modifier
                        .clickable {
                            if (zadachiUpdateList.isEmpty()) {
                                open_zadacha = true

                            } else {
                                Toast.makeText(
                                    context,
                                    "Сначало закончите ваши прошлые задачи",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            scope.launch {
                                sheetState.hide()
                            }
                        },
                    color = if (openThemeValue) Color(0xFF292929) else Color(0xFFCEC9CE),

                    )
            }
        }
    ) {
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
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.fillMaxWidth(1f)
                    ) {
                        Row {
                            Text(
                                text = "$temperature °C ",
                                color = if (openThemeValue) Color.Black else Color.White,
                                fontWeight = FontWeight.W900,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(top = 10.dp, end = 5.dp)
                            )
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
                }


                LazyColumn() {
                    items(1) {
                        Card(
                            shape = RoundedCornerShape(5),
                            backgroundColor = if (openThemeValue) Color(0xFFF7F6F8) else Color(
                                0xFF2f2f2f
                            ),
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(10.dp),
                        ) {
                            Column(Modifier.fillMaxWidth(1f)) {
                                var numberSort = 0

                                var showDialog by remember { mutableStateOf(false) }

                                var checkCount by remember { mutableIntStateOf(store.checkCount) }

                                LaunchedEffect(checkCount) {
                                    suspendCoroutine { continuation ->
                                        CoroutineScope(Dispatchers.IO).launch {
                                            store.updatecheckCount(checkCount)
                                            continuation.resume(Unit)
                                        }
                                    }
                                }
                                for (zadacha in zadachiUpdateList) {
                                    val checkboxKey =
                                        remember(zadacha) { booleanPreferencesKey(zadacha) }
                                    val isChecked = context.dataStore.data.map { preferences ->
                                        preferences[checkboxKey] ?: false
                                    }.collectAsState(initial = false)

                                    Row() {
                                        Column(
                                            horizontalAlignment = Alignment.Start,
                                        ) {
                                            Row {
                                                Checkbox(
                                                    checked = isChecked.value,
                                                    onCheckedChange = { newCheckedState ->
                                                        CoroutineScope(Dispatchers.IO).launch {
                                                            context.dataStore.edit { preferences ->
                                                                preferences[checkboxKey] =
                                                                    newCheckedState
                                                            }
                                                        }
                                                        if (newCheckedState) {
                                                            CoroutineScope(Dispatchers.IO).launch {
                                                                store.updatecheckCount(store.checkCount + 1)
                                                            }
                                                        } else {
                                                            CoroutineScope(Dispatchers.IO).launch {
                                                                store.updatecheckCount(store.checkCount - 1)
                                                            }
                                                        }
                                                    },
                                                    colors = CheckboxDefaults.colors(
                                                        Color(
                                                            0xFF4F863F
                                                        )
                                                    )
                                                )


                                                Text(
                                                    text = "${numberSort + 1}. $zadacha",
                                                    fontSize = 20.sp,
                                                    color =
                                                    if (!openThemeValue && isChecked.value) {
                                                        Color(0xFF378108)
                                                    } else if (openThemeValue && isChecked.value) {
                                                        Color(0xFF378108)
                                                    } else if (openThemeValue) {
                                                        Color.Black
                                                    } else {
                                                        Color.White
                                                    },


                                                    style = if (isChecked.value) {
                                                        TextStyle(textDecoration = TextDecoration.LineThrough)
                                                    } else {
                                                        TextStyle(textDecoration = TextDecoration.None)
                                                    },
                                                    modifier = Modifier.padding(top = 10.dp)

                                                )
                                            }
                                        }

                                        Column(
                                            horizontalAlignment = Alignment.End,
                                            modifier = Modifier
                                                .fillMaxWidth(1f)
                                        ) {

                                            IconButton(onClick = {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    store.saveToken_2(updatedZadacha)
                                                }
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    zadachiUpdateList.remove(zadacha)
                                                    if (zadachiUpdateList.isEmpty()) {
                                                        store.clearTasks() // Очистить значение в DataStore
                                                    } else {
                                                        store.saveToken_2(zadachiUpdateList) // Обновить значение в DataStore
                                                    }
                                                }
                                            }) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.baseline_delete_24),
                                                    contentDescription = "",
                                                    tint = if (openThemeValue) Color.Black else Color.White
                                                )
                                            }
                                        }

                                        numberSort += 1;

                                    }
                                    if (store.checkCount == zadachiUpdateList.size) {
                                        showDialog = true
                                    }

                                    if (showDialog) {
                                        // Отобразить диалоговое окно
                                        Dialog(
                                            onDismissRequest = { showDialog = false },
                                        ) {
                                            Card(
                                                backgroundColor = if (openThemeValue) Color(
                                                    0xFFF7F6F8
                                                ) else Color(0xFF2f2f2f),
                                                modifier = Modifier.size(300.dp, 200.dp)
                                            ) {
                                                Column(
                                                    verticalArrangement = Arrangement.Top,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Spacer(modifier = Modifier.padding(top = 50.dp))
                                                    Text(
                                                        text = "Вы выполнили все задачи!!",
                                                        color = if (openThemeValue) Color(0xFF2f2f2f) else Color(
                                                            0xFFF7F6F8
                                                        ),
                                                        fontSize = 19.sp
                                                    )

                                                }
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Bottom,
                                                    modifier = Modifier.fillMaxSize()
                                                ) {


                                                    Button(
                                                        onClick = {
                                                            CoroutineScope(Dispatchers.IO).launch {

                                                                store.updatecheckCount(0)
                                                                store.clearTasks()
                                                            }

                                                            zadachiUpdateList.removeAll(
                                                                zadachiUpdateList
                                                            )

                                                            showDialog = false

                                                        },
                                                        modifier = Modifier
                                                            .fillMaxWidth(1f)
                                                            .padding(20.dp)
                                                            .height(50.dp),
                                                        colors = ButtonDefaults.buttonColors(
                                                            if (openThemeValue) Color(0xFFBEBEBB) else Color(
                                                                0xFFf2bf30
                                                            )
                                                        )
                                                    ) {
                                                        Text(text = "Отлично ")
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }

                    items(zametkiValue) { zametka ->

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
                                            .animateContentSize()
                                            .clickable {
                                                open_todo = !open_todo
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    balloonWindows.awaitAlignBottom()
                                                }

                                            },
                                        backgroundColor = if (openThemeValue) Color(0xFFF7F6F8) else Color(
                                            0xFF2f2f2f
                                        ),
                                        shape = RoundedCornerShape(5),
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
                                            color = if (openThemeValue) Color.Black else Color(
                                                0xFFE0D8D8
                                            )
                                        )
                                    }
                                }

                            } else {

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(1f)
                                        .padding(10.dp),
                                    backgroundColor = if (openThemeValue) Color(0xFFF7F6F8) else Color(
                                        0xFF2f2f2f
                                    ),
                                    shape = RoundedCornerShape(5),
                                ) {
                                    Text(
                                        text = zametka,
                                        modifier = Modifier
                                            .padding(10.dp),
                                        color = if (openThemeValue) Color.Black else Color(
                                            0xFFE0D8D8
                                        )
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
            builder.setPreferenceName("plu")
            builder.setShowCounts(1)
            builder.runIfReachedShowCounts {

                scope.launch {
                    sheetState.show()
                }
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
                        backgroundColor = if (openThemeValue) Color(0xFFBEBEBB) else Color(
                            0xFFf2bf30
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_add_24),
                            contentDescription = "",
                            tint = if (openThemeValue) Color.White else Color.White
                        )
                    }
                }
            }

            if (open_zadacha) {
                var enebled_button by remember {
                    mutableStateOf(true)
                }
                Card(
                    modifier = Modifier
                        .fillMaxSize(),

                    ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .background(if (openThemeValue) Color(0xFFFFFFFF) else Color(0xFF171717))
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 50.dp)

                    ) {
                        TopAppBar(
                            modifier = Modifier.fillMaxWidth(1f),
                            backgroundColor = (if (openThemeValue) Color(0xFFCEC9CE) else Color(
                                0xFF292929
                            ))
                        )
                        {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Задачи",
                                    color = if (openThemeValue) Color.Black else Color.White,
                                    fontSize = 20.sp
                                )
                            }


                        }
                        var textFieldDone = ImeAction.Next
                        for (i in textFieldList.indices) {


                            Spacer(modifier = Modifier.padding(top = 20.dp))
                            val textnumber = i + 1
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .fillMaxWidth(1f)
                            ) {
                                TextField(
                                    value = textFieldList[i],
                                    onValueChange = { textFieldList[i] = it },
                                    label = { Text(text = "Задача № $textnumber") },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20))
                                        .background(Color.White),
                                    shape = RoundedCornerShape(20),
                                    trailingIcon = {
                                        IconButton(onClick = { textFieldList.remove(textFieldList[i]) }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_delete_24),
                                                contentDescription = "",
                                                tint = if (openThemeValue) Color.Black else Color(
                                                    0xFFBEBEBB
                                                )
                                            )
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Sentences,
                                        autoCorrect = false,
                                        keyboardType = KeyboardType.Text,
                                        imeAction = textFieldDone
                                    ),
                                )
                                if (textFieldList.size == 10) {
                                    textFieldDone = ImeAction.Done
                                }
                                enebled_button = i != 10 - 1

                            }
                        }

                        Button(
                            onClick = {


                                textFieldList.add(TextFieldValue().text)
                            },
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(36.dp)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                if (openThemeValue) Color(0xFFBEBEBB) else Color(
                                    0xFFf2bf30
                                )
                            ),
                            enabled = enebled_button,

                            ) {
                            Text(
                                "Добавить Задачу",
                                color = if (openThemeValue) Color.White else Color.Black
                            )
                        }


                    }


                }

                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(
                        onClick = {
                            val allFieldsNotEmpty = textFieldList.all { it.isNotEmpty() }
                            if (allFieldsNotEmpty) {


                                open_zadacha = false
                                CoroutineScope(Dispatchers.IO).launch {
                                    store.saveToken_2(updatedZadacha)
                                }
                                textFieldList.clear()
                                textFieldList.add(TextFieldValue("").text)


                            } else {

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
                            color = if (openThemeValue) Color.White else Color.Black
                        )

                    }
                }
            }








            if (open_dialog) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column {


                        TopAppBar(
                            modifier = Modifier.fillMaxWidth(1f),
                            backgroundColor = (if (openThemeValue) Color(0xFFCEC9CE) else Color(
                                0xFF292929
                            ))
                        )
                        {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Заметки",
                                    color = if (openThemeValue) Color.Black else Color.White,
                                    fontSize = 20.sp
                                )
                            }


                        }
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
                                    imeAction = ImeAction.Default
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
                                            open_dialog = false
                                            val newZametka = opis
                                            val updatedZametki = zametkiValue + listOf(newZametka)
                                            opis = ""
                                            !open_dialog
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
    }
}