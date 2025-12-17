package com.example.workwell.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ScreenWithFooter(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        // ---------------------------------------------------------
        // 1. EL CONTENIDO DE LA VISTA (Capa del fondo)
        // ---------------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxSize()
                // IMPORTANTE: Añadimos padding abajo para que el último elemento
                // de tu lista no quede tapado por el footer flotante.
                // Ajusta este valor si tu footer cambia de tamaño.
                .padding(bottom = 80.dp)
        ) {
            content()
        }

        // ---------------------------------------------------------
        // 2. EL FOOTER CON SOMBRA (Capa superior flotante)
        // ---------------------------------------------------------
        // Esta es la lógica exacta que tenías en tu HomeView
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            // Sombra hacia arriba (Gradiente)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-30).dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.1f)
                            )
                        )
                    )
            )

            // El Footer real (Tu componente existente)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White // O Color.Transparent si quieres ver el fondo
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Footer() // Llamada a tu función Footer existente
                }
            }
        }
    }
}