package com.example.ggshop.Tests

import com.example.ggshop.data.Producto
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale
/*Verifica la precisión en los cálculos del carrito, formato de precios y la integridad de los datos de los productos.
*/
class LogicaTest {

    @Test
    fun calculoDelTotalDelCarritoDebeSerExacto() {

        val carrito = listOf(
            Producto(id = 1, nombre = "Mouse", precio = 25000.0, stock = 10, descripcion = "", categoria = "", imagenUrl = ""),
            Producto(id = 2, nombre = "Teclado", precio = 50000.0, stock = 5, descripcion = "", categoria = "", imagenUrl = "")
        )


        val total = carrito.sumOf { it.precio }


        assertEquals(75000.0, total, 0.0)
    }

    @Test
    fun formatoDeMonedaDebeUsarPunto() {
        val precio = 15990.0
        val formateado = String.format(Locale.US, "$%.2f", precio)

        assertEquals("$15990.00", formateado)
    }

    @Test
    fun productoNuevoDebeTenerIdCero() {
        val nuevoProducto = Producto(
            nombre = "RTX 4090", descripcion = "GPU", precio = 1500000.0,
            stock = 1, imagenUrl = "", categoria = "Hardware"
        )

        assertEquals(0L, nuevoProducto.id)
    }
}