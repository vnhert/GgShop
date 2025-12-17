package com.example.ggshop.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ggshop.navigation.Screen
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Favoritos(viewModel: MainViewModel = viewModel()) {
    var itemNavSeleccionado by remember { mutableStateOf("Favorites") }

    Scaffold(
        topBar = { TopBarFavoritos(viewModel) },
        bottomBar = {
            BottomNavBarPrincipal(
                viewModel = viewModel,
                itemSeleccionado = itemNavSeleccionado
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono Tech de "En construcción"
            Icon(
                imageVector = Icons.Default.PrecisionManufacturing,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = TechYellow
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "WISH LIST UNDER CONSTRUCTION",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = TechBlack,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Estamos optimizando los servidores para que guardes tu gear favorito. Estará disponible en el próximo drop.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón para volver a la tienda
            Button(
                onClick = { viewModel.navigateTo(Screen.MainScreen) },
                colors = ButtonDefaults.buttonColors(containerColor = TechBlack),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "BACK TO STORE",
                    color = TechYellow,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarFavoritos(viewModel: MainViewModel) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "FAVORITES",
                fontWeight = FontWeight.ExtraBold,
                color = TechBlack,
                letterSpacing = 2.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = { viewModel.navigateTo(Screen.MainScreen) }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = TechBlack
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
private fun BottomNavBarPrincipal(
    viewModel: MainViewModel,
    itemSeleccionado: String
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = itemSeleccionado == "Home",
            onClick = { viewModel.navigateTo(Screen.MainScreen) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio"
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TechBlack,
                indicatorColor = TechYellow,
                unselectedIconColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = itemSeleccionado == "Profile",
            onClick = { viewModel.navigateTo(Screen.Profile) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Perfil"
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TechBlack,
                indicatorColor = TechYellow,
                unselectedIconColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = itemSeleccionado == "Favorites",
            onClick = { /* Ya estás aquí */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Favorite, // Corazón relleno para mostrar que está activo
                    contentDescription = "Favoritos"
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TechBlack,
                indicatorColor = TechYellow,
                unselectedIconColor = Color.Gray
            )
        )
    }
}