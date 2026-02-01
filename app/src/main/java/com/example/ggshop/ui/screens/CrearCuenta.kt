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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ggshop.viewmodel.MainViewModel
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearCuenta(viewModel: MainViewModel) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val esCorreoValido = correo.contains("@") && correo.contains(".")

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        IconButton(
            onClick = { viewModel.navigateBack() },
            modifier = Modifier.padding(top = 40.dp, start = 16.dp),
            enabled = !isLoading
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TechBlack)
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Crear Cuenta", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = correo, onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                isError = correo.isNotEmpty() && !esCorreoValido,
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = direccion, onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pass, onValueChange = { pass = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.registrarUsuario(nombre, correo, pass, direccion) },
                enabled = nombre.isNotBlank() && esCorreoValido && pass.length >= 4 && !isLoading,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TechBlack, contentColor = TechYellow),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = TechYellow, strokeWidth = 2.dp)
                } else {
                    Text("REGISTRARME", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}