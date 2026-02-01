package com.example.ggshop.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ggshop.data.Producto
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.viewmodel.MainViewModel
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilAdministrador(viewModel: MainViewModel) {
    var tabIndex by remember { mutableIntStateOf(0) }

    val clientes by viewModel.listaClientes.collectAsState()
    val ventas by viewModel.historialVentas.collectAsState()
    val productos by viewModel.productos.collectAsState()

    // 1. ESCUCHAMOS EL ESTADO DE CARGA (Para la animación)
    val isLoading by viewModel.isLoading.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var productoEditando by remember { mutableStateOf<Producto?>(null) }

    var tempNombre by remember { mutableStateOf("") }
    var tempPrecio by remember { mutableStateOf("") }
    var tempStock by remember { mutableStateOf("") }
    var tempCategoria by remember { mutableStateOf("GAMING") }

    // Variable para la URL (Tu código)
    var tempUrlString by remember { mutableStateOf("") }

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        tempImageUri = uri
    }

    fun abrirDialog(producto: Producto?) {
        productoEditando = producto
        if (producto != null) {
            tempNombre = producto.nombre
            tempPrecio = producto.precio.toString()
            tempStock = producto.stock.toString()
            tempCategoria = producto.categoria
            // Cargamos URL y URI
            tempUrlString = producto.imagenUrl
            tempImageUri = producto.imageUri?.let { Uri.parse(it) }
        } else {
            tempNombre = ""
            tempPrecio = ""
            tempStock = ""
            tempCategoria = "GAMING"
            tempUrlString = ""
            tempImageUri = null
        }
        showDialog = true
    }

    // ENVOLVEMOS TODO EN UN BOX PARA PONER LA ANIMACIÓN ENCIMA
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("PANEL DE CONTROL", fontWeight = FontWeight.ExtraBold, color = TechYellow) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = TechBlack),
                    navigationIcon = {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(Icons.Default.ArrowBack, null, tint = TechYellow)
                        }
                    }
                )
            },
            floatingActionButton = {
                if (tabIndex == 2) {
                    FloatingActionButton(onClick = { abrirDialog(null) }, containerColor = TechYellow, contentColor = TechBlack) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo Producto")
                    }
                }
            },
            containerColor = Color.White
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {

                TabRow(
                    selectedTabIndex = tabIndex,
                    containerColor = TechBlack,
                    contentColor = TechYellow,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[tabIndex]), color = TechYellow)
                    }
                ) {
                    Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("CLIENTES", fontWeight = FontWeight.Bold, fontSize = 12.sp) }, icon = { Icon(Icons.Default.Person, null) })
                    Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("VENTAS", fontWeight = FontWeight.Bold, fontSize = 12.sp) }, icon = { Icon(Icons.Default.ShoppingCart, null) })
                    Tab(selected = tabIndex == 2, onClick = { tabIndex = 2 }, text = { Text("STOCK", fontWeight = FontWeight.Bold, fontSize = 12.sp) }, icon = { Icon(Icons.Default.List, null) })
                }

                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    when (tabIndex) {
                        0 -> { // CLIENTES
                            if (clientes.isEmpty()) MensajeVacio("No hay clientes registrados.")
                            else LazyColumn {
                                items(clientes) { cliente ->
                                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                        Column(Modifier.padding(16.dp)) {
                                            Text(cliente.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                                            Text(cliente.email, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                        1 -> { // VENTAS
                            if (ventas.isEmpty()) MensajeVacio("No hay historial de ventas.")
                            else LazyColumn {
                                items(ventas) { venta ->
                                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                        Column(Modifier.padding(16.dp)) {
                                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                // --- CORRECCIÓN AQUÍ: clienteId en lugar de clienteEmail ---
                                                Text(venta.clienteId, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                // -----------------------------------------------------------
                                                Text("$${venta.total}", color = Color(0xFF2E7D32), fontWeight = FontWeight.ExtraBold)
                                            }
                                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                                            Text(text = venta.itemsResumen, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 18.sp)
                                        }
                                    }
                                }
                            }
                        }
                        2 -> { // STOCK
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    Card(colors = CardDefaults.cardColors(containerColor = TechBlack), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                                        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text("Total Productos:", color = Color.White)
                                            Text("${productos.size}", color = TechYellow, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                                        }
                                    }
                                }
                                items(productos) { producto ->
                                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = producto.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TechBlack)
                                                Text(text = "Cat: ${producto.categoria} | $${producto.precio}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Surface(color = if (producto.stock < 5) Color(0xFFFFEBEE) else Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp)) {
                                                    Text(text = "Stock: ${producto.stock}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = if (producto.stock < 5) Color(0xFFC62828) else Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            // 2. BOTONES DE ACCIÓN (LÁPIZ + BASURERO)
                                            Row {
                                                IconButton(onClick = { abrirDialog(producto) }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Gray)
                                                }
                                                // Botón rojo para eliminar
                                                IconButton(onClick = { viewModel.eliminarProducto(producto.id) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFE53935))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = if (productoEditando == null) "Nuevo Producto" else "Editar Producto") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = tempNombre, onValueChange = { tempNombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = tempPrecio, onValueChange = { tempPrecio = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = tempStock, onValueChange = { tempStock = it }, label = { Text("Stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = tempCategoria, onValueChange = { tempCategoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())

                            // CAMPO URL
                            OutlinedTextField(
                                value = tempUrlString,
                                onValueChange = { tempUrlString = it },
                                label = { Text("URL de Imagen (Opcional)") },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("https://...") }
                            )

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            // SUBIDA DE FOTO
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(onClick = { launcher.launch("image/*") }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                                    Text("O subir Foto", fontSize = 12.sp)
                                }
                                if (tempImageUri != null) {
                                    Image(painter = rememberAsyncImagePainter(tempImageUri), contentDescription = "Preview", modifier = Modifier.size(50.dp))
                                    Text("Foto OK", fontSize = 10.sp, color = Color.Green)
                                } else {
                                    Text("Sin foto local", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (tempNombre.isNotBlank() && tempPrecio.isNotBlank()) {

                                    val imagenFinal = if (tempUrlString.isNotBlank()) tempUrlString else (tempImageUri?.toString() ?: "")

                                    if (productoEditando == null) {
                                        viewModel.guardarNuevoProducto(
                                            Producto(
                                                id = System.currentTimeMillis(),
                                                nombre = tempNombre,
                                                descripcion = "Descripción Admin",
                                                precio = tempPrecio.toDoubleOrNull() ?: 0.0,
                                                stock = tempStock.toIntOrNull() ?: 0,
                                                imagenUrl = imagenFinal,
                                                categoria = tempCategoria,
                                                imageUri = tempImageUri?.toString()
                                            )
                                        )
                                    } else {
                                        val productoActualizado = productoEditando!!.copy(
                                            nombre = tempNombre,
                                            precio = tempPrecio.toDoubleOrNull() ?: 0.0,
                                            stock = tempStock.toIntOrNull() ?: 0,
                                            categoria = tempCategoria,
                                            imagenUrl = imagenFinal,
                                            imageUri = tempImageUri?.toString() ?: productoEditando!!.imageUri
                                        )
                                        viewModel.actualizarProductoExistente(productoActualizado)
                                    }
                                    showDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TechBlack, contentColor = TechYellow)
                        ) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        Row {
                            // Mantenemos el eliminar aquí también por si acaso
                            if (productoEditando != null) {
                                TextButton(
                                    onClick = {
                                        viewModel.eliminarProducto(productoEditando!!.id)
                                        showDialog = false
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                                ) {
                                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                                    Text("Eliminar")
                                }
                            }
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    }
                )
            }
        }

        // 3. AQUÍ ESTÁ LA ANIMACIÓN DE CARGA (OVERLAY)
        // Se dibuja ENCIMA del Scaffold cuando isLoading es true
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) { }, // Bloquea los clicks
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TechYellow)
            }
        }
    }
}

@Composable
fun MensajeVacio(texto: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = texto, color = Color.Gray, fontWeight = FontWeight.Medium)
    }
}