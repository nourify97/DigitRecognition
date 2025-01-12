package com.nourify.digitrecognition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nourify.digitrecognition.ui.screens.drawing.DrawProcessingVM
import com.nourify.digitrecognition.ui.screens.drawing.DrawingCanvas
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: DrawProcessingVM

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            viewModel = koinViewModel()

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        colors =
                            TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                        title = {
                            Text(
                                "Digit Recognizer",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier =
                        Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                ) {
                    DrawingCanvas(
                        modifier =
                            Modifier
                                .weight(6F)
                                .fillMaxWidth(),
                        paths = viewModel.paths,
                        currentPath = viewModel.currentPath.value,
                        currentPathRef = viewModel.currentPathRef.intValue,
                        onDrag = { viewModel.onDrag(it) },
                        onDragStart = { viewModel.onDragStart(it) },
                        onDragCancel = viewModel::onCancelDrag,
                        onDragEnd = { viewModel.onDragEnd() },
                        onProcessBitmap = { viewModel.processBitmap(it) },
                    )
                    Column(
                        modifier =
                            Modifier
                                .weight(4F)
                                .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        viewModel.error.value?.let {
                            Text(text = it, fontSize = 20.sp)
                        }
                        Text(
                            text = viewModel.result.value,
                            fontSize = 20.sp,
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(onClick = viewModel::clearCanvas) {
                            Text(text = "Clear", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel::closeClassifier
    }
}
