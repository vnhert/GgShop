package com.example.ggshop.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ggshop.R
import com.example.ggshop.ui.theme.TechBlack
import com.example.ggshop.ui.theme.TechYellow
import com.example.ggshop.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarUsuario(viewModel: MainViewModel) {
    val context = LocalContext.current

    // Datos actuales del ViewModel
    val currentName by viewModel.usuarioLogueadoNombre.collectAsState()
    val currentEmail by viewModel.usuarioLogueadoEmail.collectAsState()
    val currentImage by viewModel.profileImageUri.collectAsState()

    // Variables temporales para editar
    var newName by remember { mutableStateOf(currentName) }
    var newEmail by remember { mutableStateOf(currentEmail) }
    var newPass by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Launcher para cambiar foto
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateProfileImage(uri)
    }

    // Sincronizar datos si cargan un poco después
    LaunchedEffect(currentName, currentEmail) {
        if (newName.isBlank()) newName = currentName
        if (newEmail.isBlank() || newEmail == "invitado@ggshop.com") newEmail = currentEmail
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("EDITAR PERFIL", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- FOTO DE PERFIL ---
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = currentImage ?: R.drawable.profile_pic,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TechYellow)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = TechBlack)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- CAMPO NOMBRE ---
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Nombre de Usuario") },
                // AQUÍ ESTABA EL ERROR, AHORA TIENE LOS PARÉNTESIS ()
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechBlack,
                    focusedLabelColor = TechBlack
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- CAMPO EMAIL ---
            OutlinedTextField(
                value = newEmail,
                onValueChange = { newEmail = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(), // Correcto
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechBlack,
                    focusedLabelColor = TechBlack
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- CAMPO CONTRASEÑA (NUEVO) ---
            OutlinedTextField(
                value = newPass,
                onValueChange = { newPass = it },
                label = { Text("Nueva Contraseña") },
                placeholder = { Text("Deja vacío para mantener la actual") },
                modifier = Modifier.fillMaxWidth(), // Correcto
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechBlack,
                    focusedLabelColor = TechBlack
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTÓN GUARDAR ---
            Button(
                onClick = {
                    if (newName.isNotBlank() && newEmail.isNotBlank()) {
                        viewModel.actualizarDatosUsuario(newName, newEmail, newPass)
                        Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        viewModel.navigateBack()
                    } else {
                        Toast.makeText(context, "Completa nombre y correo", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TechBlack),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("GUARDAR CAMBIOS", color = TechYellow, fontWeight = FontWeight.Bold)
            }
        }
    }
}