package com.example.ggshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ggshop.R
import com.example.ggshop.navigation.Screen
import com.example.ggshop.viewmodel.MainViewModel
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuario(viewModel: MainViewModel) {
    val nombre by viewModel.usuarioLogueadoNombre.collectAsState()
    val email by viewModel.usuarioLogueadoEmail.collectAsState()
    val imagenUri by viewModel.profileImageUri.collectAsState()
    val puntos by viewModel.puntosUsuario.collectAsState()

    // 1. RECUPERAMOS EL ESTADO DE ADMIN
    val esAdmin by viewModel.esAdmin.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MI PERFIL", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FOTO DE PERFIL
            AsyncImage(
                model = imagenUri ?: R.drawable.profile_pic,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = TechBlack)
            Text(text = email, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // --- BANNER GAMER ZONE ---
            Card(
                onClick = { viewModel.navigateTo(Screen.GamerZone) },
                modifier = Modifier.fillMaxWidth().height(80.dp),
                colors = CardDefaults.cardColors(containerColor = TechBlack),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("GAMER ZONE", color = TechYellow, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text("$puntos Puntos acumulados", color = Color.White, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.Gamepad, null, tint = TechYellow, modifier = Modifier.size(40.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- LISTA DE OPCIONES ---

            MenuOptionItem(
                icon = Icons.Default.Edit,
                title = "Editar Perfil",
                onClick = { viewModel.navigateTo(Screen.EditProfile) }
            )

            // 2. APLICAMOS EL CANDADO DE SEGURIDAD AQUÍ
            // Solo se muestra si 'esAdmin' es VERDADERO
            if (esAdmin) {
                MenuOptionItem(
                    icon = Icons.Default.Settings,
                    title = "Panel de Administrador",
                    onClick = { viewModel.navigateTo(Screen.Inventory) }
                )
            }

            MenuOptionItem(
                icon = Icons.Default.Settings,
                title = "Configuración y preferencias",
                onClick = { /* Acción futura */ }
            )

            MenuOptionItem(
                icon = Icons.Default.Notifications,
                title = "Alertas y Drops",
                onClick = { /* Acción futura */ }
            )

            MenuOptionItem(
                icon = Icons.Default.Lock,
                title = "Privacidad y seguridad",
                onClick = { /* Acción futura */ }
            )

            MenuOptionItem(
                icon = Icons.Default.LocationOn,
                title = "Nuestras Sucursales",
                onClick = { /* Acción futura */ }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.cerrarSesion() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("CERRAR SESIÓN", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MenuOptionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TechBlack)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontWeight = FontWeight.Bold, color = TechBlack)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}