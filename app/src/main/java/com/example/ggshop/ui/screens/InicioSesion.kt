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

    // Estado para manejar el error de credenciales incorrectas
    var loginError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido a GgShop",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
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

        // BOTÓN INGRESAR
        Button(
            onClick = {
                if (viewModel.validarCredencialesPersistidas()) {
                    // Si todo está ok, vamos a la pantalla de Inicio
                    viewModel.navigateTo(Screen.MainScreen)
                } else {
                    // Si no coinciden, activamos el error visual
                    loginError = true
                }
            },
            enabled = isLoginValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.Yellow,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.White
            )
        ) {
            Text(text = "Ingresar", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enlace para ir a registro por si no tiene cuenta
        TextButton(onClick = { viewModel.navigateTo(Screen.Register) }) {
            Text("¿No tienes cuenta? Regístrate aquí", color = Color.Gray)
        }
    }
}