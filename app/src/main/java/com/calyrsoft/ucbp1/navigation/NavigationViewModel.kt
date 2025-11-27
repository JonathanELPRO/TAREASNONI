package com.calyrsoft.ucbp1.navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

enum class NavigationOptions {
    DEFAULT,
    CLEAR_BACK_STACK,
    REPLACE_HOME
}
class NavigationViewModel : ViewModel() {

    sealed class NavigationCommand {
        data class NavigateTo(val route: String, val options: NavigationOptions = NavigationOptions.DEFAULT) : NavigationCommand()
        object PopBackStack : NavigationCommand()
    }


    private val _navigationCommands = Channel<NavigationCommand>(Channel.BUFFERED)
    //estoy usando un chanel y ya no el flow que el docente usaba pues este viewmodel se renderiza antes de la vista
    //y la vista incluye al Mainactivity y al AppNavigation
    //hemos creando un intent del tipo action view el cual esta asociado a una url, la cual testeamos  con deeplink tester
    //en cuanto se aprieta ese link se lanza la aplicacion con ese intent que te mencione anteriormente
    //se renderiza primero el viewmodel es decir en el main activity primero llamamos a:
    //                navigationViewModel.handleDeepLink(currentIntent)
    //lo que hara que se llame a : handleUriDeepLink y luego a navigateTo que antes como esto era un flow
    //hacia un emit de un estado de tipo command, si alguien queria recolectarlo y aun no hacia un collect pena por el, es decur que no lo iba a recolectar bien
    //y precisamente AppNavigation que se renderiazaba tarde lo queria recolectar pero ya era muy tarde porque ya se hizo el emit
    //todo esto se soluciona haciendo a esto un channel, que claro hace lo mismo solo que en vez de emit hace un send
    //un send se guarda en buffer por si alguien lo quiere usar mas adelante
    //y efectivamente alguien en este caso el App Navigation lo usara mas adelante con collect
    //para que el collect siga funcionando hacemos:     val navigationCommands = _navigationCommands.receiveAsFlow()

    val navigationCommands = _navigationCommands.receiveAsFlow()

    fun navigateTo(route: String, options: NavigationOptions = NavigationOptions.DEFAULT) {
        Log.d("NavigationViewModel", "🚀 Emitiendo nav a $route con $options")
        viewModelScope.launch {
            _navigationCommands.send(NavigationCommand.NavigateTo(route, options))
        }
    }

    fun popBackStack() {
        viewModelScope.launch { _navigationCommands.send(NavigationCommand.PopBackStack) }
    }
    fun handleDeepLink(intent: android.content.Intent?) {
        viewModelScope.launch {
            try {
                // DEBUG: Log el intent recibido
                Log.d("NavigationViewModel", "Intent recibido: ${intent?.extras?.keySet()}")

                intent?.extras?.keySet()?.forEach { key ->
                    Log.d("NavigationViewModel", "Extra: $key = ${intent.getStringExtra(key)}")
                }

                when {
                    intent?.hasExtra("navigateTo") == true -> {
                        val destination = intent.getStringExtra("navigateTo")
                        Log.d("NavigationViewModel", "Procesando navigateTo: $destination")
                        Log.d("FirebaseService", "Entre al intent que tiene un navigate to ")

                        handleNavigationDestination(destination)
                    }


                    intent?.action == android.content.Intent.ACTION_VIEW -> {

                        handleUriDeepLink(intent.data)
                    }
                    intent?.hasExtra("click_action") == true -> {
                        val clickAction = intent.getStringExtra("click_action")
                        Log.d("NavigationViewModel", "Procesando click_action: $clickAction")
                        handleClickAction(clickAction)
                    }
                    else -> {
                        Log.d("NavigationViewModel", "Navegación por defecto a Login")

                        navigateTo(Screen.TimeScreen.route, NavigationOptions.CLEAR_BACK_STACK)
                    }
                }
            } catch (e: Exception) {
                Log.e("NavigationViewModel", "Error en handleDeepLink", e)
                navigateTo(Screen.Dollar.route, NavigationOptions.CLEAR_BACK_STACK)
            }
        }
    }

    private fun handleClickAction(clickAction: String?) {
        when (clickAction) {
            "OPEN_LOGIN" -> navigateTo(Screen.AuthLogin.route, NavigationOptions.CLEAR_BACK_STACK)
            "OPEN_MOVIES" -> navigateTo(Screen.Screens.MoviesScreen.route, NavigationOptions.REPLACE_HOME)
            "OPEN_DOLLAR" -> navigateTo(Screen.Dollar.route, NavigationOptions.REPLACE_HOME)
            "OPEN_GITHUB" -> navigateTo(Screen.GithubScreen.route, NavigationOptions.REPLACE_HOME)
            else -> navigateTo(Screen.AuthLogin.route, NavigationOptions.CLEAR_BACK_STACK)
        }
    }

    private fun handleUriDeepLink(uri: android.net.Uri?) {
        Log.d("HOST", " Host: ${uri?.host}")
        when (uri?.host) {

            "login" -> {
                navigateTo(Screen.AuthLogin.route, NavigationOptions.CLEAR_BACK_STACK)
            }
            "movies" -> {
                navigateTo(Screen.Screens.MoviesScreen.route, NavigationOptions.REPLACE_HOME)
            }
            "dollar" -> {
                navigateTo(Screen.Dollar.route, NavigationOptions.REPLACE_HOME)
            }
            "github" -> {
                navigateTo(Screen.GithubScreen.route, NavigationOptions.REPLACE_HOME)
            }
            "register" -> {
                navigateTo(Screen.AuthRegister.route, NavigationOptions.REPLACE_HOME)
            }
            else -> {
                navigateTo(Screen.AuthLogin.route, NavigationOptions.CLEAR_BACK_STACK)
            }
        }
    }

    private fun handleNavigationDestination(destination: String?) {
        Log.d("Destination", "El destination fue $destination")

        when (destination?.uppercase()) {

            "LOGIN" -> navigateTo(Screen.AuthLogin.route, NavigationOptions.CLEAR_BACK_STACK)
            "MOVIES" -> navigateTo(Screen.Screens.MoviesScreen.route, NavigationOptions.REPLACE_HOME)
            "DOLLAR" -> navigateTo(Screen.Dollar.route, NavigationOptions.REPLACE_HOME)
            "GITHUB" -> navigateTo(Screen.GithubScreen.route, NavigationOptions.REPLACE_HOME)
            "REGISTER" -> navigateTo(Screen.AuthRegister.route, NavigationOptions.REPLACE_HOME)
            else -> navigateTo(Screen.AuthLogin.route, NavigationOptions.CLEAR_BACK_STACK)
        }
    }
}