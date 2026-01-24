package com.example.ggshop.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ggshop.R
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.viewmodel.MainViewModel

// Modelo de datos para las opciones de juego
data class GameOption(val nombre: String, val imageRes: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamerZone(viewModel: MainViewModel) {
    val puntos by viewModel.puntosUsuario.collectAsState()
    val context = LocalContext.current

    val juegos = listOf(
        GameOption("Valorant", R.drawable.valorant),
        GameOption("League of Legends", R.drawable.lol),
        GameOption("CS:GO 2", R.drawable.csgo),
        GameOption("Fortnite", R.drawable.fortnite),
        GameOption("Call of Duty", R.drawable.cod),
        GameOption("Roblox", R.drawable.roblox)
    )

    var showDialog by remember { mutableStateOf(false) }
    var juegoSeleccionado by remember { mutableStateOf<GameOption?>(null) }
    var horasJugadas by remember { mutableFloatStateOf(1f) }

    // CÁLCULO DE DESCUENTO
    // Meta: 100,000 puntos = 90% Descuento
    val metaPuntos = 100000f
    val maxDescuento = 90f

    // Regla de tres simple, topeada a 90%
    val descuentoActual = (puntos.toFloat() / metaPuntos) * maxDescuento
    val descuentoMostrado = descuentoActual.coerceIn(0f, 90f)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("GG REWARDS", fontWeight = FontWeight.Black, color = TechYellow) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = TechYellow)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = TechBlack)
            )
        },
        containerColor = TechBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TARJETA DE PUNTOS
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Star, null, tint = TechYellow, modifier = Modifier.size(48.dp))
                    Text("MIS GG POINTS", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("$puntos", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)

                    // TEXTO DE DESCUENTO ACTUALIZADO
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nivel Actual: %.1f%% OFF".format(descuentoMostrado),
                        color = TechYellow,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "(Meta: 100.000 Puntos para 90% OFF)",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
            }

            Text("REGISTRAR SESIÓN DE JUEGO", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Selecciona el juego para reclamar tus puntos.", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(juegos) { juego ->
                    Card(
                        modifier = Modifier
                            .height(140.dp)
                            .clickable {
                                juegoSeleccionado = juego
                                showDialog = true
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = juego.imageRes),
                                    contentDescription = juego.nombre,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(TechBlack)
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = juego.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TechYellow
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog && juegoSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = TechYellow)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Actividad", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Juego: ${juegoSeleccionado!!.nombre}", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tiempo jugado: ${horasJugadas.toInt()} horas")

                    Slider(
                        value = horasJugadas,
                        onValueChange = { horasJugadas = it },
                        valueRange = 1f..12f,
                        steps = 10,
                        colors = SliderDefaults.colors(
                            thumbColor = TechYellow,
                            activeTrackColor = TechYellow,
                            inactiveTrackColor = Color.LightGray
                        )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFFDE7), shape = RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // ACTUALIZAMOS EL TEXTO PARA MOSTRAR QUE SON MENOS PUNTOS AHORA
                        Text(
                            text = "+${horasJugadas.toInt() * 25} Puntos", // Ahora es * 25
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFF57F17),
                            fontSize = 20.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.canjearPuntosPorJuego(juegoSeleccionado!!.nombre, horasJugadas.toInt())
                        Toast.makeText(context, "¡Puntos Agregados!", Toast.LENGTH_SHORT).show()
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TechBlack, contentColor = TechYellow),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("RECLAMAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}