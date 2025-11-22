package com.calyrsoft.ucbp1.features.auth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calyrsoft.ucbp1.features.auth.domain.model.User
import com.calyrsoft.ucbp1.features.auth.domain.usecase.LoginUseCase
import com.calyrsoft.ucbp1.features.logs.data.datasource.LogsRemoteDataSource
import com.develoop.logs.ELogLevel
import com.develoop.logs.LogData
import com.develoop.logs.LogRequest
import com.google.firebase.messaging.FirebaseMessaging
import com.google.protobuf.ByteString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoginViewModel2(
    private val loginUseCase: LoginUseCase
) : ViewModel() {


    sealed class LoginUIState {
        object Init : LoginUIState()
        object Loading : LoginUIState()
        data class Success(val user: User) : LoginUIState()
        data class Error(val message: String) : LoginUIState()
    }

    private val _state = MutableStateFlow<LoginUIState>(LoginUIState.Init)
    val state: StateFlow<LoginUIState> = _state



    fun login(userOrEmail: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginUIState.Loading
            val result = loginUseCase(userOrEmail, password)
            result
                .onSuccess { user -> _state.value = LoginUIState.Success(user) }
                .onFailure { e -> _state.value = LoginUIState.Error(e.message ?: "Error al iniciar sesión") }
        }
    }

    fun resetState() {
        _state.value = LoginUIState.Init
    }

    suspend fun getToken(): String = suspendCoroutine { continuation ->
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FIREBASE", "getInstanceId failed", task.exception)
                continuation.resumeWithException(task.exception ?: Exception("Unknown error"))
                return@addOnCompleteListener
            }
            // Si la tarea fue exitosa, se obtiene el token
            val token = task.result
            Log.d("FIREBASE de login", "FCM Token: $token")


            // Reanudar la ejecución con el token
            continuation.resume(token ?: "")
        }
    }

    fun sendLog() {
        // Construimos el LogData y la petición
        val logData = LogData.newBuilder()
            .setAndroidId(ByteString.copyFromUtf8("abc123"))
            .setAppInstanceId(ByteString.copyFromUtf8("instance_001"))
            .setLogLevel(ELogLevel.LEVEL_ERROR)
            .setMessage("Something went wrong")
            .setStackTrace("Stacktrace here...")
            .setServerTimeStamp(System.currentTimeMillis())
            .setMobileTimeStamp(System.currentTimeMillis())
            .setVersionCode(123)
            .setUserId("user_42")
            .build()

        //se usa set para campos singulares como userId y el resto de sus amigos
        //es decir no puedo tener varios userId en el LOgData
        //sin embargo en Log request puedo tener varios logData
        //ya que Logdata es repeated por eso se usa addNombre



        val request = LogRequest.newBuilder()
            .addLogs(logData)
            .build()

        // Ejecutamos la llamada al servidor gRPC
        viewModelScope.launch {
            val result = LogsRemoteDataSource(
                host = "10.0.2.2",
                port = 9090
            ).send(request)
            //sabemos que esto retorna un
            //message LogResponse {
            //  ELogResultCode resultCode = 1;
            //}
            //
            //
            //enum ELogResultCode {
            //  LOG_RESULT_UNDEFINED = 0;
            //  LOG_RESULT_ERROR = 1;
            //  LOG_RESULT_OK = 2;
            //}
            //el servidor retorna LOG_RESULT_OK eso se define en su codigo del servidor
            //claro que si habra problema seguro devolvera otra cosa
            println("result: $result")
        }
    }



}
