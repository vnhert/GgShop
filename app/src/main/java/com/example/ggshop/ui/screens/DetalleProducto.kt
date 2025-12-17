package com.example.ggshop.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ggshop.data.Producto
import com.example.ggshop.navigation.Screen
import com.example.ggshop.ui.theme.TechBlack // Reemplaza Cordovan
import com.example.ggshop.ui.theme.TechYellow // Reemplaza NewYorkPink
import com.example.ggshop.ui.theme.GgShopTheme
import com.example.ggshop.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProducto(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val productoSeleccionado by viewModel.productoSeleccionado.collectAsState()
    var quantity by remember { mutableIntStateOf(1) }

    if (productoSeleccionado == null) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("Buscando el gadget seleccionado...", color = TechBlack)
        }
        return
    }

    val producto = productoSeleccionado!!

    Scaffold(
        topBar = { TopBarDetalleProducto(viewModel) },
        bottomBar = {
            BottomNavBarPrincipal(viewModel = viewModel, itemSeleccionado = "Home")
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ProductImageAndControls(producto = producto)

            Spacer(modifier = Modifier.height(24.dp))

            ProductInfoSection(
                title = producto.nombre,
                price = "$${producto.precio}",
                category = producto.categoria,
                description = producto.descripcion
            )

            QuantitySelector(
                quantity = quantity,
                onQuantityChange = { quantity = it }
            )

            // BOTÓN AÑADIR AL CARRITO ESTILO GGSHOP
            Button(
                onClick = {
                    viewModel.agregarAlCarrito(producto, quantity)
                    Toast.makeText(
                        context,
                        "Añadido: ${producto.nombre} (x$quantity)",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechYellow,
                    contentColor = TechBlack
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    "AÑADIR AL CARRITO",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarDetalleProducto(viewModel: MainViewModel) {
    TopAppBar(
        title = { Text("Detalle", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = { viewModel.navigateTo(Screen.MainScreen) }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = TechBlack)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
private fun ProductImageAndControls(producto: Producto) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = producto.imagenUrl,
            contentDescription = producto.nombre,
            modifier = Modifier.fillMaxHeight().padding(24.dp),
            contentScale = ContentScale.Fit
        )

        IconButton(
            onClick = { /* Lógica Favoritos */ },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.FavoriteBorder,
                contentDescription = "Favorito",
                tint = TechBlack,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun ProductInfoSection(
    title: String,
    price: String,
    category: String,
    description: String
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            category.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray
        )
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = TechBlack
        )
        Text(
            price,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = TechBlack,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = TechYellow, modifier = Modifier.size(18.dp))
            }
            Text(" (4.5)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SOBRE ESTE PRODUCTO",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = TechBlack
        )

        Text(
            text = if (description.isNotBlank()) description else "No hay especificaciones disponibles.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 4.dp),
            lineHeight = 20.sp
        )
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("CANTIDAD:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
        ) {
            IconButton(onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }) {
                Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.width(30.dp),
                textAlign = TextAlign.Center
            )
            IconButton(onClick = { onQuantityChange(quantity + 1) }) {
                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BottomNavBarPrincipal(
    viewModel: MainViewModel,
    itemSeleccionado: String
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = itemSeleccionado == "Home",
            onClick = { viewModel.navigateTo(Screen.MainScreen) },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TechBlack,
                indicatorColor = TechYellow
            )
        )
        NavigationBarItem(
            selected = itemSeleccionado == "Profile",
            onClick = { viewModel.navigateTo(Screen.Profile) },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TechBlack,
                indicatorColor = TechYellow
            )
        )
        NavigationBarItem(
            selected = itemSeleccionado == "Favorites",
            onClick = { viewModel.navigateTo(Screen.Favorites) },
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TechBlack,
                indicatorColor = TechYellow
            )
        )
    }
}