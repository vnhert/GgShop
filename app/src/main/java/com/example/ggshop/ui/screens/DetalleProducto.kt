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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ggshop.R
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProducto(viewModel: MainViewModel) {
    val context = LocalContext.current
    val productoSeleccionado by viewModel.productoSeleccionado.collectAsState()
    val favoritos by viewModel.favoritos.collectAsState()
    var quantity by remember { mutableIntStateOf(1) }

    // Si no hay producto seleccionado, mostramos carga o volvemos atrás
    if (productoSeleccionado == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TechBlack)
        }
        return
    }

    val producto = productoSeleccionado!!
    val esFavorito = favoritos.any { it.id == producto.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    // ARREGLO NAVEGACIÓN: Usamos navigateBack() del ViewModel
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TechBlack
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            // --- 1. IMAGEN GRANDE ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(producto.imagenUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.size(250.dp), // Tamaño controlado
                    contentScale = ContentScale.Fit,
                    error = painterResource(R.drawable.logo),
                    placeholder = painterResource(R.drawable.logo)
                )

                // Botón de Favorito flotante
                IconButton(
                    onClick = { viewModel.toggleFavorito(producto) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(50))
                ) {
                    Icon(
                        imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = if (esFavorito) Color.Red else TechBlack
                    )
                }
            }

            // --- 2. INFORMACIÓN DEL PRODUCTO ---
            Column(modifier = Modifier.padding(24.dp)) {
                // Categoría y Calificación (RECUPERADO)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = producto.categoria.uppercase(),
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // Estrellas ficticias (puedes hacerlas dinámicas luego)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = TechYellow, modifier = Modifier.size(16.dp))
                        Text(" 4.8", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(" (120 reviews)", color = Color.Gray, fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Nombre
                Text(
                    text = producto.nombre,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                    color = TechBlack,
                    lineHeight = 32.sp
                )

                // Precio
                Text(
                    text = "$${producto.precio}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = TechBlack,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))

                // Descripción (RECUPERADO)
                Text(
                    text = "Descripción",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TechBlack
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = producto.descripcion.ifEmpty { "Sin descripción disponible para este producto gamer de alta gama." },
                    color = Color.Gray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(24.dp))

                // Selector de Cantidad
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("CANTIDAD:", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0F0F0),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (quantity > 1) quantity-- }) {
                                Icon(Icons.Default.Remove, null, tint = TechBlack)
                            }
                            Text("$quantity", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                            IconButton(onClick = { quantity++ }) {
                                Icon(Icons.Default.Add, null, tint = TechBlack)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Botón Añadir al Carrito
                Button(
                    onClick = {
                        viewModel.agregarAlCarrito(producto, quantity)
                        Toast.makeText(context, "Añadido al carrito", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TechYellow, contentColor = TechBlack),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("AÑADIR AL CARRITO", fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            }
        }
    }
}