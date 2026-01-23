package com.example.ggshop.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ggshop.navigation.Screen
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuario(viewModel: MainViewModel = viewModel()) {
    // 1. DATOS EN TIEMPO REAL
    val nombreUsuario by viewModel.usuarioLogueadoNombre.collectAsState()
    val emailUsuario by viewModel.usuarioLogueadoEmail.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()

    var itemNavSeleccionado by remember { mutableStateOf("Profile") }

    // --- VARIABLES PARA EL DIÁLOGO DE EDICIÓN ---
    var mostrarDialogoEditar by remember { mutableStateOf(false) }
    var editNombre by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editPass by remember { mutableStateOf("") }

    // Lanzador de galería para la foto
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateProfileImage(uri)
    }

    // Función para abrir el diálogo cargando los datos actuales
    fun abrirEdicion() {
        editNombre = nombreUsuario
        editEmail = emailUsuario
        editPass = "" // La contraseña empieza vacía (si la deja vacía, no se cambia)
        mostrarDialogoEditar = true
    }

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

            // AVATAR CON CLICK
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Círculo grande (Foto)
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clickable { launcher.launch("image/*") },
                    shape = CircleShape,
                    color = Color(0xFFF5F5F5),
                    border = BorderStroke(2.dp, TechYellow)
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.padding(20.dp),
                            tint = Color.LightGray
                        )
                    }
                }
                // Botón pequeño (Lápiz)
                Surface(
                    shape = CircleShape,
                    color = TechBlack,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") }
                ) {
                    Icon(Icons.Default.Edit, "Editar foto", tint = TechYellow, modifier = Modifier.padding(6.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TEXTOS (NOMBRE Y EMAIL)
            Text(
                text = nombreUsuario,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = TechBlack
            )
            Text(
                text = emailUsuario,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))


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
                        text = "LISTO PARA EL SIGUIENTE NIVEL?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "CONFIGURACION",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                color = Color.Gray,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            //LISTA DE OPCIONES

            // 1. EDITAR PERFIL (Abre el diálogo)
            SettingsItem(
                icon = Icons.Default.Edit,
                text = "Editar Perfil",
                onClick = { abrirEdicion() }
            )

            SettingsItem(icon = Icons.Default.Settings, text = "Preferencias")
            SettingsItem(icon = Icons.Default.Notifications, text = "Alertas y drops")
            SettingsItem(icon = Icons.Default.Lock, text = "Privacidad y seguridad")
            SettingsItem(icon = Icons.Default.Info, text = "Acerca de GGShop")

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN CERRAR SESIÓN
            Button(
                onClick = { viewModel.cerrarSesion() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        //  (DIALOG) PARA EDITAR DATOS
        if (mostrarDialogoEditar) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoEditar = false },
                title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editNombre,
                            onValueChange = { editNombre = it },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Correo (Gmail)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        OutlinedTextField(
                            value = editPass,
                            onValueChange = { editPass = it },
                            label = { Text("Nueva Contraseña") },
                            placeholder = { Text("Dejar vacía para mantener") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // LLAMADA AL VIEWMODEL PARA GUARDAR
                            viewModel.actualizarDatosUsuario(editNombre, editEmail, editPass)
                            mostrarDialogoEditar = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TechBlack, contentColor = TechYellow)
                    ) {
                        Text("Guardar Cambios")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoEditar = false }) {
                        Text("Cancelar")
                    }
                },
                containerColor = Color.White
            )
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
                Icon(Icons.Default.ArrowBack, contentDescription = "Atras")
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