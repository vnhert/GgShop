package com.example.ggshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ggshop.data.Producto
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorInventario(viewModel: MainViewModel) {
    // Obtenemos la lista de productos del ViewModel
    val productos by viewModel.productos.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("GESTIÓN DE INVENTARIO",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = TechBlack,
                    titleContentColor = TechYellow,
                    navigationIconContentColor = TechYellow
                )
            )
        },
        floatingActionButton = {
            // Botón para añadir nuevo producto (IE 2.4.1 Acciones de Admin)
            FloatingActionButton(
                containerColor = TechYellow,
                contentColor = TechBlack,
                onClick = { /* Aquí iría la lógica para añadir producto */ }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
            }
        },
        containerColor = Color(0xFFF5F5F5) // Gris muy claro de fondo
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {

            // Resumen de Inventario
            ResumenInventario(productos.size)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(productos) { producto ->
                    ItemInventario(producto)
                }
            }
        }
    }
}

@Composable
fun ResumenInventario(total: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total de Productos", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text("$total Items registrados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ItemInventario(producto: Producto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TechBlack
                )
                Text(
                    text = "Categoría: ${producto.categoria}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Badge de Stock
                Surface(
                    color = if (producto.stock > 5) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Stock: ${producto.stock}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (producto.stock > 5) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(
                onClick = { /* Lógica de edición */ },
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFF0F0F0))
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TechBlack)
            }
        }
    }
}