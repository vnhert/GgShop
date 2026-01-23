package com.example.ggshop.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ggshop.R
import com.example.ggshop.navigation.Screen
import com.example.ggshop.viewmodel.MainViewModel
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

private val FondoGrisTech = Color(0xFFF2F2F2)
private val TechYellow = Color(0xFFFFD700)
private val TechBlack = Color(0xFF000000)

@Composable
fun CrearCuenta(viewModel: MainViewModel) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var repetirContrasena by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(70.dp))
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo GgShop", modifier = Modifier.size(100.dp), contentScale = ContentScale.Fit)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Crear Cuenta", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TechBlack)
            Spacer(modifier = Modifier.height(32.dp))

            mensajeError?.let {
                Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            }

            Text("Nombre", style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp))
            TextField(value = nombre, onValueChange = { nombre = it; mensajeError = null }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Ingresa tu nombre") }, singleLine = true, colors = TextFieldDefaults.colors(focusedContainerColor = FondoGrisTech, unfocusedContainerColor = FondoGrisTech, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp))

            Spacer(modifier = Modifier.height(16.dp))
            Text("Correo electrónico", style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp))
            TextField(value = correo, onValueChange = { correo = it; mensajeError = null }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Ingresa tu correo") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), colors = TextFieldDefaults.colors(focusedContainerColor = FondoGrisTech, unfocusedContainerColor = FondoGrisTech, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp))

            Spacer(modifier = Modifier.height(16.dp))
            Text("Dirección", style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp))
            TextField(value = direccion, onValueChange = { direccion = it; mensajeError = null }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Ingresa tu dirección") }, singleLine = true, colors = TextFieldDefaults.colors(focusedContainerColor = FondoGrisTech, unfocusedContainerColor = FondoGrisTech, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp))

            Spacer(modifier = Modifier.height(16.dp))
            Text("Contraseña", style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp))
            TextField(value = contrasena, onValueChange = { contrasena = it; mensajeError = null }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Ingresa tu contraseña") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), visualTransformation = PasswordVisualTransformation(), colors = TextFieldDefaults.colors(focusedContainerColor = FondoGrisTech, unfocusedContainerColor = FondoGrisTech, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp))

            Spacer(modifier = Modifier.height(16.dp))
            Text("Repetir contraseña", style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp))
            TextField(value = repetirContrasena, onValueChange = { repetirContrasena = it; mensajeError = null }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Repite tu contraseña") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), visualTransformation = PasswordVisualTransformation(), colors = TextFieldDefaults.colors(focusedContainerColor = FondoGrisTech, unfocusedContainerColor = FondoGrisTech, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp))

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val emailValido = android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()
                    when {
                        nombre.isBlank() || correo.isBlank() || direccion.isBlank() || contrasena.isBlank() -> mensajeError = "Por favor, completa todos los campos."
                        !emailValido -> mensajeError = "El formato del correo no es válido."
                        contrasena.length < 6 -> mensajeError = "Mínimo 6 caracteres."
                        contrasena != repetirContrasena -> mensajeError = "Las contraseñas no coinciden."
                        else -> {
                            // Guarda en la nueva lista del ViewModel
                            viewModel.registrarUsuario(nombre, correo, contrasena, direccion)
                            viewModel.navigateTo(Screen.Login)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TechBlack, contentColor = TechYellow)
            ) {
                Text("Crear Cuenta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        IconButton(onClick = { viewModel.navigateBack() }, modifier = Modifier.padding(top = 40.dp, start = 16.dp).align(Alignment.TopStart)) {
            Icon(Icons.Default.ArrowBack, "Volver", tint = TechBlack, modifier = Modifier.size(28.dp))
        }
    }
}