package com.example.ggshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ggshop.navigation.Screen
import com.example.ggshop.viewmodel.MainViewModel


private val TechYellow = Color(0xFFFFD700)
private val TechBlack = Color(0xFF000000)

@Composable
fun InicioSesion(viewModel: MainViewModel) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoginValid by viewModel.isLoginValid.collectAsState()

    // Estado para manejar el error de credenciales incorrectas
    var loginError by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // --- BOTÓN PARA RETROCEDER ---
        IconButton(
            onClick = { viewModel.navigateBack() },
            modifier = Modifier
                .padding(top = 40.dp, start = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = TechBlack,
                modifier = Modifier.size(28.dp)
            )
        }

        // --- CONTENIDO PRINCIPAL ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp)) // Espacio para bajar el título por el botón

            Text(
                text = "Bienvenido a GgShop",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TechBlack
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    viewModel.onEmailChange(it)
                    loginError = false // Resetear error al escribir
                },
                label = { Text("Email (debe incluir @)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = loginError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = {
                    viewModel.onPasswordChange(it)
                    loginError = false
                },
                label = { Text("Contraseña (mín. 6 caracteres)") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = (password.isNotEmpty() && password.length < 6) || loginError,
                supportingText = {
                    if (password.isNotEmpty() && password.length < 6) {
                        Text("Faltan ${6 - password.length} caracteres", color = Color.Red)
                    } else if (loginError) {
                        Text("Credenciales no coinciden con el registro", color = Color.Red)
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN INGRESAR (Actualizado con TechYellow)
            Button(
                onClick = {
                    if (viewModel.validarCredencialesPersistidas()) {
                        viewModel.navigateTo(Screen.MainScreen)
                    } else {
                        loginError = true
                    }
                },
                enabled = isLoginValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechBlack,
                    contentColor = TechYellow,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.White
                )
            ) {
                Text(text = "Ingresar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlace para ir a registro
            TextButton(onClick = { viewModel.navigateTo(Screen.Register) }) {
                Text("¿No tienes cuenta? Regístrate aquí", color = Color.Gray)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Copyright al final (Consistencia IE 2.1.1)
            Text(
                text = "© 2026 GgShop Technology",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}