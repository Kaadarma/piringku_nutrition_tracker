package com.example.piringku.ui.barcode

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.Executors

@Composable
fun BarcodeScanner(
    onBack: () -> Unit,
    onBarcodeScanned: (String) -> Unit,
    onManualSearch: () -> Unit,
) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            hasCameraPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scanY",
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            CameraPreview(isFlashOn = isFlashOn, onBarcodeScanned = onBarcodeScanned)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
        )

        Box(
            modifier = Modifier
                .size(width = 280.dp, height = 200.dp)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val c = Color(0xFF2d6a4f)
                val strokeW = 6f
                val cornerLen = 32f
                val cornerR = 16f

                drawPath(
                    path = Path().apply {
                        moveTo(0f, cornerR)
                        quadraticBezierTo(0f, 0f, cornerR, 0f)
                        lineTo(cornerLen, 0f)
                    },
                    color = c,
                    style = Stroke(width = strokeW),
                )

                drawPath(
                    path = Path().apply {
                        moveTo(size.width - cornerR, 0f)
                        quadraticBezierTo(size.width, 0f, size.width, cornerR)
                        lineTo(size.width, cornerLen)
                    },
                    color = c,
                    style = Stroke(width = strokeW),
                )

                drawPath(
                    path = Path().apply {
                        moveTo(0f, size.height - cornerR)
                        quadraticBezierTo(0f, size.height, cornerR, size.height)
                        lineTo(cornerLen, size.height)
                    },
                    color = c,
                    style = Stroke(width = strokeW),
                )

                drawPath(
                    path = Path().apply {
                        moveTo(size.width - cornerR, size.height)
                        quadraticBezierTo(size.width, size.height, size.width, size.height - cornerR)
                        lineTo(size.width, size.height - cornerLen)
                    },
                    color = c,
                    style = Stroke(width = strokeW),
                )

                val scanLineY = scanY * size.height
                drawLine(
                    Color(0xFF40916C),
                    Offset(4f, scanLineY),
                    Offset(size.width - 4f, scanLineY),
                    strokeWidth = 2f,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .padding(top = 28.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.White,
                )
            }
            Text(
                text = "Scan Barcode",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            IconButton(
                onClick = { isFlashOn = !isFlashOn },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
            ) {
                Icon(
                    if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Flash",
                    tint = Color.White,
                )
            }
        }

        Text(
            text = "Posisikan barcode di dalam kotak untuk memindai",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 260.dp),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = onManualSearch,
                modifier = Modifier.height(48.dp).width(200.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Input Manual")
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Pastikan pencahayaan cukup terang",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun CameraPreview(
    isFlashOn: Boolean,
    onBarcodeScanned: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var camera by remember { mutableStateOf<Camera?>(null) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val analyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                analyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy: ImageProxy ->
                    scanBarcode(imageProxy, onBarcodeScanned)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    val cam = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        analyzer,
                    )
                    camera = cam
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize(),
    )

    LaunchedEffect(isFlashOn, camera) {
        camera?.cameraControl?.enableTorch(isFlashOn)
    }
}

private fun scanBarcode(imageProxy: ImageProxy, onResult: (String) -> Unit) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = com.google.mlkit.vision.common.InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes: List<Barcode> ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { value ->
                        onResult(value)
                        break
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}
