package com.example.ggshop.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ggshop.navigation.Screen
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.ui.theme.TechWhite
import com.example.ggshop.ui.theme.TechGray
import com.example.ggshop.viewmodel.CartItem
import com.example.ggshop.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- TOP BAR TECH ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartTopBar(viewModel: MainViewModel) {
    CenterAlignedTopAppBar(
        title = { Text("Carrito de Compras", fontWeight = FontWeight.Bold, color = TechBlack) },
        navigationIcon = {
            IconButton(onClick = { viewModel.navigateTo(Screen.MainScreen) }) {
                Icon(Icons.Default.ArrowBack, "Volver", tint = TechBlack)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
    )
}

// --- BARRA DE TOTAL / PAGAR (MODIFICADA CON ANIMACIÓN DEL PROFE) ---
@Composable
fun CartCheckoutBar(total: String, viewModel: MainViewModel) {

    // 1. ESTADOS PARA LA ANIMACIÓN (Igual que el ejemplo del profe)
    var isLoading by remember { mutableStateOf(false) }
    var textoBoton by remember { mutableStateOf("Pagar Ahora") }

    // Necesitamos el contexto para el mensaje Toast y el Scope para la corrutina
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Total:", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(total, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = TechBlack)
        }

        Button(
            onClick = {
                // 2. LÓGICA DE LA ANIMACIÓN AL HACER CLICK
                isLoading = true
                textoBoton = "Procesando..."

                // Lanzamos la corrutina (hilo secundario)
                scope.launch {
                    delay(3000L) // Esperamos 3 segundos (Simulación)

                    // Al terminar la espera:
                    isLoading = false
                    textoBoton = "¡Éxito!"

                    // Mostramos mensaje
                    Toast.makeText(context, "¡Pago realizado con éxito!", Toast.LENGTH_LONG).show()

                    delay(500L) // Pequeña pausa para leer el botón

                    // Ejecutamos la lógica real del ViewModel
                    viewModel.finalizarCompra()
                    viewModel.navigateTo(Screen.MainScreen)
                }
            },
            // Bloqueamos el botón si está cargando para que no le den click dos veces
            enabled = !isLoading,
            modifier = Modifier.height(50.dp).weight(1f).padding(start = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TechYellow, contentColor = TechBlack)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 3. MOSTRAMOS EL CIRCULO SI ESTA CARGANDO
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp).padding(end = 8.dp),
                        color = TechBlack,
                        strokeWidth = 3.dp
                    )
                } else {
                    // Si no está cargando, mostramos el icono del carrito normal
                    Icon(Icons.Default.ShoppingCart, "Pagar", modifier = Modifier.size(20.dp).padding(end = 4.dp))
                }

                Text(textoBoton, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
            }
        }
    }
}

// --- NAV BAR INFERIOR ---
@Composable
private fun BottomNavBarPrincipal(viewModel: MainViewModel, itemSeleccionado: String) {
    NavigationBar(containerColor = Color.White, tonalElevation = 4.dp) {
        NavigationBarItem(
            selected = itemSeleccionado == "Home",
            onClick = { viewModel.navigateTo(Screen.MainScreen) },
            icon = { Icon(Icons.Default.Home, "Inicio", modifier = Modifier.size(28.dp)) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = TechYellow, unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = itemSeleccionado == "Profile",
            onClick = { viewModel.navigateTo(Screen.Profile) },
            icon = { Icon(Icons.Default.Person, "Perfil", modifier = Modifier.size(28.dp)) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = TechYellow, unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent)
        )
    }
}

// --- PANTALLA PRINCIPAL ---
@Composable
fun Carrito(viewModel: MainViewModel) {
    var itemNavSeleccionado by remember { mutableStateOf("Cart") }
    // Usamos collectAsState para que la UI se actualice sola si el carrito cambia
    val carrito by viewModel.carrito.collectAsState()

    val total = carrito.sumOf { (it.producto.precio.toDouble()) * it.cantidad }
    val totalTexto = "$${"%,.0f".format(total)}"

    Scaffold(
        topBar = { CartTopBar(viewModel = viewModel) },
        bottomBar = {
            Column {
                // Aquí pasamos el ViewModel a la barra modificada
                CartCheckoutBar(total = totalTexto, viewModel = viewModel)
                BottomNavBarPrincipal(viewModel = viewModel, itemSeleccionado = itemNavSeleccionado)
            }
        },
        containerColor = TechWhite
    ) { innerPadding ->

        // Validación visual si el carrito está vacío
        if (carrito.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Tu carrito está vacío", color = Color.Gray, fontSize = 18.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(carrito) { item ->
                    CartItemRow(
                        item = item,
                        onIncrement = { viewModel.actualizarCantidad(item.producto.id ?: 0L, item.cantidad + 1) },
                        onDecrement = { if (item.cantidad > 1) viewModel.actualizarCantidad(item.producto.id ?: 0L, item.cantidad - 1) },
                        onDelete = { viewModel.eliminarDelCarrito(item.producto.id ?: 0L) }
                    )
                }
            }
        }
    }
}

// --- ITEM DE LA LISTA (Igual que antes) ---
@Composable
fun CartItemRow(item: CartItem, onIncrement: () -> Unit, onDecrement: () -> Unit, onDelete: () -> Unit) {
    val producto = item.producto
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(0.25f)) {
            Card(shape = RoundedCornerShape(8.dp), modifier = Modifier.size(80.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                AsyncImage(model = producto.imagenUrl, contentDescription = producto.nombre, modifier = Modifier.fillMaxSize().background(Color.White), contentScale = ContentScale.Fit)
            }
            Spacer(modifier = Modifier.height(8.dp))
            QuantitySelectorCart(quantity = item.cantidad, onIncrement = onIncrement, onDecrement = onDecrement)
        }
        Column(modifier = Modifier.weight(0.5f).padding(horizontal = 8.dp)) {
            Text(text = producto.nombre, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), fontSize = 15.sp, color = TechBlack)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = producto.descripcion, style = MaterialTheme.typography.bodySmall, color = Color.Gray, fontSize = 13.sp)
        }
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(0.25f).height(80.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onDelete() }) {
                Icon(Icons.Default.Delete, "Eliminar", modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Borrar", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text(text = "$${producto.precio}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold), color = TechBlack)
        }
    }
    Divider(color = TechGray, thickness = 1.dp, modifier = Modifier.padding(top = 16.dp))
}

@Composable
fun QuantitySelectorCart(quantity: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    Row(modifier = Modifier.background(TechGray, shape = RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = { if (quantity > 1) onDecrement() }, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(30.dp)) { Text("-", color = TechBlack, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        Text(quantity.toString(), style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold), color = TechBlack)
        TextButton(onClick = { onIncrement() }, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(30.dp)) { Text("+", color = TechBlack, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
    }
}