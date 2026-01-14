package com.example.ggshop.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ggshop.R
import com.example.ggshop.navigation.Screen
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.ui.theme.GgShopTheme
import com.example.ggshop.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarUsuario(viewModel: MainViewModel = viewModel()) {

    // Datos de ejemplo (Luego los traerás de tu base de datos)
    val nombre = "Kanye west"
    val correo = "ye.gamer@ggshop.com"
    val contrasena = "************"
    val direccion = "Tech Avenue 404, Silicon Valley"

    Scaffold(
        topBar = { TopBarEditarUsuario(viewModel) },
        bottomBar = {
            BottomNavBarPrincipal(
                viewModel = viewModel,
                itemSeleccionado = "Profile"
            )
        },
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
                // Si no tienes la imagen profile_pic, usa un Icon por defecto
                Image(
                    painter = painterResource(id = R.drawable.profile_pic), // Asegúrate de tener esta imagen o cámbiala
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .border(3.dp, TechYellow, CircleShape) // Borde amarillo tech
                )
                // BOTÓN EDITAR FOTO
                Surface(
                    shape = CircleShape,
                    color = TechBlack,
                    modifier = Modifier
                        .size(36.dp)
                        .border(2.dp, Color.White, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar foto",
                        tint = TechYellow,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(16.dp)
                    )
                }
            }

            Text(
                text = nombre,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = TechBlack,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = correo,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // CAMPOS EDITABLES
            EditableProfileField(label = "NOMBRE DE USUARIO", value = nombre)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 1.dp, color = Color(0xFFF0F0F0))

            EditableProfileField(label = "EMAIL", value = correo)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 1.dp, color = Color(0xFFF0F0F0))

            EditableProfileField(label = "PASSWORD", value = contrasena)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 1.dp, color = Color(0xFFF0F0F0))

            EditableProfileField(label = "SHIPPING ADDRESS", value = direccion)

            Spacer(modifier = Modifier.height(48.dp))

            // BOTÓN GUARDAR
            Button(
                onClick = { /* Acción para guardar en base de datos */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechBlack,
                    contentColor = TechYellow
                )
            ) {
                Text(
                    "SAVE CHANGES",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarEditarUsuario(viewModel: MainViewModel) {
    CenterAlignedTopAppBar(
        title = {
            Text("MY PROFILE", fontWeight = FontWeight.ExtraBold, color = TechBlack, letterSpacing = 2.sp)
        },
        navigationIcon = {
            IconButton(onClick = { viewModel.navigateTo(Screen.Profile) }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = TechBlack
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = TechBlack,
                fontWeight = FontWeight.Medium
            )
        }
        Icon(
            Icons.Default.KeyboardArrowRight, // Un icono más estilo "ajustes"
            contentDescription = "Editar $label",
            tint = Color.LightGray,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun BottomNavBarPrincipal(
    viewModel: MainViewModel,
    itemSeleccionado: String
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = itemSeleccionado == "Home",
            onClick = { viewModel.navigateTo(Screen.MainScreen) },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = TechBlack, indicatorColor = TechYellow)
        )
        NavigationBarItem(
            selected = itemSeleccionado == "Profile",
            onClick = { viewModel.navigateTo(Screen.Profile) },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = TechBlack, indicatorColor = TechYellow)
        )
        NavigationBarItem(
            selected = itemSeleccionado == "Favorites",
            onClick = { viewModel.navigateTo(Screen.Favorites) },
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = TechBlack, indicatorColor = TechYellow)
        )
    }
}
