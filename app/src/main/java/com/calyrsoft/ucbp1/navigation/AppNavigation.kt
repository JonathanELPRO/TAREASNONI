package com.calyrsoft.ucbp1.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.calyrsoft.ucbp1.features.auth.presentation.LoginScreen2
import com.calyrsoft.ucbp1.features.auth.presentation.RegisterScreen
//import com.calyrsoft.ucbp1.features.auth.presentation.LoginScreen
//import com.calyrsoft.ucbp1.features.auth.presentation.RegisterScreen
import com.calyrsoft.ucbp1.features.dollar.presentation.DollarScreen
import com.calyrsoft.ucbp1.features.profile.presentation.ForgotPasswordScreen
import com.calyrsoft.ucbp1.features.github.presentation.GithubScreen
import com.calyrsoft.ucbp1.features.lodging.presentation.LodgingDetailsScreen
import com.calyrsoft.ucbp1.features.lodging.presentation.LodgingEditorScreen
import com.calyrsoft.ucbp1.features.lodging.presentation.LodgingListScreen
import com.calyrsoft.ucbp1.features.movie.domain.model.MovieModel
import com.calyrsoft.ucbp1.features.movie.presentation.MoviesScreen
import com.calyrsoft.ucbp1.features.movie.presentation.details.MovieDetailsScreen
import com.calyrsoft.ucbp1.features.posts.presentation.PostsScreen
import com.calyrsoft.ucbp1.features.profile.presentation.ProfileScreen
import com.calyrsoft.ucbp1.features.profile.presentation.SigninPage
import com.calyrsoft.ucbp1.features.reservation.presentation.HistoryScreen
import com.calyrsoft.ucbp1.features.reservation.presentation.PaymentScreen
import com.calyrsoft.ucbp1.features.reservation.presentation.ReservationScreen
import com.calyrsoft.ucbp1.features.webview.presentation.AtuladoScreen
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


