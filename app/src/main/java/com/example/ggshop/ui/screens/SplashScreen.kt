package com.example.ggshop.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ggshop.R // Asegúrate de importar tu R
import com.example.ggshop.navigation.Screen
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(viewModel: MainViewModel) {

    //  2 segundos y navega
    LaunchedEffect(key1 = true) {
        delay(2000) // Espera 2000 milisegundos (2 segundos)


        // El 'true' en popUpTo borra el Splash del historial para que no puedas volver atrás
        viewModel.navigateTo(Screen.Login)
    }

    // El diseño visual
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // O TechBlack si prefieres fondo oscuro
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {


            Image(
                // ARCHIVO EN DRAWABLE para mi logo
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "GgShop App",
                modifier = Modifier.size(200.dp), // Ajuste del tamaño
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Una barrita de carga para que se vea "pro"
            CircularProgressIndicator(
                color = TechYellow,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}