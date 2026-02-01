package com.example.ggshop.Tests

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class GGShopContextTest {
    @Test
    fun verificarPackageNameDeGGShop() {
        // Obtenemos el contexto de la aplicación instalada
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Verificamos que el ID de la app sea el correcto
        assertEquals("com.example.ggshop", appContext.packageName)
    }
}