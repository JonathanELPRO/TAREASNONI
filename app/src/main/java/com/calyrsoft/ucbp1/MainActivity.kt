package com.calyrsoft.ucbp1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.calyrsoft.ucbp1.features.profile.presentation.MaintenanceBanner
import com.calyrsoft.ucbp1.features.remoteconfig.data.manager.RemoteConfigManager
import com.calyrsoft.ucbp1.features.splashScreen.SplashScreen
import com.calyrsoft.ucbp1.navigation.AppNavigation
import com.calyrsoft.ucbp1.navigation.NavigationDrawer
import com.calyrsoft.ucbp1.navigation.NavigationViewModel
import com.calyrsoft.ucbp1.navigation.Screen
import com.calyrsoft.ucbp1.ui.theme.Ucbp1Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerHost(
    coroutineScope: CoroutineScope,
    drawerState: DrawerState,
    navigationViewModel: NavigationViewModel,
    navController : NavHostController
) {
    Scaffold(
        //te da la estructura típica de una pantalla de Material Design,, usaremos topBar para definirle algo superiormente,
        //la hamburguesa ya fue definida antes y precisamente la hamburguesa esta llamando a este codigo
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu")
                    }
                }
                //navigationIcon es la parte visual de la hamburguesa, si lo apretamos el contenido de la hamburguesa se despliega
            )
        }
    ) {
            innnerPadding -> AppNavigation(
        navController = navController,
        navigationViewModel = navigationViewModel,
        modifier = Modifier.padding(innnerPadding))
        //estas ultimas lineas lo que hacen como tal es dibujar las pantallas de movies, login, github, etc
        //para ello le le mandamos innerpadding para que seapa en que coordenadas dibujar, asi como tambien
        //a la clase que navega y su viewmodel


    }


}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp( navigationViewModel: NavigationViewModel) {
    val navController: NavHostController = rememberNavController()
    //creamos a quien realmente navegara entre pantallas
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    //Estoy observando la pantalla actual del navegador (NavController)
    //y quiero que se actualice automáticamente cuando cambie
    val currentDestination = navBackStackEntry?.destination
    //De esa entrada actual, sácame el destino (la pantalla exacta a la que corresponde).
    //    Ese destination contiene información como:
    //
    //    La ruta (por ejemplo "profile", "movies", "login")
    //
    //    Los argumentos (si la ruta los usa)
    //
    //    Otros metadatos de navegación.
    val navigationDrawerItems = listOf(
        // --- Proyecto antiguo ---
        NavigationDrawer.Profile,
        NavigationDrawer.Dollar,
        NavigationDrawer.Movie,
        NavigationDrawer.Github,

        // --- 🔐 AUTH ---
        NavigationDrawer.AuthLogin,
        NavigationDrawer.AuthRegister,

        // --- 🏨 LODGING ---
        NavigationDrawer.LodgingList,
        NavigationDrawer.LodgingEditor,

        // --- 📅 RESERVATION ---
        NavigationDrawer.ReservationCreate,
        NavigationDrawer.ReservationHistory,
        NavigationDrawer.ReservationPayment,
        NavigationDrawer.Atulado
    )

    val drawerState =
        rememberDrawerState(initialValue =
            androidx.compose.material3.DrawerValue.Closed)
    //drawerState es una variable que guarda el estado actual del cajón (Drawer)(menu de hamburguesa)
    //— o sea, si está abierto o cerrado., puedo usar este drawer para abrir y cerra el cajon tambien
    val coroutineScope = rememberCoroutineScope()
    //lanzamos una corutina


    ModalNavigationDrawer(
        drawerState = drawerState,
        //ModalNavigationDrawer es un composable de cajon que ya viene por defecto en kotlin
        //pero le debemos indicar como obtener el estado de su cajon, es decir aqui sabemos otra vez si esta abierto o cerrado

        drawerContent = {
            //con lo de arriba definiremos todo el contenido de este cajon, osea la hamburguesa como tal
            ModalDrawerSheet(
                modifier = Modifier.width(256.dp)
            ) {
                Box(
                    modifier = Modifier.width(256.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.width(120.dp),
                        painter = painterResource(id =
                            R.drawable.ic_launcher_background),
                        contentDescription = "Logo",
                    )
                    Image(
                        painter = painterResource(id =
                            R.drawable.ic_launcher_foreground),
                        contentDescription = "Logo",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                navigationDrawerItems.forEach { item ->
                    val isSelected = currentDestination?.route == item.route
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                //si ya estoy en esta pantalla, no vuelvas a crear otra igual encima.
                                restoreState = true
                                //“Si ya visité esta pantalla antes y la tengo guardada, restaura su estado (osea si viistaste una pantalla antes y la scrolleaste reapareceras en el mismo lugar)
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                //“Cuando cambies de pantalla
                                //limpia todo el historial hasta la pantalla principal,
                                //pero guarda el estado de las pantallas que borraste para restaurarlas después, es decir  solo guarda la parte visual, sus view model mueren y eso esta bien
                            }
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            //Después de navegar osea navegas y se cierra el menu de hamburguesa, lanza una corrutina para cerrar ese menu de hamburguesa
                        }


                    )
                }
            }
        }
    ) {

        NavigationDrawerHost(coroutineScope, drawerState, navigationViewModel, navController)
        //notemos que:
        //ModalNavigationDrawer(AQUI DEFINIMOS TODO LO DE LA HAMBURGUESA){AHORA ESTAMOS AQUI(ESTA LINEA DIBUJAR UN TITULO Y TODO LO DE LAS PANTALLAS DE DOLAR, LOGIN Y ASI...)}
        //Le mandas el coroutineScope porque el menú lateral (drawer) se abre y se cierra usando corrutinas.
        //LE MANDAMOS Igual el estado de si la hamburguesa esta abierta o cerrada, el viewmodel y el controller tabien para que muestren el resto de pantallas

    }
}


