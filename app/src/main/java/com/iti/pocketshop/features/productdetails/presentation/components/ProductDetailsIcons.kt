package com.iti.pocketshop.features.productdetails.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun ArrowBackIcon() {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(Modifier.size(24.dp)) {
        val startX = if (isRtl) size.width * 0.72f else size.width * 0.28f
        val endX = if (isRtl) size.width * 0.28f else size.width * 0.72f
        drawLine(color, Offset(startX, size.height / 2), Offset(endX, size.height / 2), 1.6.dp.toPx())
        drawLine(color, Offset(startX, size.height / 2), Offset(size.width / 2, size.height * 0.25f), 1.6.dp.toPx(), StrokeCap.Round)
        drawLine(color, Offset(startX, size.height / 2), Offset(size.width / 2, size.height * 0.75f), 1.6.dp.toPx(), StrokeCap.Round)
    }
}

@Composable
internal fun HeartIcon(filled: Boolean, color: Color) {
    Canvas(Modifier.size(17.dp)) {
        val path = Path().apply {
            moveTo(size.width / 2, size.height * 0.88f)
            cubicTo(size.width * 0.12f, size.height * 0.63f, size.width * 0.03f, size.height * 0.38f, size.width * 0.18f, size.height * 0.20f)
            cubicTo(size.width * 0.31f, size.height * 0.05f, size.width * 0.46f, size.height * 0.12f, size.width / 2, size.height * 0.26f)
            cubicTo(size.width * 0.54f, size.height * 0.12f, size.width * 0.69f, size.height * 0.05f, size.width * 0.82f, size.height * 0.20f)
            cubicTo(size.width * 0.97f, size.height * 0.38f, size.width * 0.88f, size.height * 0.63f, size.width / 2, size.height * 0.88f)
            close()
        }
        if (filled) drawPath(path, color) else drawPath(path, color, style = Stroke(1.7.dp.toPx()))
    }
}

@Composable
internal fun StarIcon(filled: Boolean, iconSize: Dp = 12.dp) {
    val color = MaterialTheme.colorScheme.primary
    Canvas(Modifier.size(iconSize)) {
        val path = Path()
        repeat(10) { point ->
            val radius = if (point % 2 == 0) size.minDimension / 2 else size.minDimension * 0.22f
            val angle = Math.toRadians((-90 + point * 36).toDouble())
            val offset = Offset(
                x = size.width / 2 + (cos(angle) * radius).toFloat(),
                y = size.height / 2 + (sin(angle) * radius).toFloat(),
            )
            if (point == 0) path.moveTo(offset.x, offset.y) else path.lineTo(offset.x, offset.y)
        }
        path.close()
        if (filled) drawPath(path, color) else drawPath(path, color, style = Stroke(1.dp.toPx()))
    }
}

@Composable
internal fun MinusIcon() {
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(Modifier.size(13.dp)) {
        drawLine(color, Offset(1.dp.toPx(), size.height / 2), Offset(size.width - 1.dp.toPx(), size.height / 2), 1.dp.toPx(), StrokeCap.Round)
    }
}

@Composable
internal fun PlusIcon() {
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(Modifier.size(13.dp)) {
        val stroke = 1.dp.toPx()
        drawLine(color, Offset(stroke, size.height / 2), Offset(size.width - stroke, size.height / 2), stroke, StrokeCap.Round)
        drawLine(color, Offset(size.width / 2, stroke), Offset(size.width / 2, size.height - stroke), stroke, StrokeCap.Round)
    }
}

@Composable
internal fun BagIcon() {
    val color = MaterialTheme.colorScheme.onPrimary
    Canvas(Modifier.size(15.dp)) {
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.18f, size.height * 0.3f),
            size = Size(size.width * 0.64f, size.height * 0.58f),
            cornerRadius = CornerRadius(1.5.dp.toPx()),
            style = Stroke(1.dp.toPx()),
        )
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(size.width * 0.34f, size.height * 0.08f),
            size = Size(size.width * 0.32f, size.height * 0.42f),
            style = Stroke(1.dp.toPx()),
        )
    }
}

@Composable
internal fun CheckIcon() {
    val color = MaterialTheme.colorScheme.onPrimary
    Canvas(Modifier.size(15.dp)) {
        drawLine(color, Offset(size.width * 0.14f, size.height * 0.52f), Offset(size.width * 0.42f, size.height * 0.78f), 1.5.dp.toPx(), StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.42f, size.height * 0.78f), Offset(size.width * 0.88f, size.height * 0.22f), 1.5.dp.toPx(), StrokeCap.Round)
    }
}
