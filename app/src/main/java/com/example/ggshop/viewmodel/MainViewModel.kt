package com.example.ggshop.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ggshop.R
import com.example.ggshop.data.Producto
// [CAMBIO ROOM] Importaciones necesarias
import com.example.ggshop.data.room.AppDatabase
import com.example.ggshop.data.room.UsuarioEntity
import com.example.ggshop.navigation.NavigationEvent
import com.example.ggshop.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class CartItem(val producto: Producto, val cantidad: Int)
data class Cliente(val nombre: String, val email: String)
data class Venta(val clienteEmail: String, val items: List<CartItem>, val total: Int)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // 1. Inicializamos la conexión a la Base de Datos
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.usuariosDao()

    // SharedPreferences solo para el Carrito (para lógica actual)
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

    // --- FOTO DE PERFIL ---
    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri: StateFlow<Uri?> = _profileImageUri.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    val isLoginValid: StateFlow<Boolean> = combine(_email, _password) { email, pass ->
        email.contains("@") && pass.length >= 6
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun onEmailChange(v: String) { _email.value = v }
    fun onPasswordChange(v: String) { _password.value = v }

    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin.asStateFlow()

    // --- LISTAS ADMIN ---
    private val _listaClientes = MutableStateFlow<List<Cliente>>(emptyList())
    val listaClientes: StateFlow<List<Cliente>> = _listaClientes.asStateFlow()

    private val _historialVentas = MutableStateFlow<List<Venta>>(emptyList())
    val historialVentas: StateFlow<List<Venta>> = _historialVentas.asStateFlow()

    // --- GESTIÓN DE PRODUCTOS (CRUD) ---
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()


    // [ROOM] REGISTRO DE USUARIOS
    fun registrarUsuario(nombre: String, correo: String, pass: String, direccion: String) {
        viewModelScope.launch(Dispatchers.IO) { // Ejecutamos en segundo plano
            // 1. Verificamos si el correo ya existe en la BD
            val existe = dao.obtenerPorEmail(correo)

            if (existe == null) {
                // 2. Creamos la entidad
                val nuevoUsuario = UsuarioEntity(
                    email = correo,
                    nombre = nombre,
                    password = pass,
                    imageUri = null
                )
                // 3. Insertamos en Room
                dao.insert(nuevoUsuario)
                // NOTA: Ya no necesitamos actualizar _listaClientes manualmente aquí
                // porque el Flow del bloque init lo hará automáticamente.
            }
        }
    }


    // [ROOM] LOGIN DE USUARIOS
    fun validarCredencialesPersistidas(): Boolean {
        val emailInput = _email.value
        val passInput = _password.value

        // 1. Caso Admin
        if (emailInput == "admin@ggshop.com" && passInput == "admin123") {
            _esAdmin.value = true
            _usuarioLogueadoNombre.value = "Administrador Principal"
            _usuarioLogueadoEmail.value = "admin@ggshop.com"
            _profileImageUri.value = null
            return true
        }

        // 2. Caso Usuario Normal (Consultar a ROOM)
        viewModelScope.launch(Dispatchers.IO) {
            val usuarioEncontrado = dao.login(emailInput, passInput)

            withContext(Dispatchers.Main) {
                if (usuarioEncontrado != null) {
                    // ¡Login Exitoso! Cargamos los datos de la BD a la memoria
                    _esAdmin.value = false
                    _usuarioLogueadoNombre.value = usuarioEncontrado.nombre
                    _usuarioLogueadoEmail.value = usuarioEncontrado.email

                    // Cargamos la foto si tiene
                    if (usuarioEncontrado.imageUri != null) {
                        _profileImageUri.value = Uri.parse(usuarioEncontrado.imageUri)
                    } else {
                        _profileImageUri.value = null
                    }

                    // Navegamos al Home
                    navigateTo(Screen.MainScreen)
                } else {
                    // Aquí podrías manejar el error de login
                }
            }
        }
        return (emailInput == "admin@ggshop.com" && passInput == "admin123")
    }

    // [ROOM] ACTUALIZAR DATOS
    fun actualizarDatosUsuario(nuevoNombre: String, nuevoEmail: String, nuevaPass: String) {
        val emailActual = _usuarioLogueadoEmail.value

        // Actualizamos la pantalla
        _usuarioLogueadoNombre.value = nuevoNombre

        viewModelScope.launch(Dispatchers.IO) {
            val usuarioActual = dao.obtenerPorEmail(emailActual) ?: return@launch
            val passFinal = if (nuevaPass.isNotBlank()) nuevaPass else usuarioActual.password
            dao.actualizarUsuario(emailActual, nuevoNombre, passFinal)
        }
    }

    // [ROOM] FOTO DE PERFIL
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
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateProfileImage(uri: Uri?) {
        if (uri != null) {
            val savedUri = copiarImagenAlmacenamientoInterno(uri)
            if (savedUri != null) {
                _profileImageUri.value = savedUri
                val emailActual = _usuarioLogueadoEmail.value
                viewModelScope.launch(Dispatchers.IO) {
                    dao.actualizarFoto(emailActual, savedUri.toString())
                }
            }
        }
    }


    // CARGA DE DATOS Y BLOQUE INIT

    init {
        cargarProductos()
        cargarCarritoPersistido()

        // [FLOW] Conexión en tiempo real con la BD
        // recupera usuarios antiguos y nuevos automáticamente
        viewModelScope.launch(Dispatchers.IO) {
            // "dao.obtenerTodosEnTiempoReal() devuelve un Flow
            // .collect se queda esperando cambios en la base de datos
            dao.obtenerTodosEnTiempoReal().collect { listaEntidades ->
                val listaMapeada = listaEntidades.map { entity ->
                    Cliente(entity.nombre, entity.email)
                }
                // Actualizamos la lista del admin
                _listaClientes.value = listaMapeada
            }
        }
    }



    fun cargarProductos() {
        if (_productos.value.isEmpty()) {
            _productos.value = listOf(
                Producto(1L, "Logitech G502 Hero", "Sensor HERO 25K", 49990, 15, R.drawable.mousee, "GAMING"),
                Producto(2L, "Teclado Redragon", "Mecánico TKL", 32990, 8, R.drawable.teclado, "GAMING"),
                Producto(5L, "Audífonos HyperX", "Surround 7.1", 85990, 10, R.drawable.audi, "GAMING"),
                Producto(6L, "Monitor ASUS TUF", "165Hz, 1ms", 249990, 4, R.drawable.moni, "GAMING"),
                Producto(7L, "Mousepad SteelSeries", "Tela micro-tejida", 15990, 20, R.drawable.mousepad, "GAMING"),
                Producto(8L, "Silla Gamer Corsair", "Ergonómica", 299990, 2, R.drawable.silla, "GAMING"),
                Producto(3L, "iPhone 15 128GB", "Chip A16", 799990, 5, R.drawable.iphone, "CELULARES"),
                Producto(4L, "Galaxy S24 Ultra", "QHD+ y S-Pen", 1199990, 3, R.drawable.samsung, "CELULARES"),
                Producto(10L, "Pixel 8 Pro", "IA Google", 899990, 4, R.drawable.google, "CELULARES"),
                Producto(11L, "Moto Edge 40", "Pantalla curva", 279990, 7, R.drawable.motorola, "CELULARES"),
                Producto(12L, "Nothing Phone (2)", "Glyph Interface", 549990, 6, R.drawable.nothing, "CELULARES"),
                Producto(13L, "Xiaomi 13T Pro", "Lente Leica", 599990, 9, R.drawable.xiaomi, "CELULARES")
            )
        }
    }

    fun agregarProducto(nombre: String, precio: Int, stock: Int, categoria: String, imageUri: String?) {
        val nuevoId = (_productos.value.mapNotNull { it.id }.maxOrNull() ?: 0L) + 1L
        val nuevoProducto = Producto(nuevoId, nombre, "Nuevo ingreso", precio, stock, R.drawable.logo, categoria, imageUri)
        _productos.value = _productos.value + nuevoProducto
    }

    fun editarProducto(productoEditado: Producto) {
        _productos.value = _productos.value.map {
            if (it.id == productoEditado.id) productoEditado else it
        }
    }

    fun eliminarProducto(id: Long) {
        _productos.value = _productos.value.filterNot { it.id == id }
    }

    fun obtenerNombreUsuario(): String {
        return _usuarioLogueadoNombre.value
    }

    fun cerrarSesion() {
        _email.value = ""
        _password.value = ""
        _usuarioLogueadoNombre.value = "Invitado"
        _usuarioLogueadoEmail.value = ""
        _profileImageUri.value = null
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

    fun finalizarCompra() {
        val itemsActuales = _carrito.value
        if (itemsActuales.isNotEmpty()) {
            val total = itemsActuales.sumOf { it.producto.precio * it.cantidad }
            val usuarioActual = _usuarioLogueadoEmail.value
            val nuevaVenta = Venta(usuarioActual, itemsActuales, total)
            _historialVentas.value = _historialVentas.value + nuevaVenta
            actualizarYGuardarCarrito(emptyList())
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
}