package com.example.ggshop.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ggshop.navigation.Screen
import com.example.ggshop.viewmodel.MainViewModel
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.animateFloatAsState// Para animar la escala del botón

// Colores consistentes
private val TechYellow = Color(0xFFFFD700)
private val TechBlack = Color(0xFF000000)

@Composable
fun InicioSesion(viewModel: MainViewModel) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoginValid by viewModel.isLoginValid.collectAsState()

    var loginError by remember { mutableStateOf(false) }

    // Para la animación de "presionar" el botón
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(if (isPressed) 0.95f else 1f, label = "buttonScale")

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Bienvenido a GgShop",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TechBlack
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    viewModel.onEmailChange(it)
                    loginError = false
                },
                label = { Text("Email (debe incluir @)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = loginError
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- ANIMACIÓN MÁS VISUAL PARA EL MENSAJE DE ERROR (EFECTO REBOTE) ---
            AnimatedVisibility(
                visible = loginError,
                // Animación de entrada con rebote
                enter = scaleIn(animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f)) + fadeIn(animationSpec = tween(300)),
                // Animación de salida (más suave)
                exit = scaleOut(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
            ) {
                Text(
                    text = "❌ Credenciales no coinciden ❌",
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp // Más grande para mayor visibilidad
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN INGRESAR con ANIMACIÓN DE ESCALA
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
                    .height(55.dp)
                    // Aplica la escala animada aquí
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechBlack,
                    contentColor = TechYellow,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.White
                ),
                interactionSource = interactionSource // Para detectar si se presiona
            ) {
                Text(text = "Ingresar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { viewModel.navigateTo(Screen.Register) }) {
                Text("¿No tienes cuenta? Regístrate aquí", color = Color.Gray)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "© 2026 GgShop Technology",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}