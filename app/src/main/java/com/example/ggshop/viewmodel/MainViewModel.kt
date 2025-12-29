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

// 👈 Mismo estilo: Definido aquí mismo para el carrito
data class CartItem(
    val producto: Producto,
    val cantidad: Int
)

class MainViewModel(
    private val productoRepository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    // --- NAVEGACIÓN (SharedFlow para eventos únicos) ---
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    fun navigateTo(screen: Screen) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateTo(route = screen))
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }

    // --- LISTA DE PRODUCTOS TECH (Celulares, Laptops, etc.) ---
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    fun cargarProductos() {
        val listaManual = listOf(
            // CATEGORÍA: GAMING
            Producto(
                id = 1L,
                nombre = "Logitech G502 Hero High Performance",
                descripcion = "Sensor HERO 25K, 11 botones programables, pesas ajustables y RGB.",
                precio = 49990,
                stock = 15,
                imagenUrl = "",
                categoria = "GAMING"
            ),
            Producto(
                id = 2L,
                nombre = "Teclado Redragon Kumara K552 RGB",
                descripcion = "Teclado mecánico TKL, switches Blue, construcción en aluminio y ABS.",
                precio = 32990,
                stock = 8,
                imagenUrl = "https://www.pcfactory.cl/public/foto/28362/1_500.jpg",
                categoria = "GAMING"
            ),
            Producto(
                id = 5L,
                nombre = "Audífonos HyperX Cloud II Red",
                descripcion = "Sonido envolvente 7.1, almohadillas de memory foam y micrófono con cancelación de ruido.",
                precio = 74990,
                stock = 12,
                imagenUrl = "https://www.pcfactory.cl/public/foto/20257/1_500.jpg",
                categoria = "GAMING"
            ),

            // CATEGORÍA: CELULARES
            Producto(
                id = 3L,
                nombre = "Apple iPhone 15 128GB Black",
                descripcion = "Chip A16 Bionic, Dynamic Island, cámara principal de 48 MP y USB-C.",
                precio = 799990,
                stock = 5,
                imagenUrl = "https://www.pcfactory.cl/public/foto/49504/1_500.jpg",
                categoria = "CELULARES"
            ),
            Producto(
                id = 4L,
                nombre = "Samsung Galaxy S24 Ultra 256GB",
                descripcion = "Pantalla QHD+ de 6.8\", procesador Snapdragon 8 Gen 3 y S-Pen incluido.",
                precio = 1199990,
                stock = 3,
                imagenUrl = "https://www.pcfactory.cl/public/foto/50257/1_500.jpg",
                categoria = "CELULARES"
            )
        )

        // Actualizamos el flujo para que la UI se refresque automáticamente
        _productos.value = listaManual
    }

    // --- DETALLE DEL PRODUCTO SELECCIONADO ---
    private val _productoSeleccionado = MutableStateFlow<Producto?>(null)
    val productoSeleccionado: StateFlow<Producto?> = _productoSeleccionado

    fun seleccionarProducto(producto: Producto) {
        _productoSeleccionado.value = producto
    }

    // --- LÓGICA DEL CARRITO (Idéntica a Rosa Pastel) ---
    private val _carrito = MutableStateFlow<List<CartItem>>(emptyList())
    val carrito: StateFlow<List<CartItem>> = _carrito

    fun agregarAlCarrito(producto: Producto, cantidad: Int) {
        val actual = _carrito.value.toMutableList()
        val index = actual.indexOfFirst { it.producto.id == producto.id }

        if (index >= 0) {
            // Si el gadget ya está en el carro, suma la cantidad
            val itemViejo = actual[index]
            actual[index] = itemViejo.copy(cantidad = itemViejo.cantidad + cantidad)
        } else {
            // Si es nuevo (ej. un mouse nuevo), agrégalo
            actual.add(CartItem(producto = producto, cantidad = cantidad))
        }

        _carrito.value = actual
    }

    fun actualizarCantidad(productoId: Int, nuevaCantidad: Int) {
        val actual = _carrito.value.toMutableList()

        val index = actual.indexOfFirst {
            (it.producto.id ?: 0L).toInt() == productoId
        }

        if (index >= 0) {
            if (nuevaCantidad <= 0) {
                actual.removeAt(index) // Si llega a 0, se quita del carrito
            } else {
                actual[index] = actual[index].copy(cantidad = nuevaCantidad)
            }
            _carrito.value = actual
        }
    }
    fun navigateUp() {
        viewModelScope.launch {
            // Esto dispara el evento que la MainActivity está escuchando
            _navigationEvents.emit(NavigationEvent.NavigateUp)
        }
    }

    fun eliminarDelCarrito(productoId: Int) {
        _carrito.value = _carrito.value.filterNot {
            (it.producto.id ?: 0L).toInt() == productoId
        }
    }
}