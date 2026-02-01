package com.example.ggshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ggshop.viewmodel.MainViewModel
import com.example.ggshop.viewmodel.CartItem // IMPORTACIÓN CLAVE
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Carrito(viewModel: MainViewModel) {
    val carrito by viewModel.carrito.collectAsState()
    val total = if (carrito.isEmpty()) 0 else carrito.sumOf { (it.producto.precio.toInt() * it.cantidad) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tu Carrito", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (carrito.isNotEmpty()) {
                Surface(shadowElevation = 16.dp, color = Color.White) {
                    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total:", style = MaterialTheme.typography.headlineSmall)
                            Text("$${total}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.finalizarCompra() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TechBlack, contentColor = TechYellow),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("FINALIZAR COMPRA", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (carrito.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("El carrito está vacío", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(carrito) { item ->
                    CartItemRow(item, viewModel)
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, viewModel: MainViewModel) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.producto.imagenUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(item.producto.nombre, fontWeight = FontWeight.Bold)
                Text("$${item.producto.precio}")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Usamos el ID del producto directamente
                IconButton(onClick = { viewModel.actualizarCantidad(item.producto.id, item.cantidad - 1) }) {
                    Icon(Icons.Default.Remove, null)
                }
                Text("${item.cantidad}")
                IconButton(onClick = { viewModel.actualizarCantidad(item.producto.id, item.cantidad + 1) }) {
                    Icon(Icons.Default.Add, null)
                }
                IconButton(onClick = { viewModel.eliminarDelCarrito(item.producto.id) }) {
                    Icon(Icons.Default.Delete, null, tint = Color.Red)
                }
            }
        }
    }
}