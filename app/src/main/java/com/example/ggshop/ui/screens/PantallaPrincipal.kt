package com.example.ggshop.ui.screens
// Verifica que este sea tu package


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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
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
    val productos by viewModel.productos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarProductos()
    }

    Scaffold(
        topBar = { TopBarPrincipal(viewModel = viewModel) },
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

            // Renderiza el carrusel y las cards
            ContenidoTech(
                viewModel = viewModel,
                productos = productos,
                tabSeleccionada = tabSeleccionada
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarPrincipal(viewModel: MainViewModel) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Logo",
                    modifier = Modifier.size(32.dp),
                    tint = TechYellow
                )
                Spacer(modifier = Modifier.width(12.dp))

                // --- SUCURSALES (FUNCIONAL) ---
                Column(
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.Stores) }
                ) {
                    Text(text = "GGSHOP", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TechBlack)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, Modifier.size(12.dp), Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sucursales y puntos de retiro", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }
        },
        actions = {
            // --- BOTÓN PARA VOLVER AL INICIO (LOGIN/REGISTRO) ---
            IconButton(onClick = { viewModel.navigateTo(Screen.Login) }) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = TechBlack)
            }
            IconButton(onClick = { /* Lógica buscar */ }) { Icon(Icons.Default.Search, null, tint = TechBlack) }
            IconButton(onClick = { viewModel.navigateTo(Screen.Cart) }) {
                Icon(Icons.Default.ShoppingCart, null, tint = TechBlack)
            }
        }
    )
}

@Composable
private fun ContenidoTech(viewModel: MainViewModel, productos: List<Producto>, tabSeleccionada: Int) {
    // FILTRAR PRODUCTOS SEGÚN LA TAB SELECCIONADA
    val categoriaBuscada = if (tabSeleccionada == 0) "GAMING" else "CELULARES"
    val productosFiltrados = productos.filter { it.categoria == categoriaBuscada }

    Column(modifier = Modifier.fillMaxWidth()) {
        // --- CARRUSEL DINÁMICO ---
        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Image(
                painter = painterResource(id = if (tabSeleccionada == 0) R.drawable.setup else R.drawable.carrusel2),
                contentDescription = "Banner",
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
            text = "Destacados en $categoriaBuscada",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // --- LAS CARDS DE PRODUCTOS FILTRADOS ---
        Column(modifier = Modifier.padding(8.dp)) {
            if (productosFiltrados.isEmpty()) {
                Text("No hay productos en esta categoría", modifier = Modifier.padding(16.dp), color = Color.Gray)
            } else {
                productosFiltrados.chunked(2).forEach { fila ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        fila.forEach { producto ->
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(producto = producto, viewModel = viewModel)
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
fun ProductCard(producto: Producto, viewModel: MainViewModel) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .clickable {
                viewModel.seleccionarProducto(producto)
                viewModel.navigateTo(Screen.ProductDetail)
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            AsyncImage(
                model = producto.imagenUrl,
                contentDescription = producto.nombre,
                modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.White).padding(8.dp),
                contentScale = ContentScale.Fit
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 14.sp)
                Text("$${producto.precio}", fontWeight = FontWeight.ExtraBold, color = TechBlack)
                Spacer(Modifier.height(8.dp))
                Surface(color = TechYellow, shape = RoundedCornerShape(4.dp)) {
                    Text("VER DETALLES", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

// --- OTROS COMPONENTES ---
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