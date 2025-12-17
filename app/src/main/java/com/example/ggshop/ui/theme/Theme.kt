package com.example.ggshop.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = TechYellow,         // Botones principales
    onPrimary = TechBlack,        // Texto sobre botones amarillos
    secondary = TechBlack,
    background = TechWhite,       // Fondo de la pantalla
    surface = TechWhite,          // Fondo de tarjetas/cards
    onBackground = TechBlack,     // Texto general
    onSurface = TechBlack
)

@Composable
fun GgShopTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography, // Asegúrate de que Typography esté definido en Type.kt
        content = content
    )
}

