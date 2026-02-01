package com.example.ggshop.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ggshop.data.*
import com.example.ggshop.navigation.Screen
import com.example.ggshop.navigation.NavigationEvent
import com.example.ggshop.repository.ProductoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartItem(
    val producto: Producto,
    val cantidad: Int
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val productoRepository = ProductoRepository()
    private var ultimoProductoEliminado: Producto? = null

    // --- ESTADOS DE UI ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos = _productos.asStateFlow()

    private val _carrito = MutableStateFlow<List<CartItem>>(emptyList())
    val carrito = _carrito.asStateFlow()

    private val _historialVentas = MutableStateFlow<List<PedidoResponse>>(emptyList())
    val historialVentas = _historialVentas.asStateFlow()

    private val _listaClientes = MutableStateFlow<List<Usuario>>(emptyList())
    val listaClientes = _listaClientes.asStateFlow()

    // --- SESIÓN ---
    val usuarioLogueadoNombre = MutableStateFlow("")
    val usuarioLogueadoEmail = MutableStateFlow("")
    val profileImageUri = MutableStateFlow<android.net.Uri?>(null)
    val esAdmin = MutableStateFlow(false)

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    private val _loginError = MutableStateFlow(false)
    val loginError = _loginError.asStateFlow()

    private val _productoSeleccionado = MutableStateFlow<Producto?>(null)
    val productoSeleccionado = _productoSeleccionado.asStateFlow()

    private val _favoritos = MutableStateFlow<List<Producto>>(emptyList())
    val favoritos = _favoritos.asStateFlow()

    private val _puntosUsuario = MutableStateFlow(0)
    val puntosUsuario = _puntosUsuario.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        cargarTodo()
    }

    fun cargarTodo() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _productos.value = productoRepository.getProductosDeBackend()
                _listaClientes.value = productoRepository.obtenerTodosLosUsuarios()
                _historialVentas.value = productoRepository.obtenerTodasLasVentas()
            } catch (e: Exception) {
                Log.e("VM", "Error carga: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- 1. FINALIZAR COMPRA (CORREGIDO Y ACTUALIZADO) ---
    fun finalizarCompra() {
        viewModelScope.launch {
            _isLoading.value = true
            val itemsActuales = _carrito.value

            if (itemsActuales.isNotEmpty()) {
                delay(2000)

                // DATOS GENERALES
                val totalVenta = itemsActuales.sumOf { it.producto.precio * it.cantidad }
                val resumenProductos = itemsActuales.joinToString("\n") { "${it.cantidad}x ${it.producto.nombre}" }

                // Email del comprador actual
                val compradorEmail = if (usuarioLogueadoEmail.value.isNotBlank()) usuarioLogueadoEmail.value else "Invitado"

                // A. ACTUALIZAR LISTA LOCAL (Feedback inmediato)
                // --- CORRECCIÓN AQUÍ: Convertimos ID a String y usamos 'clienteId' ---
                val nuevaVentaLocal = PedidoResponse(
                    id = System.currentTimeMillis().toString(), // .toString() para que sea compatible
                    total = totalVenta,
                    clienteId = compradorEmail, // Cambiado de clienteEmail a clienteId para coincidir con tu Data Class
                    itemsResumen = resumenProductos
                )
                _historialVentas.value = listOf(nuevaVentaLocal) + _historialVentas.value

                // B. ENVIAR VENTA AL BACKEND
                val itemsParaBackend = itemsActuales.map { item ->
                    ItemPedidoRequest(
                        productoId = item.producto.id,
                        cantidad = item.cantidad,
                        precioUnitario = item.producto.precio,
                        subtotal = item.producto.precio * item.cantidad
                    )
                }

                // --- LÓGICA DE DETECCIÓN DE ID ---
                // 1. Buscamos al usuario en la lista descargada
                val usuarioEncontrado = _listaClientes.value.find { it.email == compradorEmail }

                // 2. Imprimimos en Logcat qué encontramos (PARA DEPURAR)
                if (usuarioEncontrado != null) {
                    Log.e("PRUEBA_ID", "Usuario encontrado: ${usuarioEncontrado.nombre}")
                    Log.e("PRUEBA_ID", "ID REAL (raw): ${usuarioEncontrado.id}")
                } else {
                   Log.e("PRUEBA_ID", "¡NO SE ENCONTRÓ EL USUARIO EN LA LISTA LOCAL!")
                }

                // 3. Selección inteligente del ID
                val idParaEnviar = if (usuarioEncontrado != null &&
                    usuarioEncontrado.id.toString() != "0" &&
                    usuarioEncontrado.id.toString().isNotEmpty()) {
                    // Si tenemos un ID válido (numérico o texto), lo usamos
                    usuarioEncontrado.id.toString()
                } else {
                    // PLAN B: Si no hay ID o es 0, enviamos el Email.
                    Log.e("PRUEBA_ID", "Usando Email como ID de respaldo")
                    compradorEmail
                }

                Log.e("PRUEBA_ID", "ENVIANDO AL SERVIDOR: clienteId = $idParaEnviar")

                val pedidoParaBackend = PedidoRequest(
                    clienteId = idParaEnviar,
                    total = totalVenta,
                    items = itemsParaBackend
                )

                try {
                    val ventaGuardada = productoRepository.enviarPedido(pedidoParaBackend)

                    // Aceptamos cualquier respuesta que no sea nula como éxito
                    if (ventaGuardada != null) {
                        Log.d("VM", "Venta guardada en servidor correctamente")
                        Toast.makeText(getApplication(), "¡Compra registrada exitosamente!", Toast.LENGTH_LONG).show()
                    } else {
                        Log.e("VM", "El servidor no devolvió respuesta")
                        Toast.makeText(getApplication(), "Error: Sin respuesta del servidor", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    // Si entra aquí es un error de red real, o un error de parsing si el servidor manda algo raro
                    Log.e("VM", "Excepción al enviar: ${e.message}")
                    // Como sabemos que el servidor sí guarda, a veces es solo error de lectura de respuesta.
                    // Podrías poner un Toast de éxito aquí si confirmas que se guarda en backend.
                    Toast.makeText(getApplication(), "Procesado (Verificar historial)", Toast.LENGTH_LONG).show()
                }

                // C. ACTUALIZAR STOCK
                val listaProductosActualizada = _productos.value.map { productoEnStock ->
                    val itemEnCarrito = itemsActuales.find { it.producto.id == productoEnStock.id }
                    if (itemEnCarrito != null) {
                        val nuevoStock = (productoEnStock.stock - itemEnCarrito.cantidad).coerceAtLeast(0)
                        productoEnStock.copy(stock = nuevoStock)
                    } else {
                        productoEnStock
                    }
                }
                _productos.value = listaProductosActualizada

                itemsActuales.forEach { itemCarrito ->
                    val productoConNuevoStock = listaProductosActualizada.find { it.id == itemCarrito.producto.id }
                    if (productoConNuevoStock != null) {
                        try {
                            productoRepository.actualizarProducto(productoConNuevoStock)
                        } catch (e: Exception) { Log.e("VM", "Error stock: ${e.message}") }
                    }
                }

            } else {
                Toast.makeText(getApplication(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
            }

            _carrito.value = emptyList()
            _isLoading.value = false
            navigateTo(Screen.MainScreen)
        }
    }

    // --- 2. GESTIÓN DE PRODUCTOS ---
    fun eliminarProducto(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500)
            val productoEncontrado = _productos.value.find { it.id == id }
            val exito = productoRepository.eliminarProducto(id)
            if (exito) {
                ultimoProductoEliminado = productoEncontrado
                _productos.value = _productos.value.filterNot { it.id == id }
                Toast.makeText(getApplication(), "Eliminado con éxito", Toast.LENGTH_SHORT).show()
            }
            _isLoading.value = false
        }
    }

    fun guardarNuevoProducto(producto: Producto) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500)
            val exito = productoRepository.crearProducto(producto)
            if (exito) {
                _productos.value = _productos.value + producto
                Toast.makeText(getApplication(), "Producto añadido con éxito", Toast.LENGTH_SHORT).show()
            }
            _isLoading.value = false
        }
    }

    fun actualizarProductoExistente(producto: Producto) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500)
            val exito = productoRepository.actualizarProducto(producto)
            if (exito) {
                _productos.value = _productos.value.map {
                    if (it.id == producto.id) producto else it
                }
                Toast.makeText(getApplication(), "Producto editado con éxito", Toast.LENGTH_SHORT).show()
            }
            _isLoading.value = false
        }
    }

    // --- 3. LOGIN ---
    fun validarLogin() {
        val mailInput = email.value
        val passInput = password.value
        _isLoading.value = true
        viewModelScope.launch {
            delay(1000)
            if (mailInput == "admin@ggshop.com" && passInput == "admin123") {
                usuarioLogueadoNombre.value = "Administrador"
                usuarioLogueadoEmail.value = mailInput
                esAdmin.value = true
                _loginError.value = false
                Toast.makeText(getApplication(), "Modo Administrador Activado 🛠️", Toast.LENGTH_SHORT).show()
                _isLoading.value = false
                navigateTo(Screen.MainScreen)
                return@launch
            }
            val usuarioEncontrado = _listaClientes.value.find { it.email == mailInput && it.password == passInput }
            if (usuarioEncontrado != null) {
                usuarioLogueadoNombre.value = usuarioEncontrado.nombre
                usuarioLogueadoEmail.value = usuarioEncontrado.email
                esAdmin.value = false
                _loginError.value = false
                Toast.makeText(getApplication(), "Bienvenido/a, ${usuarioEncontrado.nombre} 👋", Toast.LENGTH_SHORT).show()
                _isLoading.value = false
                navigateTo(Screen.MainScreen)
            } else {
                _isLoading.value = false
                _loginError.value = true
                Toast.makeText(getApplication(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- RESTO DE FUNCIONES ---
    fun deshacerEliminacion() {
        ultimoProductoEliminado?.let { producto ->
            viewModelScope.launch {
                productoRepository.crearProducto(producto)
                _productos.value = _productos.value + producto
                ultimoProductoEliminado = null
                Toast.makeText(getApplication(), "Restaurado", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun registrarUsuario(n: String, c: String, p: String, d: String) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)
            val exito = productoRepository.registrarUsuario(n, c, p)
            if (exito) {
                val nuevoUsuario = Usuario(nombre = n, email = c, password = p, direccion = d)
                _listaClientes.value = _listaClientes.value + nuevoUsuario
                _isLoading.value = false
                navigateTo(Screen.Login)
                Toast.makeText(getApplication(), "Cuenta creada. Inicia sesión.", Toast.LENGTH_LONG).show()
            } else {
                _isLoading.value = false
            }
        }
    }
    fun cerrarSesion() {
        email.value = ""; password.value = ""; usuarioLogueadoNombre.value = ""; usuarioLogueadoEmail.value = ""; esAdmin.value = false; _puntosUsuario.value = 0; navigateTo(Screen.Login)
    }
    fun cargarProductos() { viewModelScope.launch { try { _productos.value = productoRepository.getProductosDeBackend() } catch (e: Exception) { Log.e("VM", "Error carga: ${e.message}") } } }
    fun seleccionarProducto(p: Producto) { _productoSeleccionado.value = p; navigateTo(Screen.Detail) }
    fun navegarAAñadirProducto() { _productoSeleccionado.value = null; navigateTo(Screen.Inventory) }
    fun seleccionarProductoParaEditar(p: Producto) { _productoSeleccionado.value = p; navigateTo(Screen.Inventory) }
    fun canjearPuntosPorJuego(juego: String, horas: Int) { _puntosUsuario.value += (horas * 25) }
    fun actualizarDatosUsuario(n: String, e: String, p: String) { usuarioLogueadoNombre.value = n; usuarioLogueadoEmail.value = e }
    fun agregarAlCarrito(p: Producto, cantidad: Int = 1) {
        val lista = _carrito.value.toMutableList()
        val itemExistente = lista.find { it.producto.id == p.id }
        if (itemExistente != null) {
            val index = lista.indexOf(itemExistente)
            lista[index] = itemExistente.copy(cantidad = itemExistente.cantidad + cantidad)
        } else { lista.add(CartItem(p, cantidad)) }
        _carrito.value = lista
        Toast.makeText(getApplication(), "Agregado al carrito", Toast.LENGTH_SHORT).show()
    }
    fun actualizarCantidad(id: Long, nueva: Int) { _carrito.value = _carrito.value.map { if (it.producto.id == id) it.copy(cantidad = nueva.coerceAtLeast(1)) else it } }
    fun eliminarDelCarrito(id: Long) { _carrito.value = _carrito.value.filterNot { it.producto.id == id } }
    fun toggleFavorito(p: Producto) {
        val lista = _favoritos.value.toMutableList()
        if (lista.any { it.id == p.id }) lista.removeAll { it.id == p.id } else lista.add(p)
        _favoritos.value = lista
    }
    fun updateProfileImage(uri: android.net.Uri?) { profileImageUri.value = uri }
    fun navigateTo(s: Screen) { viewModelScope.launch { _navigationEvents.emit(NavigationEvent.NavigateTo(s)) } }
    fun navigateBack() { viewModelScope.launch { _navigationEvents.emit(NavigationEvent.PopBackStack) } }
    fun navigateUp() { viewModelScope.launch { _navigationEvents.emit(NavigationEvent.NavigateUp) } }
    fun onEmailChange(v: String) { email.value = v; _loginError.value = false }
    fun onPasswordChange(v: String) { password.value = v; _loginError.value = false }
    val isLoginValid: StateFlow<Boolean> = combine(email, password) { e, p -> e.isNotEmpty() && p.length >= 4 }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
}