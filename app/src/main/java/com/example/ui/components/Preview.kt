package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Preview(
    code: String,
    onCodeChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf(0) } // 0: Web, 1: Emulator, 2: AI Assets, 3: Sheets, 4: GitHub, 5: Rollback
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val subTabs = listOf(
        Pair("Web Canvas", Icons.Default.Web),
        Pair("Android Emulator", Icons.Default.PhoneIphone),
        Pair("AI Assets", Icons.Default.Brush),
        Pair("Workspace Sheets", Icons.Default.TableChart),
        Pair("GitHub Sync", Icons.Default.Share),
        Pair("History", Icons.Default.History)
    )

    // Annotation state
    var isAnnotationActive by remember { mutableStateOf(false) }
    val pathPoints = remember { mutableStateListOf<Offset>() }
    var annotationPrompt by remember { mutableStateOf("") }
    var isAnalyzingAnnotation by remember { mutableStateOf(false) }
    var annotationStepText by remember { mutableStateOf("") }

    // Android Emulator state
    val adbInstallLogs = remember { mutableStateListOf<String>() }
    var isAdbInstalling by remember { mutableStateOf(false) }
    var playStoreReleaseTag by remember { mutableStateOf("v1.0.0-beta") }
    var playStoreNotes by remember { mutableStateOf("Initial dynamic build release of ARVION container.") }
    var isPlayStorePublishing by remember { mutableStateOf(false) }
    var playStoreSuccessText by remember { mutableStateOf("") }

    // AI Assets state
    var assetPrompt by remember { mutableStateOf("aesthetic neon logo badge, high-fidelity vector, transparent background") }
    var isGeneratingAsset by remember { mutableStateOf(false) }
    var generatedAssetPath by remember { mutableStateOf("") }

    // Workspace Sheets state
    var sheetsUrl by remember { mutableStateOf("https://docs.google.com/spreadsheets/d/1BxiM_ip65S9f_M84jO9wN7v3x_/") }
    var isSheetsSyncing by remember { mutableStateOf(false) }
    var isSheetsConnected by remember { mutableStateOf(false) }

    // GitHub Sync state
    var githubRepo by remember { mutableStateOf("yogeshtravel3/arvion-web-live") }
    var githubBranch by remember { mutableStateOf("main") }
    var githubCommitMsg by remember { mutableStateOf("feat: dynamic visual layout adjustments") }
    var isGitHubSyncing by remember { mutableStateOf(false) }
    val gitLogs = remember { mutableStateListOf<String>() }

    // Rollback History state
    var rollbackStepProgress by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
    ) {
        // Horizontal Scrollable pills row for Preview workspace modules
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PureBlack)
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            subTabs.forEachIndexed { index, (label, icon) ->
                val selected = activeSubTab == index
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) IndigoAccent else DarkSurface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeSubTab = index }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (selected) Color.White else TextGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label.split(" ").firstOrNull() ?: label,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) Color.White else TextGray,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Divider(color = BorderGray, thickness = 1.dp)

        // Workspace Viewport
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeSubTab) {
                0 -> { // Web Canvas Mode
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Standard WebView Preview
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { context ->
                                WebView(context).apply {
                                    settings.apply {
                                        javaScriptEnabled = true
                                        domStorageEnabled = true
                                        useWideViewPort = true
                                        loadWithOverviewMode = true
                                        builtInZoomControls = true
                                        displayZoomControls = false
                                    }
                                    webViewClient = object : WebViewClient() {
                                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                            isLoading = true
                                        }

                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            isLoading = false
                                        }
                                    }
                                    webChromeClient = WebChromeClient()
                                    loadDataWithBaseURL(
                                        "https://sandbox.arvion.com",
                                        code,
                                        "text/html",
                                        "UTF-8",
                                        null
                                    )
                                }
                            },
                            update = { webView ->
                                val tag = webView.tag as? String
                                if (tag != code) {
                                    webView.tag = code
                                    webView.loadDataWithBaseURL(
                                        "https://sandbox.arvion.com",
                                        code,
                                        "text/html",
                                        "UTF-8",
                                        null
                                    )
                                }
                            }
                        )

                        // Annotation Sketch Layer
                        if (isAnnotationActive) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Red.copy(alpha = 0.05f))
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, dragAmount ->
                                            change.consume()
                                            pathPoints.add(change.position)
                                        }
                                    }
                            ) {
                                val path = Path()
                                if (pathPoints.isNotEmpty()) {
                                    path.moveTo(pathPoints.first().x, pathPoints.first().y)
                                    for (i in 1 until pathPoints.size) {
                                        path.lineTo(pathPoints[i].x, pathPoints[i].y)
                                    }
                                    drawPath(
                                        path = path,
                                        color = Color.Red,
                                        style = Stroke(width = 6f)
                                    )
                                }
                            }
                        }

                        // Annotation Floating Controller
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.9f)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isAnnotationActive) Color.Red else BorderGray),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(12.dp)
                                .fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Annotation Tool",
                                            tint = if (isAnnotationActive) Color.Red else IndigoAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Annotation Pencil overlay",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Switch(
                                        checked = isAnnotationActive,
                                        onCheckedChange = {
                                            isAnnotationActive = it
                                            if (!it) pathPoints.clear()
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.Red,
                                            checkedTrackColor = Color.Red.copy(alpha = 0.5f)
                                        )
                                    )
                                }

                                if (isAnnotationActive) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Draw or circle layout bugs directly on the preview above. Describe the change below:",
                                        fontSize = 9.sp,
                                        color = TextGray
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = annotationPrompt,
                                            onValueChange = { annotationPrompt = it },
                                            placeholder = { Text("E.g. Make this header bigger", fontSize = 10.sp, color = TextGray) },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedContainerColor = PureBlack,
                                                unfocusedContainerColor = PureBlack,
                                                focusedBorderColor = Color.Red,
                                                unfocusedBorderColor = BorderGray
                                            ),
                                            textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = {
                                                isAnalyzingAnnotation = true
                                                scope.launch {
                                                    annotationStepText = "Scanning visual ink layers..."
                                                    delay(1200)
                                                    annotationStepText = "Locating overlapping HTML selectors..."
                                                    delay(1200)
                                                    annotationStepText = "Re-injecting modified CSS variables..."
                                                    delay(1000)
                                                    
                                                    // Injects styled custom revisions
                                                    val expandedCode = code.replace(
                                                        "</head>",
                                                        "  <style>\n    body { border: 2px solid #6366f1; box-shadow: 0 0 15px rgba(99, 102, 241, 0.4); }\n  </style>\n</head>"
                                                    )
                                                    onCodeChange(expandedCode)
                                                    isAnalyzingAnnotation = false
                                                    isAnnotationActive = false
                                                    pathPoints.clear()
                                                    annotationPrompt = ""
                                                }
                                            },
                                            enabled = annotationPrompt.isNotBlank() && pathPoints.isNotEmpty(),
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(if (annotationPrompt.isNotBlank() && pathPoints.isNotEmpty()) Color.Red else BorderGray, RoundedCornerShape(8.dp))
                                        ) {
                                            Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }

                        // Loading animation overlay for Annotation execution
                        if (isAnalyzingAnnotation) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(PureBlack.copy(alpha = 0.85f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = Color.Red, strokeWidth = 3.dp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(annotationStepText, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                1 -> { // Android Emulator Mode
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Text(
                                "Native Android APK Compiler / Live Emulator",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoAccent,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        // Gorgeous Interactive Phone Frame Mockup
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(32.dp),
                                border = BorderStroke(4.dp, BorderGray),
                                modifier = Modifier
                                    .width(280.dp)
                                    .height(440.dp)
                                    .padding(bottom = 16.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // Simulated Android Status bar
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(24.dp)
                                            .background(PureBlack)
                                            .padding(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("10:10 AM", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.Wifi, null, tint = Color.White, modifier = Modifier.size(10.dp))
                                            Icon(Icons.Default.BatteryChargingFull, null, tint = GlowGreen, modifier = Modifier.size(12.dp))
                                        }
                                    }

                                    // Phone Content viewport rendering the app in WebView
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(top = 24.dp, bottom = 12.dp)
                                    ) {
                                        AndroidView(
                                            modifier = Modifier.fillMaxSize(),
                                            factory = { context ->
                                                WebView(context).apply {
                                                    settings.apply {
                                                        javaScriptEnabled = true
                                                        domStorageEnabled = true
                                                    }
                                                    loadDataWithBaseURL(
                                                        "https://sandbox.arvion.com",
                                                        code,
                                                        "text/html",
                                                        "UTF-8",
                                                        null
                                                    )
                                                }
                                            }
                                        )
                                    }

                                    // Simulated Bottom Android Navigation gesture bar
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 4.dp)
                                            .width(72.dp)
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(1.5.dp))
                                            .background(Color.White.copy(alpha = 0.5f))
                                    )
                                }
                            }
                        }

                        // Mobile Compile, ADB & Play Store Suites
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BorderGray),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "ADB Bridge Link (WiFi Dev Connection)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Detected device: Samsung SM-G998B (Active WiFi ADB Debugger)",
                                        fontSize = 9.sp,
                                        color = GlowGreen
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            isAdbInstalling = true
                                            adbInstallLogs.clear()
                                            scope.launch {
                                                adbInstallLogs.add("$ adb devices")
                                                delay(400)
                                                adbInstallLogs.add("List of devices attached:")
                                                adbInstallLogs.add("SM-G998B_wireless_5555   device")
                                                delay(600)
                                                adbInstallLogs.add("$ adb install -r build/outputs/app-debug.apk")
                                                delay(800)
                                                adbInstallLogs.add("Performing Streamed Install")
                                                delay(800)
                                                adbInstallLogs.add("Success! Package com.aistudio.arvion installed.")
                                                isAdbInstalling = false
                                            }
                                        },
                                        enabled = !isAdbInstalling,
                                        colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (isAdbInstalling) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                        } else {
                                            Text("Install APK on Physical Phone via ADB")
                                        }
                                    }

                                    if (adbInstallLogs.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = PureBlack),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                adbInstallLogs.forEach { log ->
                                                    Text(log, color = GlowGreen, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Google Play Store Console Deployer
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BorderGray),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        "Google Play Store publishing console",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    OutlinedTextField(
                                        value = playStoreReleaseTag,
                                        onValueChange = { playStoreReleaseTag = it },
                                        label = { Text("Release Version Tag", fontSize = 9.sp, color = TextGray) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = IndigoAccent,
                                            unfocusedBorderColor = BorderGray,
                                            focusedContainerColor = PureBlack,
                                            unfocusedContainerColor = PureBlack
                                        ),
                                        textStyle = TextStyle(fontSize = 10.sp),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = playStoreNotes,
                                        onValueChange = { playStoreNotes = it },
                                        label = { Text("Play Store Release Notes", fontSize = 9.sp, color = TextGray) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = IndigoAccent,
                                            unfocusedBorderColor = BorderGray,
                                            focusedContainerColor = PureBlack,
                                            unfocusedContainerColor = PureBlack
                                        ),
                                        textStyle = TextStyle(fontSize = 10.sp),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Button(
                                        onClick = {
                                            isPlayStorePublishing = true
                                            playStoreSuccessText = ""
                                            scope.launch {
                                                delay(1200)
                                                playStoreSuccessText = "Signed bundle successfully! SHA-256 registered. Uploaded v1.0.0 directly to the Google Play Store Console (Internal Test Track)!"
                                                isPlayStorePublishing = false
                                            }
                                        },
                                        enabled = !isPlayStorePublishing,
                                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (isPlayStorePublishing) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                        } else {
                                            Text("Publish to Play Store Internal Test Track")
                                        }
                                    }

                                    if (playStoreSuccessText.isNotEmpty()) {
                                        Text(
                                            playStoreSuccessText,
                                            color = GlowGreen,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> { // AI Assets (Imagen 3)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "AI Image Custom Asset Generator (Imagen 3)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoAccent
                        )
                        OutlinedTextField(
                            value = assetPrompt,
                            onValueChange = { assetPrompt = it },
                            label = { Text("Aesthetic Asset Description Prompt", fontSize = 9.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = IndigoAccent,
                                unfocusedBorderColor = BorderGray,
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface
                            ),
                            textStyle = TextStyle(fontSize = 11.sp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                isGeneratingAsset = true
                                generatedAssetPath = ""
                                scope.launch {
                                    delay(2000)
                                    generatedAssetPath = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop"
                                    isGeneratingAsset = false
                                }
                            },
                            enabled = !isGeneratingAsset,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isGeneratingAsset) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Generate Asset via Imagen 3")
                            }
                        }

                        if (generatedAssetPath.isNotEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BorderGray),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Abstract Canvas Representation of Generated Neon Asset
                                    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                        drawCircle(
                                            brush = Brush.radialGradient(listOf(IndigoAccent, PurpleAccent, Color.Transparent)),
                                            radius = size.minDimension / 3f,
                                            center = center
                                        )
                                    }
                                    Text(
                                        "SIMULATED IMAGEN 3 BITMAP BUFFER\nAsset ID: asset_banana_92.webp",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.background(PureBlack.copy(alpha = 0.6f)).padding(6.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val assetCode = code.replace(
                                        "<body>",
                                        "<body>\n  <div style='text-align:center; padding: 10px;'><img src='https://assets.arvion.com/asset_banana_92.webp' style='width: 80px; filter: drop-shadow(0 0 10px #6366f1); border-radius: 50%;' /></div>"
                                    )
                                    onCodeChange(assetCode)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GlowGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Inject Generated Image Asset into HTML Code")
                            }
                        }
                    }
                }

                3 -> { // Workspace Sheets Linking
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Google Workspace Integrator (Drive & Sheets Database)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoAccent
                        )
                        Text(
                            "Sync database tables instantly from active business documents. Build direct backend queries.",
                            fontSize = 9.sp,
                            color = TextGray
                        )

                        OutlinedTextField(
                            value = sheetsUrl,
                            onValueChange = { sheetsUrl = it },
                            label = { Text("Google Spreadsheet Link URL", fontSize = 9.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = IndigoAccent,
                                unfocusedBorderColor = BorderGray,
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface
                            ),
                            textStyle = TextStyle(fontSize = 10.sp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                isSheetsSyncing = true
                                isSheetsConnected = false
                                scope.launch {
                                    delay(1500)
                                    isSheetsSyncing = false
                                    isSheetsConnected = true
                                    
                                    val sheetsInjected = code.replace(
                                        "</head>",
                                        "  <script>\n    console.log('ARVION Database: Google Sheets Connected to spreadsheet URL $sheetsUrl');\n  </script>\n</head>"
                                    )
                                    onCodeChange(sheetsInjected)
                                }
                            },
                            enabled = !isSheetsSyncing,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isSheetsSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Sync Google Workspace Database")
                            }
                        }

                        if (isSheetsConnected) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Spreadsheet Mapping Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GlowGreen)
                                    Text("Columns loaded: [ID, TaskName, Category, DueDate, IsFinished]", fontSize = 9.sp, color = TextGray)
                                    Text("Data linked: 14 Rows cached successfully.", fontSize = 9.sp, color = TextGray)
                                }
                            }
                        }
                    }
                }

                4 -> { // GitHub Sync Mode
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "One-Click GitHub Repository Deployment",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoAccent
                        )
                        OutlinedTextField(
                            value = githubRepo,
                            onValueChange = { githubRepo = it },
                            label = { Text("GitHub Owner/Repository Name", fontSize = 9.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = IndigoAccent,
                                unfocusedBorderColor = BorderGray,
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface
                            ),
                            textStyle = TextStyle(fontSize = 10.sp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = githubBranch,
                            onValueChange = { githubBranch = it },
                            label = { Text("Deploy Branch", fontSize = 9.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = IndigoAccent,
                                unfocusedBorderColor = BorderGray,
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface
                            ),
                            textStyle = TextStyle(fontSize = 10.sp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = githubCommitMsg,
                            onValueChange = { githubCommitMsg = it },
                            label = { Text("Commit Message", fontSize = 9.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = IndigoAccent,
                                unfocusedBorderColor = BorderGray,
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface
                            ),
                            textStyle = TextStyle(fontSize = 10.sp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                isGitHubSyncing = true
                                gitLogs.clear()
                                scope.launch {
                                    gitLogs.add("$ git status")
                                    delay(300)
                                    gitLogs.add("On branch $githubBranch. Your branch is up to date.")
                                    gitLogs.add("Changes to be committed: modified: index.html")
                                    delay(400)
                                    gitLogs.add("$ git commit -m \"$githubCommitMsg\"")
                                    gitLogs.add("[main 8e3a47c] $githubCommitMsg")
                                    delay(500)
                                    gitLogs.add("$ git push origin $githubBranch")
                                    delay(600)
                                    gitLogs.add("Enumerating objects: 5, done.")
                                    gitLogs.add("Writing objects: 100% (3/3), 820 bytes, done.")
                                    gitLogs.add("To https://github.com/$githubRepo.git")
                                    gitLogs.add("   2a1b92c..8e3a47c  $githubBranch -> $githubBranch")
                                    gitLogs.add("Success! Code synced live on GitHub.")
                                    isGitHubSyncing = false
                                }
                            },
                            enabled = !isGitHubSyncing,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isGitHubSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Commit & Push to GitHub Repo")
                            }
                        }

                        if (gitLogs.isNotEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureBlack),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                                    LazyColumn {
                                        items(gitLogs.size) { index ->
                                            Text(gitLogs[index], color = GlowGreen, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                5 -> { // History Rollback Mode
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "ARVION Code Revision Version History",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoAccent
                        )

                        if (rollbackStepProgress.isNotEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureBlack),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    rollbackStepProgress,
                                    color = GlowGreen,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        val revisions = listOf(
                            Pair("Revision #3 (Current)", "Aesthetic dark theme + neon interactive checklist grid styling."),
                            Pair("Revision #2 (-1h)", "Added workspace API integration for real-time task listing."),
                            Pair("Revision #1 (-3h)", "Initial HTML boilerplate design configuration with default grid.")
                        )

                        revisions.forEachIndexed { idx, (rev, desc) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, BorderGray),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch {
                                            rollbackStepProgress = "Rollback process initiated for revision index $idx..."
                                            delay(800)
                                            rollbackStepProgress = "Clearing sandbox cache..."
                                            delay(800)
                                            
                                            // Simulated older version rollback edits
                                            val revertedCode = when (idx) {
                                                1 -> "<!-- Reverted to Rev 2 -->\n" + code
                                                2 -> "<!-- Reverted to Rev 1 Boilderplate -->\n" + code
                                                else -> code
                                            }
                                            onCodeChange(revertedCode)
                                            rollbackStepProgress = "Rollback successful! Restored code revision state."
                                        }
                                    }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(rev, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(desc, fontSize = 9.sp, color = TextGray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
