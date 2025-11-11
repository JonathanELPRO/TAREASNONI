package com.calyrsoft.ucbp1.features.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calyrsoft.ucbp1.R
import com.calyrsoft.ucbp1.features.auth.domain.model.Role
import com.calyrsoft.ucbp1.ui.icons.MyIconPack

import com.calyrsoft.ucbp1.ui.icons.myiconpack.Imagen



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen2(
    vm: LoginViewModel2,
    onLoginSuccessGoToLodgings: () -> Unit = {},
    onLoginSuccessGoToRegisterLodging: (Long?) -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        vm.getToken()
        vm.sendLog()

    }



    val state by vm.state.collectAsState()
    var userOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(containerColor = Color(0xFFF4F4F4)) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(bottomStart = 80.dp, bottomEnd = 80.dp))
                    .background(Color(0xFFB00020)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Imagen de cabecera",
                    modifier = Modifier.size(180.dp).clip(RoundedCornerShape(90.dp))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Login", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = userOrEmail,
                onValueChange = { userOrEmail = it },
                label = { Text("Usuario o correo") },
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val st = state) {
                is LoginViewModel2.LoginUIState.Loading -> CircularProgressIndicator(color = Color(0xFFB00020))
                is LoginViewModel2.LoginUIState.Error -> Text(st.message, color = MaterialTheme.colorScheme.error)
                is LoginViewModel2.LoginUIState.Success -> {
                    if (st.user.role == Role.CLIENT){
                        onLoginSuccessGoToLodgings()
                    }
                    if (st.user.role == Role.ADMIN){
                        onLoginSuccessGoToRegisterLodging(st.user.id)
                    }
                    vm.resetState()
                }
                else -> Unit
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { vm.login(userOrEmail, password) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB00020)),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(50.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Iniciar sesión", color = Color.White, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRegisterClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDB304)),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(50.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Crear cuenta", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }



        Icon(
            imageVector = MyIconPack.Imagen,
            contentDescription = "Foto adecuada",
            tint = Color.Unspecified
        )

       }
}