@Composable
fun AppNavigation(navigationViewModel: NavigationViewModel, modifier: Modifier, navController: NavHostController) {

    LaunchedEffect(navigationViewModel) {   // clave asociada al VM
        navigationViewModel.navigationCommands.collect { command ->
            when (command) {
                is NavigationViewModel.NavigationCommand.NavigateTo -> {
                    Log.d("NavHost", "🧭 route=${command.route} opt=${command.options}")
                    navController.navigate(command.route) {
                        when (command.options) {
                            NavigationOptions.CLEAR_BACK_STACK -> {
                                popUpTo(0)
                            }
                            //lo de arriba borra absolutamente toda la pila de visitas
                            NavigationOptions.REPLACE_HOME -> {
                                popUpTo(Screen.AuthLogin.route) { inclusive = true }

                            }
                            //lo de arriba solo borra la pila de visitas hasta llegar al loginscreen mas reciene lo borra igual ese pero
                            else -> { /* normal */ }
                        }
                    }
                }
                NavigationViewModel.NavigationCommand.PopBackStack -> {
                    Log.d("NavHost", "⬅️ PopBackStack")
                    navController.popBackStack()
                }
            }
        }
    }


    NavHost(
        navController = navController,
        startDestination = Screen.AuthLogin.route,
        //no impirta que valor pongas arriba no empezaremos ahi por culpa del else de handleDeepLink
        //que se llama en el primer launched effect de main activity
        modifier = modifier
    ) {
        composable(Screen.LoginScreen.route) {
            SigninPage(
                modifier = modifier,
                vm = koinViewModel(),
                onSuccess = { name ->
                    val encodedName = URLEncoder.encode(name, "UTF-8")

                    navController.navigate(
                        "profile_screen/$encodedName"
                    )
                },
                navToForgotPassword = {
                    navController.navigate(Screen.ForgotPasswordScreen.route)
                },
                onMovies = {
                    navController.navigate(Screen.Screens.MoviesScreen.route)
                },
                onDollar = {
                    navController.navigate(Screen.Dollar.route)
                }

            )
        }

        //En pocas palabras: la ruta (Screen.LoginScreen)
        // y la pantalla (SigninPage) están conectadas en el NavHost mediante el bloque composable.

        composable(Screen.GithubScreen.route) {
            GithubScreen(
                modifier = modifier,
                vm = koinViewModel()
            )
        }

        composable(
            Screen.ProfileScreen.route,
            arguments = listOf(
                navArgument("name") { defaultValue = "" }
                //,navArgument("age") { defaultValue = 0 },
                )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""


            ProfileScreen(
                modifier = modifier,
                name = name,
                vm = koinViewModel(),

                onEndSession = {
                    navController.navigate(
                        Screen.LoginScreen.route
                    )
                },

                onAskExchangeRate = {
                    navController.navigate(
                        Screen.Dollar.route
                    )
                }
            )
        }

        //TODO HASTA ONEND SESSION Y ONASKEXCHANGE RATE YA LO ESTUDIASTE

//        composable(Screen.ExchangeRateScreen.route) {
//            ExchangeRateScreen(
//                modifier = modifier,
//                vm = koinViewModel()
//            )
//        }


        composable(Screen.Dollar.route) {
            DollarScreen(viewModelDollar = koinViewModel())
        }


        composable(Screen.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(
                modifier = modifier,
                vm = koinViewModel(),
                onBackToLogin = {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Screens.MoviesScreen.route) {
            MoviesScreen(
                modifier = modifier,
                vm = koinViewModel(),
                onClick = { movie ->
                    val movieJson = Json.encodeToString(movie) //vuelve el model a un string de JSON
                    val encodeMovieJson = URLEncoder.encode(movieJson, "UTF-8") //Toma ese JSON y lo escapa (codifica) usando el formato URL-safe (seguro para ser parte de una URL o ruta). sin
                    //esto no funciona enviarlo a traves de una ruta algo asi lo vuelve: %7B%22id%22%3A1086910%2C%22title%22%3A%22Expediente+Warren%22%2C%22imageUrl%22%3A%22https%3A%2F%2F...jpg%22%7D
                    navController.navigate(
                        "${Screen.Screens.MovieDetailScreen.route}/${encodeMovieJson}")

                }

            )
        }



        composable(
            route = "${Screen.Screens.MovieDetailScreen.route}/{movie}",
            arguments = listOf(
                navArgument("movie") { type = NavType.StringType }
            )
        ) {
            val movieJson = it.arguments?.getString("movie") ?: ""
            val movieDecoded = URLDecoder.decode(movieJson, "UTF-8") //Hace el proceso inverso:
            //convierte el texto codificado (%7B%22id%22%3A...%7D) de vuelta al JSON original:
            val movie = Json.decodeFromString<MovieModel>(movieDecoded)
            //convierte un string en formato json a un model osea a su formato original

            MovieDetailsScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
                movie = movie)

        }





        composable(Screen.PostsScreen.route) {
            PostsScreen(
                modifier = modifier,
                vm = koinViewModel()
            )
        }


        // 🔐 AUTH
        composable(Screen.AuthLogin.route) {
            LoginScreen2(
                vm = koinViewModel(),
                onLoginSuccessGoToLodgings = {
                    navigationViewModel.navigateTo(
                        Screen.LodgingList.route,
                        NavigationOptions.CLEAR_BACK_STACK
                    )
                },
                onLoginSuccessGoToRegisterLodging = { userId ->
                    navigationViewModel.navigateTo(
                        "lodging_editor/$userId",
                        NavigationOptions.CLEAR_BACK_STACK
                    )
                },

                onRegisterClick = {
                    navigationViewModel.navigateTo(
                        Screen.AuthRegister.route,
                        NavigationOptions.DEFAULT
                    )
                }
            )
        }

        composable(Screen.AuthRegister.route) {
            RegisterScreen(
                vm = koinViewModel(),
                onRegisterSuccess = {
                    navigationViewModel.navigateTo(
                        Screen.AuthLogin.route,
                        NavigationOptions.REPLACE_HOME
                    )
                },
                onBackToLogin = {
                    navigationViewModel.navigateTo(
                        Screen.AuthLogin.route,
                        NavigationOptions.REPLACE_HOME
                    )
                }
            )
        }

        // 🏨 LODGING
        composable(Screen.LodgingList.route) {
            LodgingListScreen(
                vm = koinViewModel(),


                onDetails = { id ->
                    navigationViewModel.navigateTo(
                        "lodging_details/$id",
                        NavigationOptions.DEFAULT
                    )
                }
            )
        }

        composable(
            Screen.LodgingDetails.route,
            arguments = listOf(navArgument("lodgingId") { type = androidx.navigation.NavType.LongType })
        ) { backStack ->
            val id = backStack.arguments!!.getLong("lodgingId")
            LodgingDetailsScreen(
                id = id,
                vm = koinViewModel(),
                onBack = { navController.popBackStack() }
            )
        }


        composable(
            Screen.LodgingEditor.route,
            arguments = listOf(
                navArgument("userId") { type = androidx.navigation.NavType.LongType }
            )
        ) { backStack ->
            val userId = backStack.arguments!!.getLong("userId")
            LodgingEditorScreen(
                currentRole = com.calyrsoft.ucbp1.features.auth.domain.model.Role.ADMIN,
                vm = koinViewModel(),
                userId = userId,
                onSaved = { navController.popBackStack() }
            )
        }

        // 📅 RESERVATION
        composable(
            Screen.ReservationCreate.route,
            arguments = listOf(
                navArgument("userId") { type = androidx.navigation.NavType.LongType },
                navArgument("lodgingId") { type = androidx.navigation.NavType.LongType }
            )
        ) { backStack ->
            val userId = backStack.arguments!!.getLong("userId")
            val lodgingId = backStack.arguments!!.getLong("lodgingId")
            ReservationScreen(
                vm = koinViewModel(),
                userId = userId,
                lodgingId = lodgingId,
                onCreated = { navController.navigate("reservation_history/$userId") }
            )
        }

        composable(
            Screen.ReservationHistory.route,
            arguments = listOf(navArgument("userId") { type = androidx.navigation.NavType.LongType })
        ) { backStack ->
            val userId = backStack.arguments!!.getLong("userId")
            HistoryScreen(vm = koinViewModel(), userId = userId)
        }

        composable(
            Screen.ReservationPayment.route,
            arguments = listOf(navArgument("reservationId") { type = androidx.navigation.NavType.LongType })
        ) { backStack ->
            val reservationId = backStack.arguments!!.getLong("reservationId")
            PaymentScreen(vm = koinViewModel(), reservationId = reservationId)
        }


        composable(
            Screen.Atulado.route
        ) {
            AtuladoScreen(
                "https://www.bisa.com/atulado",
                postData = null,
                modifier = modifier,
                shouldStopBrowsing = { true }
            )
        }

//        composable(
//            Screen.Atulado.route
//        ) {
//            AtuladoScreen(
//                // 1. Apunta a la URL que PROCESA el login
//                url = "https://the-internet.herokuapp.com/authenticate",
//
//                postData = "username=tomsmith&password=SuperSecretPassword!",
//
//                modifier = modifier,
//
//                // 2. ¡CRÍTICO! Permite la navegación para ver la página de bienvenida
//                shouldStopBrowsing = { false }
//            )
//        }

        //eso de arriba es un ejemplo de como enviar postData para que se pueda hacer login, en este caso
        //estamos usando una url de prueba que tiene un login de prueba, se un username y un password que la misma pagina
        //como es de testeo me da, la cosa es como armamos el postData?
        //entramos aqui: https://the-internet.herokuapp.com/login
        //le damos a inspeccionar al username y al password sale esto:
        //<input type="text" name="username" id="username">
        //<input type="password" name="password" id="password">
        //lo importante es agarrar el input y su name, con el name de ambos creamos esto:
        //"username=tomsmith&password=SuperSecretPassword!"
        //luego de eso debemps darnos cuenta que https://the-internet.herokuapp.com/login
        //es una solicitud get para ver el formulario
        //en este html nos podemos dar cuenta que exitse <form id="login" action="/authenticate" method="post">
        //ese form nos da la url final
        //https://the-internet.herokuapp.com/authenticate   que es el endpoint final a quien se envia los datos del formulario
        //la manera de enviarle datos ya lo definimos con ayuda el formulario de la pagina del login
        //ademas debemos habilitar la redireccion pued luego de pasar por el endpoint authenticate nos vamos a secure




    }
}