package com.example.ggshop.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ggshop.data.Producto
import com.example.ggshop.navigation.Screen
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Favoritos(viewModel: MainViewModel = viewModel()) {
    // 1. Conectamos con la lista de favoritos en tiempo real
    val favoritos by viewModel.favoritos.collectAsState()

    Scaffold(
        topBar = { TopBarFavoritos(viewModel) },
        bottomBar = {
            BottomNavBarPrincipal(viewModel = viewModel, itemSeleccionado = "Favorites")
        },
        containerColor = Color.White
    ) { paddingValues ->

        // 2. Lógica de visualización: ¿Lista vacía o con productos?
        if (favoritos.isEmpty()) {
            EmptyFavoritesState(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favoritos) { producto ->
                    FavoriteItemRow(
                        producto = producto,
                        onDelete = { viewModel.toggleFavorito(producto) },
                        onAddToCart = {
                            viewModel.agregarAlCarrito(producto, 1)
                            viewModel.navigateTo(Screen.Cart)
                        },
                        onItemClick = {
                            viewModel.seleccionarProducto(producto)
                            viewModel.navigateTo(Screen.Detail)
                        }
                    )
                }
            }
        }
    }
}

// --- ITEM DE LA LISTA (Tarjeta de producto) ---
@Composable
fun FavoriteItemRow(
    producto: Producto,
    onDelete: () -> Unit,
    onAddToCart: () -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() } // Click en la tarjeta abre el detalle
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(80.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                AsyncImage(
                    model = producto.imagenUrl,
                    contentDescription = producto.nombre,
                    modifier = Modifier.fillMaxSize().padding(4.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TechBlack
                )
                Text(
                    text = "$${producto.precio}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Botón pequeño "Comprar"
                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TechYellow,
                        contentColor = TechBlack
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Filled.ShoppingCart, null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Comprar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Botón Eliminar
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Gray
                )
            }
        }
    }
}

// --- ESTADO VACÍO (Cuando no hay favoritos) ---
@Composable
fun EmptyFavoritesState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = TechYellow
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "TU LISTA DE DESEOS ESTÁ VACÍA",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = TechBlack,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Guarda aquí lo que quieras comprar después.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

// --- TOP BAR ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarFavoritos(viewModel: MainViewModel) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "FAVORITOS",
                fontWeight = FontWeight.ExtraBold,
                color = TechBlack,
                letterSpacing = 2.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = { viewModel.navigateTo(Screen.MainScreen) }) {
                Icon(Icons.Filled.ArrowBack, "Volver", tint = TechBlack)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
}

// --- BOTTOM NAV BAR (Reutilizada) ---
@Composable
private fun BottomNavBarPrincipal(viewModel: MainViewModel, itemSeleccionado: String) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = itemSeleccionado == "Home",
            onClick = { viewModel.navigateTo(Screen.MainScreen) },
            icon = { Icon(Icons.Default.Home, "Inicio") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = TechBlack, indicatorColor = TechYellow)
        )
        NavigationBarItem(
            selected = itemSeleccionado == "Profile",
            onClick = { viewModel.navigateTo(Screen.Profile) },
            icon = { Icon(Icons.Filled.Person, "Perfil") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = TechBlack, indicatorColor = TechYellow)
        )
        NavigationBarItem(
            selected = itemSeleccionado == "Favorites",
            onClick = { /* Activo */ },
            icon = { Icon(Icons.Filled.Favorite, "Favoritos") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = TechBlack, indicatorColor = TechYellow)
        )
    }
}