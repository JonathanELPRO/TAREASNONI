package com.calyrsoft.ucbp1.features.webview.presentation

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AtuladoScreen(url: String,
                  postData: String?,
                  shouldStopBrowsing: (String?) -> Boolean,
                  modifier: Modifier){

    val webView = remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var navigateBack by remember { mutableStateOf(false) }

    // Estado para manejar el timeout
    var isLoading by remember { mutableStateOf(false) }
    var isTimeout by remember { mutableStateOf(false) }

    val timeoutMillis = 1L // 1 segundos

    // Manejo del botón "atrás"
    BackHandler(enabled = true) {
        val currentWebView = webView.value
        if (currentWebView != null && currentWebView.canGoBack()) {
            currentWebView.goBack()
        }
    }

    LaunchedEffect(navigateBack) {
        if (navigateBack) {
            val currentWebView = webView.value
            if (currentWebView != null && currentWebView.canGoBack()) {
                currentWebView.goBack()
            }
        }
        navigateBack = false
    }

    // Efecto que detecta el inicio de carga y lanza el timeout
    LaunchedEffect(isLoading) {
        if (isLoading) {
            kotlinx.coroutines.delay(timeoutMillis)
            if (isLoading) {
                // Si aún está cargando después del timeout → error
                isTimeout = true
                webView.value?.stopLoading()
            }
        }
    }


    if (isTimeout) {
        // Pantalla de error por timeout
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Tiempo de carga agotado. Intenta nuevamente.")
        }
    } else {
        // Pantalla principal con WebView
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Onboarding") },
                    navigationIcon = {
                        if (canGoBack) {
                            IconButton(onClick = { navigateBack = true }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Volver"
                                )
                            }
                        }
                    }
                )
            },
            content = { paddingValues ->
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // 🔹 WebView
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            WebView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, //ocupa todo el ancho.


                                    ViewGroup.LayoutParams.WRAP_CONTENT//la altura se adapta al contenido.
                                )

                                settings.apply {
                                    loadWithOverviewMode = true//Intenta escalar el contenido inicial para que quepa en la pantalla completa sin tener que scrollear lateralmente.
                                    isFocusable = true
                                    isFocusableInTouchMode = true
                                    //los dos de arriba
                                    //Le permite al WebView recibir foco y abrir el teclado si hay <input> en la página.
                                    //
                                    //Sin esto a veces no puedes escribir en formularios.
                                    useWideViewPort = true
                                    //Le dice al WebView que trate la página como si fuera un navegador de escritorio con viewport amplio, y luego la escale.
                                    javaScriptEnabled = true
                                    //Habilita JavaScript.
                                    //
                                    //Importantísimo, porque prácticamente cualquier página moderna usa JS.
                                    cacheMode = WebSettings.LOAD_NO_CACHE
                                    //Desactiva la caché.
                                    //
                                    //Siempre va a pedir los recursos al servidor, no va a usar lo que guardó antes.
                                }

                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                                        super.onPageStarted(view, url, favicon)
                                        view.settings.setSupportZoom(false)
                                        //Desactiva el zoom tipo “pellizcar”.
                                        isLoading = true
                                        isTimeout = false
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        canGoBack = view?.canGoBack() == true
                                        isLoading = false
                                        println("onPageFinished: $url")
                                    }

                                    override fun onReceivedError(
                                        view: WebView?,
                                        request: WebResourceRequest?,
                                        error: WebResourceError?
                                    ) {
                                        super.onReceivedError(view, request, error)
                                        isLoading = false
                                        println("onReceivedError: ${error?.description}")
                                    }

                                    override fun shouldOverrideUrlLoading(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): Boolean {
                                        return if (shouldStopBrowsing(request?.url.toString())) true
                                        else super.shouldOverrideUrlLoading(view, request)

                                        //nota: lo que hace shouldStop es:
                                        //shouldStopBrowsing = { true }
                                        //su definicion es
                                        //shouldStopBrowsing: (String?) -> Boolean,
                                        //super.shouldOverrideUrlLoading(view, request) por defecto devuelve false
                                        //basicamente esta funcion dice
                                        //si es que shouldStopBrowsing(request?.url.toString()) devuelve true
                                        //yo retornare true
                                        //caso contrario retornare super.shouldOverrideUrlLoading(view, request) que retorna
                                        //false

                                        //si este metodo finalmente retornar
                                        //true → “detén la carga, yo haré algo (por ejemplo, abrir Chrome o cerrar la pantalla)”
                                        //
                                        //false → “sigue la carga dentro del mismo WebView”
                                    }

                                    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                                        super.doUpdateVisitedHistory(view, url, isReload)
                                        canGoBack = view?.canGoBack() == true
                                        //doUpdateVisitedHistory actualiza el historia de navegaciones
                                        //cada que lo actualices significa que nos movimos a otra pagina por eso canGoBack esta en true
                                    }

                                }

                                if (postData != null) {
                                    postUrl(url, postData.toByteArray(StandardCharsets.UTF_8))
                                    //postUrl(...) → es un método nativo de la clase WebView.
                                    //url → es el destino (por ejemplo, "https://www.bisa.com/atulado/login").
                                    //
                                    //postData.toByteArray(StandardCharsets.UTF_8) →
                                // convierte el texto (String) de tus datos en un arreglo
                                // de bytes usando codificación UTF-8 (lo que espera el servidor).
                                } else {
                                    loadUrl(url)
                                    //Hace una solicitud HTTP GET normal.
                                }

                                webView.value = this
                            }
                        }
                    )

                    // 🔹 Indicador de carga
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        )
    }
}