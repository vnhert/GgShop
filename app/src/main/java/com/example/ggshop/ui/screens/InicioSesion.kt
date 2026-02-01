package com.example.ggshop.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.animation.core.animateFloatAsState
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private val TechGreen = Color(0xFF00C853)

@Composable
fun InicioSesion(viewModel: MainViewModel) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoginValid by viewModel.isLoginValid.collectAsState()

    // Observamos el error directamente del ViewModel
    val loginError by viewModel.loginError.collectAsState()

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

            // --- CAMBIO APLICADO: ETIQUETA PROFESIONAL Y VALIDACIÓN PERMISIVA ---
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },

                // 1. Etiqueta profesional que querías
                label = { Text("Correo electrónico") },

                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),

                // 2. Validación visual (Rojo):
                // Se pone rojo SI escribieron algo Y NO tiene formato de correo...
                // EXCEPTO si escribieron "admin" o "user", en cuyo caso NO se pone rojo.
                isError = email.isNotEmpty() && (!email.contains("@") || !email.contains(".")) && email != "admin" && email != "user" || loginError
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = (password.isNotEmpty() && password.length < 4) || loginError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mensaje de Error
            AnimatedVisibility(
                visible = loginError,
                enter = scaleIn(animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f)) + fadeIn(animationSpec = tween(300)),
                exit = scaleOut(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
            ) {
                Text(
                    text = "❌ Credenciales incorrectas ❌",
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.validarLogin() },
                enabled = isLoginValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
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
                interactionSource = interactionSource
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