package com.example.ggshop.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ggshop.R
import com.example.ggshop.data.Producto
import com.example.ggshop.navigation.Screen
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.viewmodel.MainViewModel
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@Composable
fun PantallaPrincipal(viewModel: MainViewModel = viewModel()) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    var itemNavSeleccionado by remember { mutableStateOf("Home") }
    var estaBuscando by remember { mutableStateOf(false) }
    var textoBusqueda by remember { mutableStateOf("") }
    val productos by viewModel.productos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarProductos()
    }

    Scaffold(
        topBar = {
            TopBarPrincipal(
                viewModel = viewModel,
                estaBuscando = estaBuscando,
                textoBusqueda = textoBusqueda,
                onToggleBusqueda = {
                    estaBuscando = !estaBuscando
                    if (!estaBuscando) textoBusqueda = ""
                },
                onTextoChange = { textoBusqueda = it }
            )
        },
        bottomBar = {
            BottomNavBarPrincipal(
                viewModel = viewModel,
                itemSeleccionado = itemNavSeleccionado,
                onItemSeleccionado = { itemNavSeleccionado = it }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            PromoBanner()

            // --- SECCIÓN DE PESTAÑAS (GAMING / CELULARES) ---
            TabsTech(tabSeleccionada = tabSeleccionada, onTabSelected = { tabSeleccionada = it })

            // --- LISTA DE PRODUCTOS ---
            ContenidoTech(
                viewModel = viewModel,
                productos = productos,
                tabSeleccionada = tabSeleccionada,
                query = textoBusqueda
            )
        }
    }
}

// (El banner GamerZoneBanner ha sido eliminado de este archivo porque ahora vive en PerfilUsuario.kt)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarPrincipal(
    viewModel: MainViewModel,
    estaBuscando: Boolean,
    textoBusqueda: String,
    onToggleBusqueda: () -> Unit,
    onTextoChange: (String) -> Unit
) {
    // Escuchamos si el usuario actual es Administrador
    val esAdmin by viewModel.esAdmin.collectAsState()

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.8f else 1f, label = "logoutScale")

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = estaBuscando,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    IconButton(onClick = onToggleBusqueda) {
                        Icon(Icons.Default.ArrowBack, null, tint = TechBlack)
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    if (estaBuscando) {
                        TextField(
                            value = textoBusqueda,
                            onValueChange = onTextoChange,
                            placeholder = { Text("Buscar en GGSHOP...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = TechYellow,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.navigateTo(Screen.Stores) }
                        ) {
                            Icon(Icons.Default.Settings, null, Modifier.size(32.dp), TechYellow)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("GGSHOP", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TechBlack)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, null, Modifier.size(12.dp), Color.Gray)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Sucursales", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        },
        actions = {
            IconButton(
                onClick = {
                    if (estaBuscando) {
                        if (textoBusqueda.isNotEmpty()) {
                            onTextoChange("")
                        } else {
                            onToggleBusqueda()
                        }
                    } else {
                        onToggleBusqueda()
                    }
                }
            ) {
                Icon(if (estaBuscando) Icons.Default.Close else Icons.Default.Search, null, tint = TechBlack)
            }

            if (!estaBuscando) {
                // --- BOTÓN DE PANEL ADMINISTRADOR ---
                if (esAdmin) {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Inventory) }) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Panel Admin",
                            tint = TechYellow
                        )
                    }
                }

                IconButton(onClick = { viewModel.navigateTo(Screen.Cart) }) {
                    Icon(Icons.Default.ShoppingCart, null, tint = TechBlack)
                }

                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Home) },
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                    interactionSource = interactionSource
                ) {
                    Icon(Icons.Default.ExitToApp, null, tint = Color.Red)
                }
            }
        }
    )
}

@Composable
private fun ContenidoTech(
    viewModel: MainViewModel,
    productos: List<Producto>,
    tabSeleccionada: Int,
    query: String
) {
    val categoriaBuscada = if (tabSeleccionada == 0) "GAMING" else "CELULARES"
    val productosFiltrados = productos.filter {
        it.categoria == categoriaBuscada && it.nombre.contains(query, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Image(
                painter = painterResource(id = if (tabSeleccionada == 0) R.drawable.setup else R.drawable.carrusel2),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
            Text(
                text = if (tabSeleccionada == 0) "EQUÍPATE PARA GANAR" else "LO ÚLTIMO EN TECH",
                color = TechYellow,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (query.isEmpty()) "Destacados en $categoriaBuscada" else "Resultados para: $query",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Column(modifier = Modifier.padding(8.dp)) {
            if (productosFiltrados.isEmpty()) {
                Text("No se encontraron productos", modifier = Modifier.padding(16.dp), color = Color.Gray)
            } else {
                productosFiltrados.chunked(2).forEachIndexed { indexFila, fila ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        fila.forEach { producto ->
                            var animado by remember(categoriaBuscada, query) { mutableStateOf(false) }

                            LaunchedEffect(categoriaBuscada, query) {
                                animado = true
                            }

                            AnimatedVisibility(
                                visible = animado,
                                enter = fadeIn(animationSpec = tween(600, delayMillis = indexFila * 100)) +
                                        slideInVertically(
                                            initialOffsetY = { 40 },
                                            animationSpec = tween(600, delayMillis = indexFila * 100)
                                        ),
                                modifier = Modifier.weight(1f).padding(4.dp)
                            ) {
                                // Llamamos al componente de la tarjeta
                                ProductoCard(producto = producto, viewModel = viewModel)
                            }
                        }
                        if (fila.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PromoBanner() {
    Surface(color = TechYellow, modifier = Modifier.fillMaxWidth()) {
        Text("ENVÍO GRATIS COMPRANDO HOY", color = TechBlack, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 8.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
private fun TabsTech(tabSeleccionada: Int, onTabSelected: (Int) -> Unit) {
    TabRow(
        selectedTabIndex = tabSeleccionada,
        containerColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[tabSeleccionada]),
                color = TechYellow
            )
        }
    ) {
        listOf("GAMING", "CELULARES").forEachIndexed { index, title ->
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)

            Tab(
                selected = tabSeleccionada == index,
                onClick = { onTabSelected(index) },
                interactionSource = interactionSource,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
                text = { Text(title, fontWeight = FontWeight.Bold) }
            )
        }
    }
}

@Composable
private fun BottomNavBarPrincipal(viewModel: MainViewModel, itemSeleccionado: String, onItemSeleccionado: (String) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = itemSeleccionado == "Home",
            onClick = { onItemSeleccionado("Home"); viewModel.navigateTo(Screen.MainScreen) },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = itemSeleccionado == "Profile",
            onClick = { onItemSeleccionado("Profile"); viewModel.navigateTo(Screen.Profile) },
            icon = { Icon(Icons.Outlined.Person, null) },
            label = { Text("Perfil") }
        )
        NavigationBarItem(
            selected = itemSeleccionado == "Favorites",
            onClick = { onItemSeleccionado("Favorites"); viewModel.navigateTo(Screen.Favorites) },
            icon = { Icon(Icons.Default.FavoriteBorder, null) },
            label = { Text("Favoritos") }
        )
    }
}