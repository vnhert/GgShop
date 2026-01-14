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

// Definimos colores locales para seguir el ejemplo de Rosa Pastel (FondoGrisClaro y TechYellow)
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

    // Estado para el mensaje de error
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Logo de GgShop
        Image(
            painter = painterResource(id = R.drawable.logo_ggshop),
            contentDescription = "Logo GgShop",
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TechBlack
        )

        Spacer(modifier = Modifier.height(32.dp))

        // MOSTRAR ERROR SI EXISTE
        mensajeError?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // --- FORMULARIO COMPLETO ---

        // Nombre
        Text(
            text = "Nombre",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            textAlign = TextAlign.Start
        )
        TextField(
            value = nombre,
            onValueChange = { nombre = it; mensajeError = null },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ingresa tu nombre", style = MaterialTheme.typography.bodyMedium) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = FondoGrisTech,
                unfocusedContainerColor = FondoGrisTech,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Correo
        Text(
            text = "Correo electrónico",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            textAlign = TextAlign.Start
        )
        TextField(
            value = correo,
            onValueChange = { correo = it; mensajeError = null },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ingresa tu correo electrónico", style = MaterialTheme.typography.bodyMedium) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = FondoGrisTech,
                unfocusedContainerColor = FondoGrisTech,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dirección
        Text(
            text = "Dirección",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            textAlign = TextAlign.Start
        )
        TextField(
            value = direccion,
            onValueChange = { direccion = it; mensajeError = null },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ingresa tu dirección", style = MaterialTheme.typography.bodyMedium) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = FondoGrisTech,
                unfocusedContainerColor = FondoGrisTech,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        Text(
            text = "Contraseña",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            textAlign = TextAlign.Start
        )
        TextField(
            value = contrasena,
            onValueChange = { contrasena = it; mensajeError = null },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ingresa tu contraseña", style = MaterialTheme.typography.bodyMedium) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = FondoGrisTech,
                unfocusedContainerColor = FondoGrisTech,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Repetir Password
        Text(
            text = "Repetir contraseña",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            textAlign = TextAlign.Start
        )
        TextField(
            value = repetirContrasena,
            onValueChange = { repetirContrasena = it; mensajeError = null },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Repite tu contraseña", style = MaterialTheme.typography.bodyMedium) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = FondoGrisTech,
                unfocusedContainerColor = FondoGrisTech,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón con Lógica de Validación
        Button(
            onClick = {
                val emailValido = android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()

                when {
                    nombre.isBlank() || correo.isBlank() || direccion.isBlank() || contrasena.isBlank() -> {
                        mensajeError = "Por favor, completa todos los campos."
                    }
                    !emailValido -> {
                        mensajeError = "El formato del correo no es válido (ejemplo@gmail.com)."
                    }
                    contrasena.length < 6 -> {
                        mensajeError = "La contraseña debe tener al menos 6 caracteres."
                    }
                    contrasena != repetirContrasena -> {
                        mensajeError = "Las contraseñas no coinciden."
                    }
                    else -> {
                        mensajeError = null
                        println("GgShop Registro -> Nombre: $nombre, Correo: $correo")
                        viewModel.navigateTo(Screen.Login)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TechBlack,
                contentColor = TechYellow
            )
        ) {
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "© 2026 GgShop Technology. Todos los derechos reservados.",
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp),
            color = Color.Gray
        )
    }
}

