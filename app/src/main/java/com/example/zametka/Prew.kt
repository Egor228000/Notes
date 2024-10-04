package com.example.zametka

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Prew(navController: NavController, context: Context, store: UserStore) {

    var isLoggedIn by remember { mutableStateOf(store.isLoggedIn) }

        val pagerState = rememberPagerState(pageCount = {
            3
        })
    var openCircle by remember {
        mutableStateOf(false)
    }

    var open by remember {
        mutableStateOf(false)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->//Here, we get the Intent result
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            openCircle = true
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            Firebase.auth.signInWithCredential(credential)

                .addOnCompleteListener {

                    if (it.isSuccessful) {
                        openCircle = false
                        open = true

                        CoroutineScope(Dispatchers.IO).launch {
                            store.updateisLoggedIn(true)
                        }

                        Thread.sleep(1500)
                        if (isLoggedIn) {
                            navController.navigate("glav_screen") {
                                popUpTo("prew") { inclusive = true }
                            }
                        } else {

                        }


                        //Here, do whatever you want to do after successful auth
                    } else {
                        Toast.makeText(
                            context,
                            "Вы не вошли в аккаунт",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

        } catch (e: ApiException) {
            Log.e("TAG", "Google sign in failed", e)
            Toast.makeText(
                context,
                "Вы не вошли в аккаунт",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    val token =
        stringResource(R.string.defalt_web_id)
    LaunchedEffect(isLoggedIn) {
        suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                store.updateisLoggedIn(isLoggedIn)
                continuation.resume(Unit)
            }
        }
    }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) {page ->
            when(page) {
                 0->{
                 Column(
                     verticalArrangement = Arrangement.Center,
                     horizontalAlignment = Alignment.CenterHorizontally,
                         modifier = Modifier
                             .fillMaxSize()
                             .background(Color(0xFF292929))
                     ) {
                         Spacer(modifier = Modifier.padding(top = 50.dp))
                         Text(
                             text = "Добро пожаловать в нашем приложении!",
                             color = Color.White,
                             fontSize = 20.sp,
                             fontWeight = FontWeight.ExtraBold,
                             textAlign = TextAlign.Center
                         )
                         AsyncImage(
                             model = "https://firebasestorage.googleapis.com/v0/b/korzina-7fa09.appspot.com/o/raw%2FzsjyCBdesQXmyEFZN3AnBOrGEEOrdSDS12T8ROrX-preview-no-bg.png?alt=media&token=49486dd1-e426-4b76-bd61-1f49e1378ee3",
                             contentDescription = null,
                             modifier = Modifier.fillMaxSize(0.9f)
                         )
                     }


                 }
                 1->{
                     Column(
                         verticalArrangement = Arrangement.Center,
                         horizontalAlignment = Alignment.CenterHorizontally,

                         modifier = Modifier
                             .fillMaxSize()
                             .background(Color(0xFF292929))
                     ) {
                         Spacer(modifier = Modifier.padding(top = 50.dp))
                         Text(
                             text = "Это приложение созданно, чтобы вам было удобно записывать важные моменты вашей жизни и ваши цели!",
                             color = Color.White,
                             fontSize = 20.sp,
                             fontWeight = FontWeight.ExtraBold,
                             textAlign = TextAlign.Center,

                         )


                                 AsyncImage(
                                     model = "https://firebasestorage.googleapis.com/v0/b/korzina-7fa09.appspot.com/o/raw%2FeBYH659UhALG3KTH6OZilxVOW7v7TI8SWXW4bQT3-preview-no-bg-fotor-202312251626.png?alt=media&token=03ca03e2-46c0-4945-96d9-6b00c9224ea5",
                                     contentDescription = null,
                                     modifier = Modifier
                                         .fillMaxSize()
                                         .background(Color(0xFF292929))


                                 )

                     }


                 }
                2->{
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF292929))
                    ) {
                        Spacer(modifier = Modifier.padding(top = 50.dp))
                        Text(
                            text = "Войдите в аккаунт чтобы не потерять ни одной записанной заметки)",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(50.dp)
                        )
                        AsyncImage(
                            model = "https://firebasestorage.googleapis.com/v0/b/korzina-7fa09.appspot.com/o/raw%2Fkey.png?alt=media&token=b1c2bcef-e303-48ab-9389-cfe401219b14",
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(0.5f)
                        )


                        if (open) {
                            Thread.sleep(1500)
                            navController.navigate("glav_screen")
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 30.dp)
                        ) {

                            Button(
                                onClick = {

                                    val gso =
                                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .requestIdToken(token)
                                            .requestEmail()
                                            .build()

                                    val googleSignInClient =
                                        GoogleSignIn.getClient(context, gso)
                                    launcher.launch(googleSignInClient.signInIntent)
                                },
                                content = {
                                    Column(
                                        horizontalAlignment = Alignment.Start,
                                    ) {
                                        if (openCircle){
                                            CircularProgressIndicator(
                                                color = Color.Black,
                                                strokeWidth = 3.dp,
                                                modifier = Modifier.size(30.dp)
                                            )
                                            
                                        }
                                        else if(open){
                                            Icon(Icons.Default.Check, contentDescription = "")
                                        }else{
                                            Image(painter = painterResource(id = R.drawable.logo_google), contentDescription = "")

                                        }
                                        
                                            

                                    }
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (open) "Успешный вход!" else "Войти в Google аакаунт",
                                            color = Color.Black,
                                            fontSize = 16.sp
                                        )
                                    }



                                },
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .height(110.dp)
                                    .padding(30.dp)

                                ,colors = ButtonDefaults.buttonColors(Color.White),
                                border = BorderStroke(1.dp, Color.Black)
                            )


                        }


                    }

                }


            }


        }





        Row(

            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(14.dp)
                )
            }
        }



}
