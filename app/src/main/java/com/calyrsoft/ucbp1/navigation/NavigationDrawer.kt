package com.calyrsoft.ucbp1.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector



sealed class NavigationDrawer(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String) {


    data object Profile : NavigationDrawer("Profile", Icons.Filled.Home,
        Icons.Outlined.Home, Screen.LoginScreen.route
    )
    data object Dollar : NavigationDrawer("Dollar",
        Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart,
        Screen.Dollar.route)
    data object Github : NavigationDrawer("Github",
        Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder,
        Screen.GithubScreen.route)
    data object Movie : NavigationDrawer("Movie",
        Icons.Filled.DateRange, Icons.Outlined.DateRange,
        Screen.Screens.MoviesScreen.route)

    // ======================================================
    // 🔐 AUTH
    // ======================================================
    data object AuthLogin : NavigationDrawer(
        "Iniciar Sesión",
        Icons.Filled.Favorite,
        Icons.Outlined.FavoriteBorder,
        Screen.AuthLogin.route
    )

    data object AuthRegister : NavigationDrawer(
        "Registrar Usuario",
        Icons.Filled.Favorite,
        Icons.Outlined.FavoriteBorder,
        Screen.AuthRegister.route
    )

    // ======================================================
    // 🏨 LODGING
    // ======================================================
    data object LodgingList : NavigationDrawer(
        "Alojamientos",
        Icons.Filled.Favorite,
        Icons.Outlined.FavoriteBorder,
        Screen.LodgingList.route
    )

    data object LodgingEditor : NavigationDrawer(
        "Nuevo Alojamiento",
        Icons.Filled.Favorite,
        Icons.Outlined.FavoriteBorder,
        Screen.LodgingEditor.route
    )

    // ======================================================
    // 📅 RESERVATION
    // ======================================================
    data object ReservationCreate : NavigationDrawer(
        "Crear Reserva",
        Icons.Filled.Favorite,
        Icons.Outlined.FavoriteBorder,
        Screen.ReservationCreate.route
    )

    data object ReservationHistory : NavigationDrawer(
        "Historial de Reservas",
        Icons.Filled.Favorite,
        Icons.Outlined.FavoriteBorder,
        Screen.ReservationHistory.route
    )

    data object ReservationPayment : NavigationDrawer(
        "Pagos",
        Icons.Filled.Favorite,
        Icons.Outlined.FavoriteBorder,
        Screen.ReservationPayment.route
    )

    data object Atulado : NavigationDrawer(
        "Atulado",
        Icons.Filled.Favorite,
        Icons.Outlined.FavoriteBorder,
        Screen.Atulado.route
    )
}
