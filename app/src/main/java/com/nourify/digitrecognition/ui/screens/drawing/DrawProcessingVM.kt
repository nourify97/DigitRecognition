package com.nourify.digitrecognition.ui.screens.drawing

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import com.nourify.digitrecognition.data.DigitRecognizer
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class DrawProcessingVM(
    private val digitRecognizer: DigitRecognizer,
) : ViewModel() {
    val paths = mutableStateListOf<Path>()
    val currentPath = mutableStateOf<Path?>(null)
    val currentPathRef = mutableIntStateOf(1)
    private val lastOffset = mutableStateOf<Offset?>(null)

    val result = mutableStateOf("")
    val error = mutableStateOf<String?>("")

    init {
        initializeClassifier()
    }

    fun onDragStart(offset: Offset) {
        currentPath.value = Path()
        currentPath.value?.moveTo(offset.x, offset.y)
        currentPathRef.intValue++
        lastOffset.value = offset
    }

    fun onDrag(offset: Offset) {
        if (lastOffset.value != null) {
            val newOffset =
                Offset(
                    lastOffset.value!!.x + offset.x,
                    lastOffset.value!!.y + offset.y,
                )
            currentPath.value?.lineTo(newOffset.x, newOffset.y)
            currentPathRef.intValue++
            lastOffset.value = newOffset
        }
    }

    fun onDragEnd() {
        currentPath.value?.let { value ->
            paths.add(value)
            currentPath.value = null
            currentPathRef.intValue = 0
        }
    }

    fun processBitmap(bitmap: ImageBitmap) {
        classifyDrawing(bitmap.asAndroidBitmap())
    }

    fun onCancelDrag() {
        currentPath.value = null
    }

    fun clearCanvas() {
        paths.clear()
        currentPath.value = null
        currentPathRef.intValue = 0
        result.value = ""
    }

    private fun initializeClassifier() {
        digitRecognizer
            .initialize()
            .addOnFailureListener { e -> Log.e(this.javaClass.name, "Error to setting up digit recognizer.", e) }
    }

    private fun classifyDrawing(bitmap: Bitmap) {
        if (digitRecognizer.isInitialized) {
            digitRecognizer
                .classifyAsync(bitmap)
                .addOnSuccessListener { resultText ->
                    result.value = resultText
                }.addOnFailureListener { e ->
                    error.value = e.localizedMessage
                }
        }
    }

    fun closeClassifier() {
        digitRecognizer.close()
    }
}
