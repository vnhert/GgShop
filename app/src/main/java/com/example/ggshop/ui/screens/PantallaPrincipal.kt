package com.example.ggshop.ui.screens

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ggshop.R
import com.example.ggshop.data.Producto
import com.example.ggshop.navigation.Screen
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.ui.theme.GgShopTheme
import com.example.ggshop.viewmodel.MainViewModel
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

// Colores específicos de GGShop
val TechBannerBlack = Color(0xFF1A1A1A)
val TechGrayLight = Color(0xFFF5F5F5)

@Composable
fun PantallaPrincipal(viewModel: MainViewModel = viewModel()) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    var itemNavSeleccionado by remember { mutableStateOf("Home") }

    val productos by viewModel.productos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarProductos()
    }

    // Filtrar por categorías tecnológicas
    val productosFiltrados = when (tabSeleccionada) {
        0 -> productos.filter { it.categoria.equals("GAMING", ignoreCase = true) }
        else -> productos.filter { it.categoria.equals("CELULARES", ignoreCase = true) }
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
            TabsTech(
                tabSeleccionada = tabSeleccionada,
                onTabSelected = { tabSeleccionada = it }
            )
            ContenidoTech(
                viewModel = viewModel,
                productos = productosFiltrados,
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
                // Logo GGShop
                Icon(
                    imageVector = Icons.Default.Settings, // Cambia por tu painterResource(id = R.drawable.logo_gg)
                    contentDescription = "GGShop Logo",
                    modifier = Modifier.size(32.dp),
                    tint = TechYellow
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("GGSHOP", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            }
        },
        actions = {
            IconButton(onClick = { /* Buscar */ }) {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TechBlack)
            }
            IconButton(onClick = { viewModel.navigateTo(Screen.Cart) }) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = TechBlack)
            }
        }
    )
}

@Composable
private fun PromoBanner() {
    Surface(
        color = TechYellow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "ENVÍO GRATIS EN GAMING GEAR COMPRANDO HOY",
            color = TechBlack,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}

@Composable
private fun TabsTech(tabSeleccionada: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("GAMING", "CELULARES")
    TabRow(
        selectedTabIndex = tabSeleccionada,
        containerColor = Color.White,
        contentColor = TechBlack,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[tabSeleccionada]),
                color = TechYellow
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = tabSeleccionada == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        fontWeight = if (tabSeleccionada == index) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (tabSeleccionada == index) TechBlack else Color.Gray
                    )
                }
            )
        }
    }
}

@Composable
private fun ContenidoTech(
    viewModel: MainViewModel,
    productos: List<Producto>,
    tabSeleccionada: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Banner principal de la categoría
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(TechBannerBlack)
        ) {
            // Aquí iría tu R.drawable.banner_tech
            Text(
                text = if (tabSeleccionada == 0) "UPGRADE YOUR SETUP" else "NEXT GEN PHONES",
                color = TechYellow,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sección de Productos
        Text(
            text = if (tabSeleccionada == 0) "Periféricos y Consolas" else "Smartphones Destacados",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (productos.isEmpty()) {
            CircularProgressIndicator(color = TechYellow, modifier = Modifier.padding(32.dp))
        } else {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                productos.chunked(2).forEach { fila ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        fila.forEach { producto ->
                            Box(modifier = Modifier.weight(1f)) {
                                ProductoTechItem(producto = producto, viewModel = viewModel)
                            }
                        }
                        if (fila.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ProductoTechItem(producto: Producto, viewModel: MainViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.seleccionarProducto(producto)
                viewModel.navigateTo(Screen.ProductDetail)
            },
        shape = RoundedCornerShape(8.dp), // Esquinas más rectas para look tech
        colors = CardDefaults.cardColors(containerColor = TechGrayLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            AsyncImage(
                model = producto.imagenUrl,
                contentDescription = producto.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = TechBlack
            )

            Text(
                text = "$${producto.precio}",
                style = MaterialTheme.typography.titleMedium,
                color = TechBlack,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = TechYellow,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "VER MÁS",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BottomNavBarPrincipal(
    viewModel: MainViewModel,
    itemSeleccionado: String,
    onItemSeleccionado: (String) -> Unit
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = itemSeleccionado == "Home",
            onClick = { onItemSeleccionado("Home"); viewModel.navigateTo(Screen.MainScreen) },
            icon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(28.dp)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TechBlack,
                indicatorColor = TechYellow,
                unselectedIconColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = itemSeleccionado == "Profile",
            onClick = { onItemSeleccionado("Profile"); viewModel.navigateTo(Screen.Profile) },
            icon = { Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(28.dp)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TechBlack,
                indicatorColor = TechYellow,
                unselectedIconColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = itemSeleccionado == "Favorites",
            onClick = { onItemSeleccionado("Favorites"); viewModel.navigateTo(Screen.Favorites) },
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(28.dp)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TechBlack,
                indicatorColor = TechYellow,
                unselectedIconColor = Color.Gray
            )
        )
    }
}