class MainActivity : ComponentActivity() {
    private val navigationViewModel: NavigationViewModel by viewModels()
    private var currentIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        currentIntent = intent

        enableEdgeToEdge()


        RemoteConfigManager.init()



        // Escuchamos cambios en tiempo real (sin reiniciar la app)
        RemoteConfigManager.listenRealtime {
            if (RemoteConfigManager.isMaintenance()) {
                Toast.makeText(
                    this@MainActivity,
                    RemoteConfigManager.getMessage(),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Modo mantenimiento desactivado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        setContent {
            var showSplash by remember { mutableStateOf(true) }

            if (showSplash) {
                SplashScreen {
                    showSplash = false
                }
            } else {
                LaunchedEffect(Unit) {
                    navigationViewModel.handleDeepLink(currentIntent)
                }
                //lo de arriba se ejecuta una sola vez con ayuda de LaunchedEffect
                //el deeplink dice:
                //Pídele al ViewModel de navegación que revise el intent con el que se abrió la app
                //y decida a qué pantalla debe ir

                LaunchedEffect(key1 = Unit) {
                    snapshotFlow { currentIntent }
                        //Crea un “flujo” (Flow) que observa la variable currentIntent.
                        .distinctUntilChanged()
                        //“Solo me avisas cuando el intent realmente cambie.
                        //Si sigue siendo el mismo, ignóralo.”
                        .collect { intent ->
                            //Log.d(tag = "MainActivity", msg = "Nuevo intent recibido: ${intent?.action}")
                            navigationViewModel.handleDeepLink(intent)
                        }
                    //si el intent cambio se lo colecciona en collect y nos movemos a otra pantalla si fuera necesario
                }

                Box(
                    Modifier
                        .fillMaxSize()
                ) {
                    MainApp(navigationViewModel)
                    Column {
                        MaintenanceBanner()

                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        currentIntent = intent
    }

    override fun onDestroy() {
        super.onDestroy()
        // --------------------------------------------------------------
        // 🧩 NUEVO: Detenemos el listener del Remote Config al destruir la Activity
        // --------------------------------------------------------------
        RemoteConfigManager.stopListening()
    }

    //este metodo de arriba es clave primero analicemos todos los casos:
    //la aplicacion esta totalmente cerrada ante tal inconveniente las notificaciones de firebase me llegan
    //normal sin embargo el firebase service de las notitifaciones(mi codigo) no esta funcionando
    //por esto no se crea un nuevo intent con un nuevo extra
    //sin embargo si desde el firebase configuro que se me envie la notifiacion con un atributo extra: navigateTo: github
    //en tal caso la aplicacion que esta apagada tras apretar en la notificacion, creara un nuevo intent y este intent tendra ese extra que envie desde firebase
    //es como si hubiera hecho esto en codigo:             putExtra("navigateTo", Screen.GithubScreen.route) // ejemplo: "github_screen"
    //y de esta forma entraremos al primer launched effect con ese extra adicional que me llego desde firebase
    //entraremos al handleDeepLink y eso sendeara con su canal un estado de tipo comando el cual esta siendo escuchandon
    //por App navigation y se encagara de moevernos a la pagina correspondiente
    //ahora imaginemos que la aplicacion estaba abierta y me llega una notificacion, en tal caso
    //el service de notificaciones estaria corriendo sin problemas y lo que hace es deshacernos del intent actual
    //porque si estamos en este punto se supone que hay un intent actual, y creamos un nuevo intent que tiene un extra
    //como se creo un nuevo intent se llama a onNewIntent y asignamos ese nuevo intent a currentIntent
    //por otro lado currentIntent ya estaba siendo monitoreado por el segundo LaunchedEffect, como detecto un cambio
    //en el currentIntent pues llamara a:                         navigationViewModel.handleDeepLink(intent) nuevamente
    //antes estabas cometiendo un error y era que en el service de notificaciones estabas reusando el intent con el que nacio la aplicacion
    //y le estabas agregando un extra eso claro llamaba a onNewIntent quien modifica a currentIntent y al entrar
    //al segundo launch effect todo moria por: .distinctUntilChanged()
    //                    //“Solo me avisas cuando el intent realmente cambie.
    //                    //Si sigue siendo el mismo, ignóralo.”

}

