package com.example.ggshop.Tests
import org.junit.Assert.assertEquals
import com.example.ggshop.data.Producto
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Pruebas unitarias para validar la integridad del modelo Producto de GGShop.
 */
class ProductoGGShopTest {

    @Test
    fun `verificar que el producto se cree con todos los campos obligatorios y opcionales`() {
        // 1. ARRANGE: Preparación de datos reales para GGShop
        val id = 1L
        val nombre = "Monitor Curvo 27\""
        val descripcion = "Panel VA 144Hz 1ms para gaming competitivo"
        val precio = 250000
        val stock = 10
        val imagenUrlLocal = "res/drawable/monitor_default"
        val categoria = "Monitores"
        val imageUrlRemota = "https://firebasestorage.googleapis.com/v0/b/ggshop/o/products%2Fmonitor.jpg"

        // 2. ACT: Creación de la instancia
        val producto = Producto(
            id = id,
            nombre = nombre,
            descripcion = descripcion,
            precio = precio,
            stock = stock,
            imagenUrl = imagenUrlLocal,
            categoria = categoria,
            imageUrl = imageUrlRemota
        )

        // 3. ASSERT: Verificación de que el objeto retenga los datos correctamente
        assertEquals("El ID no coincide", id, producto.id)
        assertEquals("El nombre es incorrecto", nombre, producto.nombre)
        assertEquals("La descripción es incorrecta", descripcion, producto.descripcion)
        assertEquals("El precio no coincide", precio, producto.precio)
        assertEquals("El stock no coincide", stock, producto.stock)
        assertEquals("La imagen local no coincide", imagenUrlLocal, producto.imagenUrl)
        assertEquals("La categoría no coincide", categoria, producto.categoria)
        assertEquals("La URL remota no coincide", imageUrlRemota, producto.imageUrl)
    }

    @Test
    fun `verificar producto con imageUrl nulo por defecto`() {
        // Prueba para el caso en que el admin no ha subido una imagen personalizada
        val productoSinImagenAdmin = Producto(
            id = 2L,
            nombre = "Mouse Gamer RGB",
            descripcion = "Sensor óptico 16000 DPI",
            precio = 45000,
            stock = 25,
            imagenUrl = "res/drawable/mouse_placeholder",
            categoria = "Periféricos"
            // imageUrl queda nulo por el valor por defecto en la data class
        )

        assertNull("La URL remota debería ser nula si no se proporciona", productoSinImagenAdmin.imageUrl)
        assertNotNull("La imagen local siempre debe existir", productoSinImagenAdmin.imagenUrl)
    }

    @Test
    fun `verificar que el ID pueda ser nulo para productos nuevos`() {
        // En Android/Firebase, los objetos nuevos a veces no tienen ID hasta ser guardados
        val productoNuevo = Producto(
            id = null,
            nombre = "Memoria RAM 16GB",
            descripcion = "DDR4 3200MHz CL16",
            precio = 85000,
            stock = 15,
            imagenUrl = "res/drawable/ram_default",
            categoria = "Componentes"
        )

        assertNull("El ID debe permitir ser nulo para el proceso de inserción", productoNuevo.id)
    }
}