package com.example.ggshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ggshop.navigation.Screen
import com.example.ggshop.viewmodel.MainViewModel

@Composable

fun InicioSesion(viewModel: MainViewModel) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoginValid by viewModel.isLoginValid.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Bienvenido", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(40.dp))

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Email (debe incluir @)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Contraseña (mín. 8 caracteres)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            // Ayuda visual: se pone rojo si es muy corta
            isError = password.isNotEmpty() && password.length < 8,
            supportingText = {
                if (password.isNotEmpty() && password.length < 8) {
                    Text("Faltan ${8 - password.length} caracteres", color = Color.Red)
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.navigateTo(Screen.MainScreen) },
            enabled = isLoginValid, // Se habilita SOLO cuando email tiene @ Y pass tiene 8+ letras
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                disabledContainerColor = Color.LightGray
            )
        ) {
            Text("Ingresar", color = if (isLoginValid) Color.Yellow else Color.White)
        }
    }
}