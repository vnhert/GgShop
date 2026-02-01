package com.example.ggshop.navigation

sealed class Screen(val route: String) {
    // --- PANTALLAS DE USUARIO ---
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object MainScreen : Screen("main_screen")
    object Stores : Screen("stores")
    object Profile : Screen("profile")
    object Cart : Screen("cart")
    object EditProfile : Screen("edit_profile")
    object Favorites : Screen("favorites")

    // --- PANTALLAS DE PRODUCTO ---
    // Usada para que el cliente vea la información del producto
    object Detail : Screen("detail")
    // Mantenida por compatibilidad si se usa en otras partes del código
    object ProductDetail : Screen("product_detail")

    // --- PANTALLAS DE ADMINISTRADOR ---
    // Esta es la ruta del PANEL DE CONTROL (Clientes, Ventas, Stock)
    object Inventory : Screen("inventory")

    // NUEVA RUTA: Para el formulario de AGREGAR y EDITAR productos
    // Esto soluciona que los botones del admin no funcionen
    object ProductForm : Screen("product_form")

    // --- OTRAS PANTALLAS ---
    object Splash : Screen("splash")
    object GamerZone : Screen("gamer_zone") //
}