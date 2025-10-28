package com.calyrsoft.ucbp1.navigation

sealed class Screen(val route: String) {
    object LoginScreen : Screen("login")
    object GithubScreen : Screen("github")



    object ProfileScreen : Screen("profile_screen/{name}")



    object Dollar: Screen("dollar")
    object ForgotPasswordScreen : Screen("forgot_password")
    object PostsScreen : Screen("posts")

    sealed class Screens(val route: String) {
        object MoviesScreen : Screens("movies")
        object MovieDetailScreen: Screens("moviedetail")
    }

    object Atulado: Screen("atulado")






    // =======================================================
    // 🔐 MÓDULO AUTH (Sistema de usuarios)
    // =======================================================
    /** Pantalla de inicio de sesión */
    object AuthLogin : Screen("auth_login")

    /** Pantalla de registro de nuevos usuarios */
    object AuthRegister : Screen("auth_register")

    // =======================================================
    // 🏨 MÓDULO LODGING (Moteles y residenciales)
    // =======================================================
    /** Lista general de alojamientos */
    object LodgingList : Screen("lodging_list")

    /** Detalle de un alojamiento específico */
    object LodgingDetails : Screen("lodging_details/{lodgingId}")

    /** Editor o creación de alojamiento */
    object LodgingEditor : Screen("lodging_editor/{userId}")

    // =======================================================
    // 📅 MÓDULO RESERVATION (Reservas y pagos)
    // =======================================================
    /** Crear una nueva reserva */
    object ReservationCreate : Screen("reservation_create/{userId}/{lodgingId}")

    /** Historial de reservas por usuario */
    object ReservationHistory : Screen("reservation_history/{userId}")

    /** Registrar pago (anticipo o saldo) */
    object ReservationPayment : Screen("reservation_payment/{reservationId}")



}