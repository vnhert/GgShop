package com.example.ggshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ggshop.data.Producto
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProducto(viewModel: MainViewModel) {
    val productoAEditar by viewModel.productoSeleccionado.collectAsState()

    // Inicializamos estados con los datos del modelo
    var nombre by remember { mutableStateOf(productoAEditar?.nombre ?: "") }
    var precio by remember { mutableStateOf(productoAEditar?.precio?.toString() ?: "") }
    var stock by remember { mutableStateOf(productoAEditar?.stock?.toString() ?: "") }
    var descripcion by remember { mutableStateOf(productoAEditar?.descripcion ?: "") }
    var categoria by remember { mutableStateOf(productoAEditar?.categoria ?: "") }
    var imagenUrl by remember { mutableStateOf(productoAEditar?.imagenUrl ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (productoAEditar == null) "AÑADIR PRODUCTO" else "EDITAR STOCK",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TechYellow)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio (Int)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = imagenUrl, onValueChange = { imagenUrl = it }, label = { Text("URL de Imagen") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val p = Producto(
                        id = productoAEditar?.id ?: 0,
                        nombre = nombre,
                        descripcion = descripcion,
                        precio = precio.toDoubleOrNull() ?: 0.0, // Ajustado a Int según tu modelo
                        stock = stock.toIntOrNull() ?: 0,
                        imagenUrl = imagenUrl, // Ahora coincide perfectamente
                        categoria = categoria
                    )

                    if (productoAEditar == null) {
                        viewModel.guardarNuevoProducto(p)
                    } else {
                        viewModel.actualizarProductoExistente(p)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = TechBlack)
            ) {
                Text(
                    if (productoAEditar == null) "GUARDAR" else "ACTUALIZAR",
                    color = TechYellow,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}