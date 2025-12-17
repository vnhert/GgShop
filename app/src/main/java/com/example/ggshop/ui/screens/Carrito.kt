package com.example.ggshop.ui.screens
// Ajusta a tu paquete

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.ggshop.ui.theme.GgShopTheme// Reemplaza RosaPastelAppTheme
import com.example.ggshop.viewmodel.CartItem
import com.example.ggshop.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// --- TOP BAR TECH ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartTopBar(viewModel: MainViewModel) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Carrito de Compras",
                fontWeight = FontWeight.Bold,
                color = TechBlack // Antes Cordovan
            )
        },
        navigationIcon = {
            IconButton(onClick = { viewModel.navigateTo(Screen.MainScreen) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = TechBlack // Antes Cordovan
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

// --- BARRA DE TOTAL / PAGAR ---

@Composable
fun CartCheckoutBar(total: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Total:",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Text(
                total,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = TechBlack // Antes Cordovan
            )
        }

        Button(
            onClick = { /* TODO: acción de checkout tech */ },
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
                .padding(start = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TechYellow, // Antes NewYorkPink
                contentColor = TechBlack
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Pagar",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 4.dp)
                )
                Text(
                    "Pagar Ahora",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

// --- NAV BAR INFERIOR ---

@Composable
private fun BottomNavBarPrincipal(
    viewModel: MainViewModel,
    itemSeleccionado: String
) {
    NavigationBar(
        containerColor = Color.White, // Antes RosaFondoNav
        tonalElevation = 4.dp
    ) {
        // Home
        NavigationBarItem(
            selected = itemSeleccionado == "Home",
            onClick = { viewModel.navigateTo(Screen.MainScreen) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio",
                    modifier = Modifier.size(
                        if (itemSeleccionado == "Home") 36.dp else 28.dp
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TechYellow, // Antes NewYorkPink
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        // Perfil
        NavigationBarItem(
            selected = itemSeleccionado == "Profile",
            onClick = { viewModel.navigateTo(Screen.Profile) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    modifier = Modifier.size(
                        if (itemSeleccionado == "Profile") 36.dp else 28.dp
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TechYellow,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        // Favoritos
        NavigationBarItem(
            selected = itemSeleccionado == "Favorites",
            onClick = { viewModel.navigateTo(Screen.Favorites) },
            icon = {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favoritos",
                    modifier = Modifier.size(
                        if (itemSeleccionado == "Favorites") 36.dp else 28.dp
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TechYellow,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
    }
}

// --- PANTALLA PRINCIPAL ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Carrito(viewModel: MainViewModel) {
    var itemNavSeleccionado by remember { mutableStateOf("Cart") }
    val carrito by viewModel.carrito.collectAsState()

    val total = carrito.sumOf {
        (it.producto.precio.toString().toDoubleOrNull() ?: 0.0) * it.cantidad
    }
    val totalTexto = "$${"%,.0f".format(total)}"

    Scaffold(
        topBar = { CartTopBar(viewModel = viewModel) },
        bottomBar = {
            Column {
                CartCheckoutBar(total = totalTexto)
                BottomNavBarPrincipal(
                    viewModel = viewModel,
                    itemSeleccionado = itemNavSeleccionado
                )
            }
        },
        containerColor = TechWhite
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(carrito) { item ->
                CartItemRow(
                    item = item,
                    onIncrement = {
                        viewModel.actualizarCantidad(
                            productoId = (item.producto.id ?: 0L).toInt(),
                            nuevaCantidad = item.cantidad + 1
                        )
                    },
                    onDecrement = {
                        if (item.cantidad > 1) {
                            viewModel.actualizarCantidad(
                                productoId = (item.producto.id ?: 0L).toInt(),
                                nuevaCantidad = item.cantidad - 1
                            )
                        }
                    },
                    onDelete = {
                        viewModel.eliminarDelCarrito((item.producto.id ?: 0L).toInt())
                    }
                )
            }
        }
    }
}

// --- ITEM DE LA LISTA ---

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit
) {
    val producto = item.producto

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(0.25f)
        ) {
            Card( // Agregué una card para que los gadgets se vean mejor
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(80.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                AsyncImage(
                    model = producto.imagenUrl,
                    contentDescription = producto.nombre,
                    modifier = Modifier.fillMaxSize().background(Color.White),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            QuantitySelectorCart(
                quantity = item.cantidad,
                onIncrement = onIncrement,
                onDecrement = onDecrement
            )
        }

        Column(
            modifier = Modifier
                .weight(0.5f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                fontSize = 15.sp,
                color = TechBlack
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = producto.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 13.sp
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .weight(0.25f)
                .height(80.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onDelete() }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Borrar",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Text(
                text = "$${producto.precio}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = TechBlack
            )
        }
    }

    Divider(
        color = TechGray, // Antes F0F0F0
        thickness = 1.dp,
        modifier = Modifier.padding(top = 16.dp)
    )
}

// --- SELECTOR ---

@Composable
fun QuantitySelectorCart(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(TechGray, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = { if (quantity > 1) onDecrement() },
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(30.dp)
        ) {
            Text("-", color = TechBlack, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            quantity.toString(),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
            color = TechBlack
        )
        TextButton(
            onClick = { onIncrement() },
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(30.dp)
        ) {
            Text("+", color = TechBlack, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CarritoScreenPreview() {
    // Para el preview, como no tenemos la base de datos conectada,
    // lo ideal es no pasarle lógica compleja.
    GgShopTheme {
        // Si tu MainViewModel tiene parámetros, el Preview fallará.
        // Una opción es dejarlo así si no usas parámetros obligatorios:
        // Carrito(viewModel = viewModel())
    }
}