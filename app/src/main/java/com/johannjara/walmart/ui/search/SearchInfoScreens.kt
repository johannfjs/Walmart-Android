package com.johannjara.walmart.ui.search

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OfflineWifiIcon(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val errorColor = MaterialTheme.colorScheme.error

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f + 15f

        // Draw dot
        drawCircle(
            color = color,
            radius = 8f,
            center = androidx.compose.ui.geometry.Offset(centerX, centerY + 20f)
        )

        // Draw arc 1
        drawArc(
            color = color,
            startAngle = 220f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(centerX - 40f, centerY - 20f),
            size = androidx.compose.ui.geometry.Size(80f, 80f),
            style = Stroke(width = 8f, cap = StrokeCap.Round)
        )

        // Draw arc 2
        drawArc(
            color = color,
            startAngle = 220f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(centerX - 80f, centerY - 60f),
            size = androidx.compose.ui.geometry.Size(160f, 160f),
            style = Stroke(width = 8f, cap = StrokeCap.Round)
        )

        // Draw slash
        drawLine(
            color = errorColor,
            start = androidx.compose.ui.geometry.Offset(centerX - 90f, centerY - 80f),
            end = androidx.compose.ui.geometry.Offset(centerX + 90f, centerY + 60f),
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun EmptySearchIcon(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    val infiniteTransition = rememberInfiniteTransition(label = "magnifying_glass")
    val translationY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "translationY"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f + translationY

        // Draw dashed circle
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = width / 2.5f,
            center = androidx.compose.ui.geometry.Offset(centerX, centerY),
            style = Stroke(width = 4f)
        )

        // Draw magnifying glass
        val glassRadius = width / 6f
        val glassCenterX = centerX - 10f
        val glassCenterY = centerY - 10f

        drawCircle(
            color = color,
            radius = glassRadius,
            center = androidx.compose.ui.geometry.Offset(glassCenterX, glassCenterY),
            style = Stroke(width = 8f)
        )

        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(glassCenterX + glassRadius / 1.4f, glassCenterY + glassRadius / 1.4f),
            end = androidx.compose.ui.geometry.Offset(glassCenterX + glassRadius * 2f, glassCenterY + glassRadius * 2f),
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isNoInternet = message == "no cuentas con Internet"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isNoInternet) {
            OfflineWifiIcon(modifier = Modifier.size(150.dp))
        } else {
            // Draw general error or warning icon
            val color = MaterialTheme.colorScheme.error
            Canvas(modifier = Modifier.size(150.dp)) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(size.width / 2f, 10f)
                    lineTo(10f, size.height - 10f)
                    lineTo(size.width - 10f, size.height - 10f)
                    close()
                }
                drawPath(path, color = color.copy(alpha = 0.1f))
                drawPath(path, color = color, style = Stroke(width = 8f, cap = StrokeCap.Round))
                
                // Draw exclamation point
                drawLine(
                    color = color,
                    start = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height * 0.4f),
                    end = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height * 0.65f),
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = color,
                    radius = 6f,
                    center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height * 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isNoInternet) "No cuentas con Internet" else "Ha ocurrido un error",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isNoInternet) {
                "Por favor, revisa tu conexión de red e intenta nuevamente."
            } else {
                message
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.width(180.dp)
        ) {
            Text(text = "Reintentar", fontSize = 16.sp)
        }
    }
}

@Composable
fun EmptyScreen(
    keyword: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptySearchIcon(modifier = Modifier.size(150.dp))

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Sin resultados para \"$keyword\"",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No encontramos productos que coincidan con tu búsqueda. Intenta buscar términos más generales o verifica tu ortografía.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
