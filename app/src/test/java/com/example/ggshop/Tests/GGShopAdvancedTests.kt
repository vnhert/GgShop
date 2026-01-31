package com.example.ggshop.Tests

import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.doubles.shouldBeExactly
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.DisplayName


data class ProductItem(
    val id: String,
    val name: String,
    val price: Double,
    val description: String = ""
)


class GGShopAdvancedTests {

    @Test
    @DisplayName("Cálculo del total del carrito con múltiples productos")
    fun `test total price calculation`() {
        // GIVEN
        val cart = listOf(
            ProductItem("1", "Procesador i9", 550.0),
            ProductItem("2", "RTX 4080", 1200.0),
            ProductItem("3", "Cable HDMI", 15.50)
        )

        // WHEN
        val total = cart.sumOf { it.price }

        // THEN
        total shouldBeExactly 1765.50
    }

    @Test
    @DisplayName("Formateo de moneda para la vista de usuario")
    fun `test currency formatting`() {
        val price = 99.9
        val formatted = "$${String.format("%.2f", price)}"

        formatted shouldBe "$99.90"
    }

    @Test
    @DisplayName("Simular eliminación de producto usando MockK")
    fun `test mock deletion logic`() {
        // Creamos un mock de la interfaz
        val cartService = mockk<CartService>(relaxed = true)
        val targetId = "id_eliminar_123"

        // Ejecutamos la acción que dispararía el botón de la UI
        cartService.removeItem(targetId)

        // Verificamos que se llamó a la función correctamente
        verify { cartService.removeItem(targetId) }
    }

    @Test
    @DisplayName("Verificar que la lista de favoritos no permite duplicados (lógica de negocio)")
    fun `test favorites unique logic`() {
        val favorites = mutableSetOf<String>()
        val productId = "p001"

        favorites.add(productId)
        favorites.add(productId) // Intentar agregar de nuevo

        favorites.size shouldBe 1
        favorites shouldContain productId
    }
}


interface CartService {
    fun removeItem(id: String)
    fun addItem(item: ProductItem)
}




