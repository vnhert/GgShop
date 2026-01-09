package com.example.ggshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ggshop.data.Producto
import com.example.ggshop.navigation.NavigationEvent
import com.example.ggshop.navigation.Screen
import com.example.ggshop.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.ggshop.R
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

data class CartItem(
    val producto: Producto,
    val cantidad: Int
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // --- 1. NAVEGACIÓN ---
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

    // --- 2. VALIDACIÓN DE LOGIN (Requerimiento: No vacíos + Límite caracteres) ---
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // Lógica: Email debe tener @ y Password al menos 8 caracteres (Límite solicitado)
    val isLoginValid: StateFlow<Boolean> = combine(_email, _password) { email, pass ->
        email.contains("@") && email.isNotBlank() && pass.length >= 8
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun onEmailChange(nuevoEmail: String) { _email.value = nuevoEmail }
    fun onPasswordChange(nuevaPass: String) { _password.value = nuevaPass }


    // --- 3. PRODUCTOS ---
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    fun cargarProductos() {
        val listaManual = listOf(
            Producto(1L, "Logitech G502 Hero", "Sensor HERO 25K, 11 botones.", 49990, 15, "https://www.pcfactory.cl/public/foto/32824/1_500.jpg", "GAMING"),
            Producto(2L, "Teclado Redragon Kumara", "Mecánico TKL, switches Blue.", 32990, 8, "https://www.pcfactory.cl/public/foto/28362/1_500.jpg", "GAMING"),
            Producto(3L, "Apple iPhone 15 128GB", "Chip A16 Bionic, cámara 48 MP.", 799990, 5, "https://www.pcfactory.cl/public/foto/49504/1_500.jpg", "CELULARES"),
            Producto(4L, "Samsung Galaxy S24 Ultra", "Pantalla QHD+ y S-Pen.", 1199990, 3, "https://www.pcfactory.cl/public/foto/50257/1_500.jpg", "CELULARES")
        )
        _productos.value = listaManual
    }

    // --- 4. PRODUCTO SELECCIONADO ---
    private val _productoSeleccionado = MutableStateFlow<Producto?>(null)
    val productoSeleccionado: StateFlow<Producto?> = _productoSeleccionado.asStateFlow()

    fun seleccionarProducto(producto: Producto) {
        _productoSeleccionado.value = producto
    }

    // --- 5. LÓGICA DEL CARRITO (Corregida con Long) ---
    private val _carrito = MutableStateFlow<List<CartItem>>(emptyList())
    val carrito: StateFlow<List<CartItem>> = _carrito.asStateFlow()

    fun agregarAlCarrito(producto: Producto, cantidad: Int) {
        val actual = _carrito.value.toMutableList()
        val index = actual.indexOfFirst { it.producto.id == producto.id }

        if (index >= 0) {
            val itemViejo = actual[index]
            actual[index] = itemViejo.copy(cantidad = itemViejo.cantidad + cantidad)
        } else {
            actual.add(CartItem(producto = producto, cantidad = cantidad))
        }
        _carrito.value = actual
    }

    // Corregido: Ahora recibe Long para que coincida con Producto.id
    fun actualizarCantidad(productoId: Long, nuevaCantidad: Int) {
        val actual = _carrito.value.toMutableList()
        val index = actual.indexOfFirst { it.producto.id == productoId }

        if (index >= 0) {
            if (nuevaCantidad <= 0) {
                actual.removeAt(index)
            } else {
                actual[index] = actual[index].copy(cantidad = nuevaCantidad)
            }
            _carrito.value = actual
        }
    }

    // Corregido: Ahora recibe Long
    fun eliminarDelCarrito(productoId: Long) {
        _carrito.value = _carrito.value.filterNot { it.producto.id == productoId }
    }
}