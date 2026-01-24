package com.example.ggshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuario(viewModel: MainViewModel) {
    // Datos del usuario
    val nombre by viewModel.usuarioLogueadoNombre.collectAsState()
    val email by viewModel.usuarioLogueadoEmail.collectAsState()
    val imageUri by viewModel.profileImageUri.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ACCOUNT", fontWeight = FontWeight.Bold, letterSpacing = 2.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, null)
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // --- FOTO Y NOMBRE ---
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = imageUri ?: R.drawable.profile_pic, // Tu imagen por defecto
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TechBlack)
                        .clickable { viewModel.navigateTo(Screen.EditProfile) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, null, tint = TechYellow, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(nombre, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            Text(email, color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // --- BANNER GAMER ZONE ---
            GamerZoneBannerPerfil(viewModel = viewModel)

            Spacer(modifier = Modifier.height(32.dp))

            // --- LISTA DE OPCIONES (COMPLETA) ---
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Text("CONFIGURACION", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(16.dp))

                OpcionPerfil(icon = Icons.Default.Edit, text = "Editar Perfil") {
                    viewModel.navigateTo(Screen.EditProfile)
                }
                OpcionPerfil(icon = Icons.Default.Settings, text = "Preferencias") {
                    // Acción futura
                }
                OpcionPerfil(icon = Icons.Default.Notifications, text = "Alertas y Drops") {
                    // Acción futura
                }
                // --- CAMBIO AQUÍ: ---
                OpcionPerfil(icon = Icons.Default.Lock, text = "Privacidad y seguridad") {
                    // Acción futura
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.cerrarSesion() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun OpcionPerfil(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = TechBlack, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
    }
}

@Composable
fun GamerZoneBannerPerfil(viewModel: MainViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { viewModel.navigateTo(Screen.GamerZone) },
        colors = CardDefaults.cardColors(containerColor = TechBlack),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = TechYellow)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "LISTO PARA EL SIGUIENTE NIVEL?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                color = Color.White
            )
        }
    }
}