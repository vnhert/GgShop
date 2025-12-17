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
        viewModelScope.launch {
            try {
                val lista = productoRepository.getProductos()
                // Log para verificar en tu Logcat de Android Studio
                println("TECH_STORE_DEBUG: Productos cargados = ${lista.size}")
                _productos.value = lista
            } catch (e: Exception) {
                println("TECH_STORE_ERROR: ${e.message}")
                e.printStackTrace()
                _productos.value = emptyList()
            }
        }
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

    fun eliminarDelCarrito(productoId: Int) {
        _carrito.value = _carrito.value.filterNot {
            (it.producto.id ?: 0L).toInt() == productoId
        }
    }
}