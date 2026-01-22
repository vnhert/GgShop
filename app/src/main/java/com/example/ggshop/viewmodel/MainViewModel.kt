package com.example.ggshop.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ggshop.R
import com.example.ggshop.data.Producto
import com.example.ggshop.navigation.NavigationEvent
import com.example.ggshop.navigation.Screen
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartItem(
    val producto: Producto,
    val cantidad: Int
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs: SharedPreferences =
        application.getSharedPreferences("GGShop_Prefs", Context.MODE_PRIVATE)

    // --- 1. NAVEGACIÓN COMPLETA ---
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    fun navigateTo(screen: Screen) {
        viewModelScope.launch { _navigationEvents.emit(NavigationEvent.NavigateTo(route = screen)) }
    }

    fun navigateBack() {
        viewModelScope.launch { _navigationEvents.emit(NavigationEvent.PopBackStack) }
    }

    fun navigateUp() {
        viewModelScope.launch { _navigationEvents.emit(NavigationEvent.NavigateUp) }
    }

    // --- 2. VALIDACIÓN DE LOGIN ---
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    val isLoginValid: StateFlow<Boolean> = combine(_email, _password) { email, pass ->
        email.contains("@") && email.isNotBlank() && pass.length >= 6
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    fun onEmailChange(nuevoEmail: String) { _email.value = nuevoEmail }
    fun onPasswordChange(nuevaPass: String) { _password.value = nuevaPass }

    // --- 3. PERSISTENCIA Y ROL DE USUARIO (CORREGIDO) ---

    // El estado de Admin debe estar a nivel de clase, no dentro de una función
    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin.asStateFlow()

    fun registrarUsuario(nombre: String, correo: String, pass: String, direccion: String) {
        prefs.edit().apply {
            putString("USER_NAME", nombre)
            putString("USER_EMAIL", correo)
            putString("USER_PASS", pass)
            putString("USER_DIR", direccion)
            apply()
        }
    }

    // Función de validación corregida para comparar con SharedPreferences
    fun validarCredencialesPersistidas(): Boolean {
        val emailIngresado = _email.value
        val passIngresada = _password.value

        // 1. Verificación para el Perfil Administrador (Hardcoded)
        if (emailIngresado == "admin@ggshop.com" && passIngresada == "admin123") {
            _esAdmin.value = true
            return true
        }

        // 2. Verificación para Usuario Normal (Desde SharedPreferences)
        val correoGuardado = prefs.getString("USER_EMAIL", "") ?: ""
        val passGuardada = prefs.getString("USER_PASS", "") ?: ""

        val loginExitoso = (emailIngresado == correoGuardado && passIngresada == passGuardada)

        if (loginExitoso) {
            _esAdmin.value = false // Si es usuario normal, se asegura que no sea admin
        }

        return loginExitoso
    }

    fun obtenerNombreUsuario(): String = prefs.getString("USER_NAME", "Gamer Pro") ?: "Gamer Pro"

    // --- 4. PRODUCTOS (CATÁLOGO COMPLETO) ---
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    fun cargarProductos() {
        _productos.value = listOf(
            Producto(1L, "Logitech G502 Hero", "Sensor HERO 25K, 11 botones.", 49990, 15, R.drawable.mousee, "GAMING"),
            Producto(2L, "Teclado Redragon Kumara", "Mecánico TKL, switches Blue.", 32990, 8, R.drawable.teclado, "GAMING"),
            Producto(5L, "Audífonos HyperX Cloud II", "Surround 7.1, Memory Foam.", 85990, 10, R.drawable.audi, "GAMING"),
            Producto(6L, "Monitor ASUS TUF 27\"", "165Hz, 1ms, Panel IPS.", 249990, 4, R.drawable.moni, "GAMING"),
            Producto(7L, "Mousepad SteelSeries QcK", "Tela micro-tejida.", 15990, 20, R.drawable.mousepad, "GAMING"),
            Producto(8L, "Silla Gamer Corsair T3", "Ergonómica, cuero.", 299990, 2, R.drawable.silla, "GAMING"),
            Producto(3L, "Apple iPhone 15 128GB", "Chip A16 Bionic, 48 MP.", 799990, 5, R.drawable.iphone, "CELULARES"),
            Producto(4L, "Samsung Galaxy S24 Ultra", "Pantalla QHD+ y S-Pen.", 1199990, 3, R.drawable.samsung, "CELULARES"),
            Producto(10L, "Google Pixel 8 Pro", "IA de Google, cámara nocturna.", 899990, 4, R.drawable.google, "CELULARES"),
            Producto(11L, "Motorola Edge 40 Neo", "Pantalla curva 144Hz.", 279990, 7, R.drawable.motorola, "CELULARES"),
            Producto(12L, "Nothing Phone (2)", "Interfaz Glyph transparente.", 549990, 6, R.drawable.nothing, "CELULARES"),
            Producto(13L, "Xiaomi 13T Pro", "Lente Leica, carga 120W.", 599990, 9, R.drawable.xiaomi, "CELULARES")
        )
    }

    // --- 5. PRODUCTO SELECCIONADO ---
    private val _productoSeleccionado = MutableStateFlow<Producto?>(null)
    val productoSeleccionado: StateFlow<Producto?> = _productoSeleccionado.asStateFlow()

    fun seleccionarProducto(producto: Producto) { _productoSeleccionado.value = producto }

    // --- 6. CARRITO CON PERSISTENCIA ---
    private val _carrito = MutableStateFlow<List<CartItem>>(emptyList())
    val carrito: StateFlow<List<CartItem>> = _carrito.asStateFlow()

    init {
        cargarProductos()
        cargarCarritoPersistido()
    }

    fun agregarAlCarrito(producto: Producto, cantidad: Int) {
        val actual = _carrito.value.toMutableList()
        val index = actual.indexOfFirst { it.producto.id == producto.id }
        if (index >= 0) {
            actual[index] = actual[index].copy(cantidad = actual[index].cantidad + cantidad)
        } else {
            actual.add(CartItem(producto, cantidad))
        }
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

    // --- 7. IMAGEN DE PERFIL ---
    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri: StateFlow<Uri?> = _profileImageUri.asStateFlow()

    fun updateProfileImage(uri: Uri?) {
        _profileImageUri.value = uri
    }
}