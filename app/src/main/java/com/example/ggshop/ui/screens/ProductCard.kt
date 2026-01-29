package com.example.ggshop.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ggshop.R
import com.example.ggshop.data.Producto
import com.example.ggshop.viewmodel.MainViewModel
import com.example.ggshop.navigation.Screen

// Colores personalizados (Asegúrate de tenerlos en tu Color.kt o cámbialos por Color.Yellow/Black)
val TechYellow = Color(0xFFFFD700)
val TechBlack = Color(0xFF1A1A1A)

@Composable
fun ProductoCard(producto: Producto, viewModel: MainViewModel) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animación de rebote al pulsar (Feedback visual)
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleAnimation"
    )

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                viewModel.seleccionarProducto(producto)
                viewModel.navigateTo(Screen.ProductDetail)
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.White)
            ) {
                /* LOGICA DE IMAGEN ESTILO ROSA PASTEL:
                   Intentamos cargar el String 'imagenUrl' que viene del Backend.
                   Si es un link de internet, AsyncImage lo descarga.
                   Si es nulo o falla, muestra el logo de la tienda.
                */
                AsyncImage(
                    model = producto.imagenUrl,
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit,
                    // Mientras carga la imagen desde el backend
                    placeholder = painterResource(id = R.drawable.logo),
                    // Si el backend no tiene imagen o el link está roto
                    error = painterResource(id = R.drawable.logo)
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TechBlack
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${producto.precio}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))


                Surface(
                    color = TechYellow,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "VER DETALLE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = TechBlack
                    )
                }
            }
        }
    }
}