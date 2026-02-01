package com.example.ggshop.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.viewmodel.MainViewModel

// OSMDroid imports para el mapa
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

data class TechStore(
    val name: String,
    val address: String,
    val status: String
)

val ggShopStores = listOf(
    TechStore("GGSHOP CENTRAL HUB", "Av. Providencia 1234, Santiago", "Abierto - Cierra 20:00"),
    TechStore("TECH POINT COSTANERA", "Av. Vitacura 555, Las Condes", "Abierto - Cierra 21:00")
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType")
@Composable
fun Sucursales(viewModel: MainViewModel) {
    var selectedStoreIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    // Configuración de OSMDroid (Mapa)
    remember {
        Configuration.getInstance().userAgentValue = context.packageName
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
    }

    val mapCenter = remember { GeoPoint(-33.4357, -70.6136) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("PUNTOS DE RETIRO", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                },
                navigationIcon = {
                    // CORRECCIÓN: Se cambió navigateUp() por navigateBack()
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = TechBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ENCABEZADO GGSHOP
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    color = TechBlack,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = null,
                        tint = TechYellow,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Column {
                    Text(
                        text = "GGSHOP HUBS",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Selecciona donde retirar tu pedido",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // LISTA DE TIENDAS
            ggShopStores.forEachIndexed { index, store ->
                StoreCard(
                    store = store,
                    isSelected = index == selectedStoreIndex,
                    onClick = { selectedStoreIndex = index }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "UBICACIÓN EN MAPA",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // CONTENEDOR DEL MAPA
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setCenter(mapCenter)
                            controller.setZoom(15.0)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN DE ACCIÓN CORREGIDO
            Button(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TechBlack),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "CONFIRMAR Y GUARDAR",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TechYellow,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreCard(
    store: TechStore,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (isSelected) 3.dp else 1.dp,
            color = if (isSelected) TechYellow else Color.LightGray
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFDFDFD) else Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = if (isSelected) TechBlack else Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = store.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}