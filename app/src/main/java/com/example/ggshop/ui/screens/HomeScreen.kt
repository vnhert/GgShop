package com.example.ggshop.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// IMPORTANTE: Verifica que estos paquetes coincidan con los tuyos
import com.example.ggshop.R
import com.example.ggshop.navigation.Screen
import com.example.ggshop.viewmodel.MainViewModel
import com.example.ggshop.ui.theme.GgShopTheme


@Composable
fun HomeScreen(viewModel: MainViewModel) {
    // Usamos Box para que el botón "Omitir" flote en la esquina
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- BOTÓN OMITIR ---
        TextButton(
            onClick = { viewModel.navigateTo(Screen.MainScreen) }, // Ajusta "MainScreen" al nombre real en tu Screen class
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(
                text = "Omitir",
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }

        // --- CONTENIDO ORIGINAL ---
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.logo_ggshop),
                contentDescription = "Logo GgShop",
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Bienvenido a GgShop",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón Iniciar Sesión (Amarillo)
            Button(
                onClick = { viewModel.navigateTo(Screen.Login) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text("Iniciar Sesión", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Crear Cuenta (Negro)
            Button(
                onClick = { viewModel.navigateTo(Screen.Register) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Crear Cuenta", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

