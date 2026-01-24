package com.example.ggshop.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object MainScreen : Screen("main_screen")
    object Stores : Screen("stores")
    object Profile : Screen("profile")
    object Cart : Screen("cart")
    object ProductDetail : Screen("product_detail")
    object EditProfile : Screen("edit_profile")
    object Favorites : Screen("favorites")
    object Inventory : Screen("inventory")
    object Detail : Screen("detail")
}