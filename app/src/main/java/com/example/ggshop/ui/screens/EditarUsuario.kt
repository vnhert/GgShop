package com.example.ggshop.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
fun EditarUsuario(viewModel: MainViewModel) {
    // Datos obtenidos de tu ViewModel actual
    val nombre = viewModel.obtenerNombreUsuario()
    val correo = "ye.gamer@ggshop.com"
    val profileImageUri by viewModel.profileImageUri.collectAsState()

    // Contexto para el recurso nativo de vibración
    val context = LocalContext.current

    // RECURSO NATIVO 1: Galería (Se especifica el contrato para evitar ambigüedad)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.updateProfileImage(uri)
    }

    Scaffold(
        topBar = { TopBarEditarUsuario(viewModel) },
        bottomBar = { BottomNavBarPrincipal(viewModel, "Profile") },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // FOTO DE PERFIL TECH
            Box(contentAlignment = Alignment.BottomEnd) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .border(3.dp, TechYellow, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile_pic),
                        contentDescription = "Foto por defecto",
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .border(3.dp, TechYellow, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                // BOTÓN EDITAR FOTO
                Surface(
                    shape = CircleShape,
                    color = TechBlack,
                    modifier = Modifier
                        .size(36.dp)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { galleryLauncher.launch("image/*") } // Abre Galería
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar foto",
                        tint = TechYellow,
                        modifier = Modifier.padding(8.dp).size(16.dp)
                    )
                }
            }

            // ANIMACIÓN FUNCIONAL
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically()
            ) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = TechBlack,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Text(
                text = correo,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // CAMPOS EDITABLES (Corrigiendo Unresolved Reference)
            EditableProfileField(label = "NOMBRE DE USUARIO", value = nombre)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 1.dp, color = Color(0xFFF0F0F0))

            EditableProfileField(label = "EMAIL", value = correo)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 1.dp, color = Color(0xFFF0F0F0))

            Spacer(modifier = Modifier.height(48.dp))

            // BOTÓN GUARDAR
            Button(
                onClick = {
                    // RECURSO NATIVO 2: Vibración (IE 2.4.1)
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        vibrator.vibrate(100)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TechBlack, contentColor = TechYellow)
            ) {
                Text("SAVE CHANGES", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            }
        }
    }
}

// FUNCIONES AUXILIARES QUE FALTABAN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarEditarUsuario(viewModel: MainViewModel) {
    CenterAlignedTopAppBar(
        title = { Text("MY PROFILE", fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp) },
        navigationIcon = {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver")
            }
        }
    )
}

@Composable
fun EditableProfileField(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}

@Composable
private fun BottomNavBarPrincipal(viewModel: MainViewModel, itemSeleccionado: String) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = itemSeleccionado == "Home",
            onClick = { viewModel.navigateTo(Screen.MainScreen) },
            icon = { Icon(Icons.Default.Home, contentDescription = null) }
        )
        NavigationBarItem(
            selected = itemSeleccionado == "Profile",
            onClick = { /* Ya estamos aquí */ },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = TechYellow)
        )
    }
}