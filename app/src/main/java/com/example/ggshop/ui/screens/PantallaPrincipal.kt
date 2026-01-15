package com.example.ggshop.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
            TabsTech(tabSeleccionada = tabSeleccionada, onTabSelected = { tabSeleccionada = it })

            ContenidoTech(
                viewModel = viewModel,
                productos = productos,
                tabSeleccionada = tabSeleccionada,
                query = textoBusqueda
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarPrincipal(
    viewModel: MainViewModel,
    estaBuscando: Boolean,
    textoBusqueda: String,
    onToggleBusqueda: () -> Unit,
    onTextoChange: (String) -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                AnimatedVisibility(
                    visible = estaBuscando,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    TextField(
                        value = textoBusqueda,
                        onValueChange = onTextoChange,
                        placeholder = { Text("Buscar en GGSHOP...") },
                        modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = TechYellow,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }

                AnimatedVisibility(
                    visible = !estaBuscando,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, null, Modifier.size(32.dp), TechYellow)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.clickable { viewModel.navigateTo(Screen.Stores) }) {
                            Text("GGSHOP", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TechBlack)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, null, Modifier.size(12.dp), Color.Gray)
                                Spacer(Modifier.width(4.dp))
                                Text("Sucursales y puntos de retiro", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        },
        actions = {
            IconButton(onClick = onToggleBusqueda) {
                Icon(if (estaBuscando) Icons.Default.Close else Icons.Default.Search, null, tint = TechBlack)
            }
            if (!estaBuscando) {
                IconButton(onClick = { viewModel.navigateTo(Screen.Cart) }) {
                    Icon(Icons.Default.ShoppingCart, null, tint = TechBlack)
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
                productosFiltrados.chunked(2).forEach { fila ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        fila.forEach { producto ->
                            Box(modifier = Modifier.weight(1f)) {
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
        indicator = { tabPositions -> TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[tabSeleccionada]), color = TechYellow) }
    ) {
        listOf("GAMING", "CELULARES").forEachIndexed { index, title ->
            Tab(selected = tabSeleccionada == index, onClick = { onTabSelected(index) }, text = { Text(title, fontWeight = FontWeight.Bold) })
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
    }
}