package com.nourify.digitrecognition.ui.screens.drawing

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    paths: List<Path>,
    currentPath: Path?,
    currentPathRef: Int,
    onDrag: (Offset) -> Unit,
    onDragStart: (Offset) -> Unit,
    onDragCancel: () -> Unit,
    onDragEnd: () -> Unit,
    onProcessBitmap: (ImageBitmap) -> Unit,
) {
    var canvasSize = Size(0f, 0f)

    Canvas(
        modifier =
            modifier
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            onDragEnd()

                            val bitmap = ImageBitmap(canvasSize.width.toInt(), canvasSize.height.toInt())
                            val drawScope = CanvasDrawScope()
                            val canvas =
                                androidx.compose.ui.graphics
                                    .Canvas(bitmap)

                            drawScope.draw(
                                density = Density(1f),
                                layoutDirection = LayoutDirection.Ltr,
                                canvas = canvas,
                                size = canvasSize,
                            ) {
                                drawRect(color = Color.Black, size = canvasSize)

                                paths.forEach { path ->
                                    drawPath(path = path, color = Color.White, style = Stroke(70.0f))
                                }

                                if (currentPath != null && currentPathRef > 0) {
                                    drawPath(path = currentPath, color = Color.White, style = Stroke(70.0f))
                                }
                            }

                            onProcessBitmap(bitmap)
                            Log.d("DrawingCanvas", "Scaled bitmap processed on drag end")
                        },
                        onDragStart = onDragStart,
                        onDragCancel = onDragCancel,
                        onDrag = { _, amount -> onDrag(amount) },
                    )
                },
    ) {
        this.apply { canvasSize = size }

        paths.forEach { drawPath(path = it, color = Color.White, style = Stroke(70.0f)) }

        if (currentPath != null && currentPathRef > 0) {
            drawPath(path = currentPath, color = Color.White, style = Stroke(70.0f))
        }
    }
}
