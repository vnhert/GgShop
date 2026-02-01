package com.example.ggshop.Tests

import com.example.ggshop.data.api.ApiService
import com.example.ggshop.data.PedidoRequest
import com.example.ggshop.data.Producto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
/*Simula la conexión con la API para validar la carga de productos y el envío correcto de pedidos al servidor.*/

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun apiServiceDebeDevolverListaDeProductos() = runTest(testDispatcher) {
        // 1. Mock
        val mockApi = mockk<ApiService>()
        val listaFalsa = listOf(
            Producto(id = 101, nombre = "Silla", precio = 120000.0, stock = 5, descripcion = "", categoria = "", imagenUrl = ""),
            Producto(id = 102, nombre = "Audio", precio = 40000.0, stock = 15, descripcion = "", categoria = "", imagenUrl = "")
        )

        coEvery { mockApi.obtenerProductos() } returns listaFalsa


        val resultado = mockApi.obtenerProductos()


        assertEquals(2, resultado.size)
        assertEquals("Silla", resultado[0].nombre)
    }

    @Test
    fun enviarPedidoDebeCapturarDatosCorrectos() = runTest(testDispatcher) {
        // 1. Mock
        val mockApi = mockk<ApiService>(relaxed = true)
        val slotPedido = slot<PedidoRequest>()

        val pedido = PedidoRequest(
            clienteId = "gene@gmail.com",
            total = 250000.0,
            items = emptyList()
        )


        mockApi.crearPedido(pedido)


        coVerify { mockApi.crearPedido(capture(slotPedido)) }
        assertEquals("gene@gmail.com", slotPedido.captured.clienteId)
    }
}