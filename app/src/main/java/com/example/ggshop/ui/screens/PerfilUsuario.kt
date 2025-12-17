package com.example.ggshop.ui.screens


import androidx.compose.foundation.Image
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun PerfilUsuario(viewModel: MainViewModel = viewModel()) {
    var itemNavSeleccionado by remember { mutableStateOf("Profile") }

    Scaffold(
        topBar = { TopBarPerfil(viewModel = viewModel) },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // SECCIÓN CABECERA USUARIO
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                // Círculo de fondo con borde
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = Color(0xFFF5F5F5),
                    border = androidx.compose.foundation.BorderStroke(2.dp, TechYellow)
                ) {
                    // Intenta cargar la imagen, si falla usa un icono
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier.padding(20.dp),
                        tint = Color.LightGray
                    )
                    // Descomenta si ya agregaste la imagen a drawables:
                    /* Image(
                        painter = painterResource(id = R.drawable.profile_pic),
                        contentDescription = "Foto",
                        contentScale = ContentScale.Crop
                    )
                    */
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Gamer Pro",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = TechBlack
            )
            Text(
                text = "gg.gamer@ggshop.com",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // MENSAJE DE BIENVENIDA ESTILO GAMER
            Surface(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = TechBlack,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = TechYellow)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "READY FOR THE NEXT LEVEL?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CONFIGURACIONES
            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                color = Color.Gray,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ITEMS DE AJUSTES
            SettingsItem(
                icon = Icons.Default.Edit,
                text = "Edit Profile",
                onClick = { viewModel.navigateTo(Screen.EditProfile) }
            )
            SettingsItem(icon = Icons.Default.Settings, text = "App Preferences")
            SettingsItem(icon = Icons.Default.Notifications, text = "Alerts & Drops")
            SettingsItem(icon = Icons.Default.Lock, text = "Privacy & Security")
            SettingsItem(icon = Icons.Default.Info, text = "About GGShop")

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarPerfil(viewModel: MainViewModel) {
    CenterAlignedTopAppBar(
        title = {
            Text("ACCOUNT", fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
        },
        navigationIcon = {
            IconButton(onClick = { viewModel.navigateTo(Screen.MainScreen) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFF5F5F5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(20.dp),
                tint = TechBlack
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f),
            color = TechBlack
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.LightGray
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

@Preview(showBackground = true)
@Composable
fun PerfilUsuarioPreview() {
    GgShopTheme {
        PerfilUsuario(viewModel = MainViewModel())
    }
}