package com.example.ggshop.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ggshop.R
import com.example.ggshop.data.Producto
import com.example.ggshop.data.room.AppDatabase
import com.example.ggshop.data.room.FavoritoEntity
import com.example.ggshop.data.room.UsuarioEntity
import com.example.ggshop.data.room.VentaEntity
import com.example.ggshop.navigation.NavigationEvent
import com.example.ggshop.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import com.example.ggshop.repository.ProductoRepository
import android.util.Log

data class CartItem(val producto: Producto, val cantidad: Int)
data class Cliente(val nombre: String, val email: String)
data class Venta(val clienteEmail: String, val itemsResumen: String, val total: Int)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // --- NUEVO: REPOSITORIO PARA API ---
    private val productoRepository = ProductoRepository()

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.usuariosDao()
    private val favoritosDao = db.favoritosDao()
    private val ventasDao = db.ventasDao()

    private val prefs: SharedPreferences =
        application.getSharedPreferences("GGShop_Prefs", Context.MODE_PRIVATE)

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    fun navigateTo(screen: Screen) { viewModelScope.launch { _navigationEvents.emit(NavigationEvent.NavigateTo(screen)) } }
    fun navigateBack() { viewModelScope.launch { _navigationEvents.emit(NavigationEvent.PopBackStack) } }
    fun navigateUp() { viewModelScope.launch { _navigationEvents.emit(NavigationEvent.NavigateUp) } }

    // --- SESIÓN ---
    private val _usuarioLogueadoNombre = MutableStateFlow("Invitado")
    val usuarioLogueadoNombre: StateFlow<String> = _usuarioLogueadoNombre.asStateFlow()

    private val _usuarioLogueadoEmail = MutableStateFlow("invitado@ggshop.com")
    val usuarioLogueadoEmail: StateFlow<String> = _usuarioLogueadoEmail.asStateFlow()

    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri: StateFlow<Uri?> = _profileImageUri.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _loginError = MutableStateFlow(false)
    val loginError: StateFlow<Boolean> = _loginError.asStateFlow()

    fun onEmailChange(v: String) {
        _email.value = v
        _loginError.value = false
    }
    fun onPasswordChange(v: String) {
        _password.value = v
        _loginError.value = false
    }

    // --- PUNTOS ---
    private val _puntosUsuario = MutableStateFlow(0)
    val puntosUsuario: StateFlow<Int> = _puntosUsuario.asStateFlow()

    val isLoginValid: StateFlow<Boolean> = combine(_email, _password) { email, pass ->
        email.contains("@") && pass.length >= 6
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin.asStateFlow()

    private val _listaClientes = MutableStateFlow<List<Cliente>>(emptyList())
    val listaClientes: StateFlow<List<Cliente>> = _listaClientes.asStateFlow()

    private val _historialVentas = MutableStateFlow<List<Venta>>(emptyList())
    val historialVentas: StateFlow<List<Venta>> = _historialVentas.asStateFlow()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _favoritos = MutableStateFlow<List<Producto>>(emptyList())
    val favoritos: StateFlow<List<Producto>> = _favoritos.asStateFlow()

    init {
        crearUsuarioAdminPorDefecto()

        // MODIFICADO: Ahora intenta cargar desde el Backend primero
        cargarProductosDesdeBackend()

        cargarCarritoPersistido()
        cargarFavoritosPersistidos()

        viewModelScope.launch(Dispatchers.IO) {
            dao.obtenerTodosEnTiempoReal().collect { listaEntidades ->
                _listaClientes.value = listaEntidades.map { Cliente(it.nombre, it.email) }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            ventasDao.obtenerHistorialVentas().collect { listaVentasDB ->
                _historialVentas.value = listaVentasDB.map {
                    Venta(it.usuarioEmail, it.itemsResumen, it.total)
                }
            }
        }
    }

    // --- NUEVO: CARGAR PRODUCTOS DESDE EL BACKEND (GUÍA 14) ---
    fun cargarProductosDesdeBackend() {
        viewModelScope.launch {
            try {
                val listaApi = productoRepository.getProductos()
                if (listaApi.isNotEmpty()) {
                    _productos.value = listaApi
                } else {
                    cargarProductosLocales()
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al conectar con el backend: ${e.message}")
                // Respaldo: Si el backend falla, carga los manuales
                cargarProductosLocales()
            }
        }
    }

    // --- REFACTORIZADO: Tu lista original ahora es un respaldo ---
    private fun cargarProductosLocales() {
        if (_productos.value.isEmpty()) {
            _productos.value = listOf(
                // Agregamos .toString() a los recursos R.drawable
                Producto(1L, "Logitech G502 Hero", "Sensor HERO 25K", 49990, 15, R.drawable.mousee.toString(), "GAMING"),
                Producto(2L, "Teclado Redragon", "Mecánico TKL", 32990, 8, R.drawable.teclado.toString(), "GAMING"),
                Producto(5L, "Audífonos HyperX", "Surround 7.1", 85990, 10, R.drawable.audi.toString(), "GAMING"),
                Producto(6L, "Monitor ASUS TUF", "165Hz, 1ms", 249990, 4, R.drawable.moni.toString(), "GAMING"),
                Producto(7L, "Mousepad SteelSeries", "Tela micro-tejida", 15990, 20, R.drawable.mousepad.toString(), "GAMING"),
                Producto(8L, "Silla Gamer Corsair", "Ergonómica", 299990, 2, R.drawable.silla.toString(), "GAMING"),
                Producto(3L, "iPhone 15 128GB", "Chip A16", 799990, 5, R.drawable.iphone.toString(), "CELULARES"),
                Producto(4L, "Galaxy S24 Ultra", "QHD+ y S-Pen", 1199990, 3, R.drawable.samsung.toString(), "CELULARES"),
                Producto(10L, "Pixel 8 Pro", "IA Google", 899990, 4, R.drawable.google.toString(), "CELULARES"),
                Producto(11L, "Moto Edge 40", "Pantalla curva", 279990, 7, R.drawable.motorola.toString(), "CELULARES"),
                Producto(12L, "Nothing Phone (2)", "Glyph Interface", 549990, 6, R.drawable.nothing.toString(), "CELULARES"),
                Producto(13L, "Xiaomi 13T Pro", "Lente Leica", 599990, 9, R.drawable.xiaomi.toString(), "CELULARES")
            )
        }
    }

    // --- EL RESTO DE TUS FUNCIONES SE MANTIENEN INTACTAS ---

    private fun crearUsuarioAdminPorDefecto() {
        viewModelScope.launch(Dispatchers.IO) {
            val admin = dao.obtenerPorEmail("admin@ggshop.com")
            if (admin == null) {
                dao.insert(
                    UsuarioEntity(
                        email = "admin@ggshop.com",
                        nombre = "Administrador Principal",
                        password = "admin123",
                        imageUri = null,
                        puntos = 0
                    )
                )
            }
        }
    }

    fun validarLogin() {
        val emailInput = _email.value.trim()
        val passInput = _password.value.trim()
        viewModelScope.launch(Dispatchers.IO) {
            val usuarioEncontrado = dao.login(emailInput, passInput)
            withContext(Dispatchers.Main) {
                if (usuarioEncontrado != null) {
                    _loginError.value = false
                    _esAdmin.value = usuarioEncontrado.email == "admin@ggshop.com"
                    _usuarioLogueadoNombre.value = usuarioEncontrado.nombre
                    _usuarioLogueadoEmail.value = usuarioEncontrado.email
                    _profileImageUri.value = if (usuarioEncontrado.imageUri != null) Uri.parse(usuarioEncontrado.imageUri) else null
                    _puntosUsuario.value = usuarioEncontrado.puntos
                    cargarFavoritosDelUsuario()
                    navigateTo(Screen.MainScreen)
                } else {
                    _loginError.value = true
                }
            }
        }
    }

    fun registrarUsuario(nombre: String, correo: String, pass: String, direccion: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (dao.obtenerPorEmail(correo) == null) {
                dao.insert(UsuarioEntity(email = correo, nombre = nombre, password = pass, imageUri = null, puntos = 0))
            }
        }
    }

    fun actualizarDatosUsuario(nuevoNombre: String, nuevoEmail: String, nuevaPass: String) {
        val emailActual = _usuarioLogueadoEmail.value
        _usuarioLogueadoNombre.value = nuevoNombre
        if (nuevoEmail != emailActual) _usuarioLogueadoEmail.value = nuevoEmail
        viewModelScope.launch(Dispatchers.IO) {
            val usuarioActual = dao.obtenerPorEmail(emailActual) ?: return@launch
            val passFinal = if (nuevaPass.isNotBlank()) nuevaPass else usuarioActual.password
            dao.actualizarUsuario(emailActual, nuevoNombre, passFinal)
        }
    }

    fun updateProfileImage(uri: Uri?) {
        if (uri != null) {
            val savedUri = copiarImagenAlmacenamientoInterno(uri)
            if (savedUri != null) {
                _profileImageUri.value = savedUri
                val emailActual = _usuarioLogueadoEmail.value
                viewModelScope.launch(Dispatchers.IO) { dao.actualizarFoto(emailActual, savedUri.toString()) }
            }
        }
    }

    private fun copiarImagenAlmacenamientoInterno(uri: Uri): Uri? {
        return try {
            val context = getApplication<Application>().applicationContext
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            Uri.fromFile(file)
        } catch (e: Exception) { e.printStackTrace(); null }
    }

    fun cargarFavoritosDelUsuario() {
        val email = _usuarioLogueadoEmail.value
        viewModelScope.launch(Dispatchers.IO) {
            favoritosDao.obtenerFavoritosPorUsuario(email).collect { listaEntidades ->
                val listaProductos = listaEntidades.mapNotNull { entidad ->
                    _productos.value.find { it.id == entidad.productoId }
                }
                _favoritos.value = listaProductos
            }
        }
    }

    fun toggleFavorito(producto: Producto) {
        val email = _usuarioLogueadoEmail.value
        viewModelScope.launch(Dispatchers.IO) {
            val esFav = favoritosDao.esFavorito(email, producto.id ?: 0)
            if (esFav) favoritosDao.eliminarFavorito(email, producto.id ?: 0)
            else favoritosDao.agregarFavorito(FavoritoEntity(usuarioEmail = email, productoId = producto.id ?: 0))
        }
    }

    fun canjearPuntosPorJuego(juego: String, horas: Int) {
        val email = _usuarioLogueadoEmail.value
        if (email == "invitado@ggshop.com") return
        val puntosGanados = horas * 25
        viewModelScope.launch(Dispatchers.IO) {
            dao.sumarPuntos(email, puntosGanados)
            val usuarioActualizado = dao.obtenerPorEmail(email)
            if (usuarioActualizado != null) {
                _puntosUsuario.value = usuarioActualizado.puntos
            }
        }
    }

    // Mantengo esta función por compatibilidad con tu código actual
    fun cargarProductos() {
        cargarProductosLocales()
    }

    fun agregarProducto(nombre: String, precio: Int, stock: Int, categoria: String, imageUri: String?) {
        val nuevoId = (_productos.value.mapNotNull { it.id }.maxOrNull() ?: 0L) + 1L

        // Convertimos el R.drawable.logo a String para que coincida con el modelo
        val nuevoProducto = Producto(
            id = nuevoId,
            nombre = nombre,
            descripcion = "Nuevo ingreso",
            precio = precio,
            stock = stock,
            imagenUrl = R.drawable.logo.toString(), // <--- CORRECCIÓN AQUÍ
            categoria = categoria,
            imageUri = imageUri
        )
        _productos.value = _productos.value + nuevoProducto
    }
    fun editarProducto(productoEditado: Producto) {
        _productos.value = _productos.value.map { if (it.id == productoEditado.id) productoEditado else it }
    }
    fun eliminarProducto(id: Long) { _productos.value = _productos.value.filterNot { it.id == id } }
    fun obtenerNombreUsuario(): String = _usuarioLogueadoNombre.value

    fun cerrarSesion() {
        _email.value = ""
        _password.value = ""
        _usuarioLogueadoNombre.value = "Invitado"
        _usuarioLogueadoEmail.value = ""
        _profileImageUri.value = null
        _puntosUsuario.value = 0
        cargarFavoritosDelUsuario()
        navigateTo(Screen.Login)
    }

    private val _productoSeleccionado = MutableStateFlow<Producto?>(null)
    val productoSeleccionado: StateFlow<Producto?> = _productoSeleccionado.asStateFlow()
    fun seleccionarProducto(producto: Producto) { _productoSeleccionado.value = producto }

    private val _carrito = MutableStateFlow<List<CartItem>>(emptyList())
    val carrito: StateFlow<List<CartItem>> = _carrito.asStateFlow()

    fun agregarAlCarrito(producto: Producto, cantidad: Int) {
        val actual = _carrito.value.toMutableList()
        val index = actual.indexOfFirst { it.producto.id == producto.id }
        if (index >= 0) actual[index] = actual[index].copy(cantidad = actual[index].cantidad + cantidad)
        else actual.add(CartItem(producto, cantidad))
        actualizarYGuardarCarrito(actual)
    }

    fun actualizarCantidad(productoId: Long, nuevaCantidad: Int) {
        val actual = _carrito.value.toMutableList()
        val index = actual.indexOfFirst { it.producto.id == productoId }
        if (index >= 0) {
            if (nuevaCantidad <= 0) actual.removeAt(index)
            else actual[index] = actual[index].copy(cantidad = nuevaCantidad)
            actualizarYGuardarCarrito(actual)
        }
    }

    fun eliminarDelCarrito(productoId: Long) {
        val actual = _carrito.value.filterNot { it.producto.id == productoId }
        actualizarYGuardarCarrito(actual)
    }

    fun finalizarCompra() {
        val itemsActuales = _carrito.value
        if (itemsActuales.isNotEmpty()) {
            val total = itemsActuales.sumOf { it.producto.precio * it.cantidad }
            val usuarioActual = _usuarioLogueadoEmail.value
            val resumen = itemsActuales.joinToString(", ") { "${it.producto.nombre} (x${it.cantidad})" }
            viewModelScope.launch(Dispatchers.IO) {
                ventasDao.registrarVenta(
                    VentaEntity(
                        usuarioEmail = usuarioActual,
                        itemsResumen = resumen,
                        total = total,
                        fecha = System.currentTimeMillis()
                    )
                )
                withContext(Dispatchers.Main) {
                    actualizarYGuardarCarrito(emptyList())
                }
            }
        }
    }

    private fun actualizarYGuardarCarrito(nuevaLista: List<CartItem>) {
        _carrito.value = nuevaLista
        val data = nuevaLista.joinToString(separator = ";") { "${it.producto.id},${it.cantidad}" }
        prefs.edit().putString("CARRITO_DATA", data).apply()
    }

    private fun cargarCarritoPersistido() {
        val data = prefs.getString("CARRITO_DATA", "") ?: ""
        if (data.isBlank()) return
        val items = data.split(";").mapNotNull { par ->
            val partes = par.split(",")
            if (partes.size == 2) {
                val id = partes[0].toLong()
                val cant = partes[1].toInt()
                val prod = _productos.value.find { it.id == id }
                if (prod != null) CartItem(prod, cant) else null
            } else null
        }
        _carrito.value = items
    }

    private fun cargarFavoritosPersistidos() { cargarFavoritosDelUsuario() }
}