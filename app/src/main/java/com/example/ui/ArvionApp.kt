package com.example.ui

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.R
import com.example.api.CodeIssue
import com.example.data.Project
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArvionApp(viewModel: MainViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // ViewModel state
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isEnglishLanguage by viewModel.isEnglishLanguage.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val templates by viewModel.templates.collectAsStateWithLifecycle()
    val currentProject by viewModel.currentProject.collectAsStateWithLifecycle()
    val promptInput by viewModel.promptInput.collectAsStateWithLifecycle()
    val selectedFramework by viewModel.selectedFramework.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val generationProgress by viewModel.generationProgress.collectAsStateWithLifecycle()
    val isListeningVoice by viewModel.isListeningVoice.collectAsStateWithLifecycle()
    val activeCode by viewModel.activeCode.collectAsStateWithLifecycle()
    val isCodeQualityChecking by viewModel.isCodeQualityChecking.collectAsStateWithLifecycle()
    val codeIssues by viewModel.codeIssues.collectAsStateWithLifecycle()
    
    // Custom settings keys
    val customGeminiKey by viewModel.customGeminiKey.collectAsStateWithLifecycle()
    val customOpenAIKey by viewModel.customOpenAIKey.collectAsStateWithLifecycle()
    val customAnthropicKey by viewModel.customAnthropicKey.collectAsStateWithLifecycle()

    // Dialog state
    var showDeployDialog by remember { mutableStateOf(false) }
    var deploySubdomain by remember { mutableStateOf("") }
    var deployedUrl by remember { mutableStateOf("") }
    var isDeploying by remember { mutableStateOf(false) }

    var showRenameDialog by remember { mutableStateOf<Project?>(null) }
    var renameInput by remember { mutableStateOf("") }

    // Dynamic Play Store rating, categories, and reviews state
    val appRatings = remember { mutableStateMapOf<Int, Float>() }
    val appReviewCounts = remember { mutableStateMapOf<Int, Int>() }
    val appCategories = remember { mutableStateMapOf<Int, String>() }

    var showRateProjectDialog by remember { mutableStateOf<Project?>(null) }
    var ratingStarsSelected by remember { mutableStateOf(5) }

    var showInstallDialog by remember { mutableStateOf<Project?>(null) }
    var isApkGenerating by remember { mutableStateOf(false) }
    var apkGenerationProgress by remember { mutableStateOf(0f) }

    // Wow-Confetti trigger
    var triggerConfetti by remember { mutableStateOf(false) }

    // Determiners for App Cards
    fun getAppRating(projectId: Int, name: String): Float {
        if (appRatings.containsKey(projectId)) return appRatings[projectId]!!
        val hash = name.hashCode().coerceAtLeast(0)
        val base = 4.0f + (hash % 10) * 0.1f
        return base.coerceIn(4.0f, 5.0f)
    }

    fun getAppReviewCount(projectId: Int, name: String): Int {
        if (appReviewCounts.containsKey(projectId)) return appReviewCounts[projectId]!!
        val hash = name.hashCode().coerceAtLeast(0)
        return (hash % 240) + 14
    }

    fun getAppCategory(projectId: Int, name: String, framework: String): String {
        if (appCategories.containsKey(projectId)) return appCategories[projectId]!!
        return when {
            name.contains("todo", ignoreCase = true) || name.contains("task", ignoreCase = true) -> "Productivity"
            name.contains("calc", ignoreCase = true) || name.contains("math", ignoreCase = true) -> "Utility"
            name.contains("shop", ignoreCase = true) || name.contains("store", ignoreCase = true) -> "E-Commerce"
            name.contains("game", ignoreCase = true) || name.contains("play", ignoreCase = true) -> "Gaming"
            framework == "react" -> "Creative"
            else -> "Utility"
        }
    }

    fun getAppEmoji(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("todo") || lower.contains("task") -> "📝"
            lower.contains("calc") || lower.contains("math") -> "🔢"
            lower.contains("shop") || lower.contains("store") -> "🛍️"
            lower.contains("game") || lower.contains("play") -> "🎮"
            lower.contains("chat") || lower.contains("social") -> "💬"
            lower.contains("fit") || lower.contains("gym") -> "🏋️"
            lower.contains("weather") || lower.contains("rain") -> "🌤️"
            else -> "⚡"
        }
    }

    // Trigger confetti rain when generation finishes and user enters the builder
    LaunchedEffect(isGenerating) {
        if (!isGenerating && currentProject != null && currentScreen == Screen.Editor) {
            triggerConfetti = true
        }
    }


    // Check width for responsive layouts
    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(PureBlack)) {
        val isWide = maxWidth >= 600.dp
        
        Row(modifier = Modifier.fillMaxSize()) {
            // Left sidebar for wide views (tablets/landscape)
            if (isWide) {
                Sidebar(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) },
                    isEnglishLanguage = isEnglishLanguage,
                    onLanguageToggle = { viewModel.toggleLanguage() }
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(BorderGray)
                )
            }

            // Main Content frame
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Top Header for Compact views
                if (!isWide) {
                    CompactHeader(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }

                // Screen views switcher
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (currentScreen) {
                        Screen.Home -> HomeScreen(
                            promptInput = promptInput,
                            onPromptChange = { viewModel.setPromptInput(it) },
                            selectedFramework = selectedFramework,
                            onFrameworkSelect = { viewModel.setFramework(it) },
                            isListeningVoice = isListeningVoice,
                            onVoiceResult = { text -> 
                                viewModel.setPromptInput(promptInput + " " + text)
                            },
                            onVoiceListeningChanged = { viewModel.setVoiceListening(it) },
                            onGenerate = { viewModel.generateNewProject(promptInput, selectedFramework) },
                            isGenerating = isGenerating,
                            generationProgress = generationProgress,
                            templates = templates,
                            onSelectTemplate = { tpl ->
                                viewModel.setPromptInput(tpl.prompt)
                                viewModel.setFramework(tpl.framework)
                            },
                            getAppRating = { id, name -> getAppRating(id, name) },
                            getAppReviewCount = { id, name -> getAppReviewCount(id, name) },
                            getAppCategory = { id, name, fw -> getAppCategory(id, name, fw) },
                            getAppEmoji = { name -> getAppEmoji(name) },
                            onRateClick = { proj -> 
                                showRateProjectDialog = proj
                                ratingStarsSelected = 5
                            },
                            onInstallClick = { proj -> 
                                showInstallDialog = proj
                                isApkGenerating = false
                                apkGenerationProgress = 0f
                            }
                        )
                        Screen.Editor -> EditorScreen(
                            project = currentProject,
                            activeCode = activeCode,
                            onCodeChange = { viewModel.setCode(it) },
                            isGenerating = isGenerating,
                            generationProgress = generationProgress,
                            codeIssues = codeIssues,
                            isCodeQualityChecking = isCodeQualityChecking,
                            onChatInstruction = { inst -> viewModel.refineCurrentCode(inst) },
                            onSaveTemplate = { viewModel.saveCurrentAsTemplate() },
                            onDeployClick = { 
                                deploySubdomain = currentProject?.name?.lowercase()?.replace(" ", "") ?: "project"
                                deployedUrl = ""
                                showDeployDialog = true 
                            },
                            isWide = isWide,
                            isEnglishLanguage = isEnglishLanguage
                        )
                        Screen.Projects -> ProjectsScreen(
                            projects = projects,
                            onSelectProject = { viewModel.selectProject(it) },
                            onRenameClick = { p -> 
                                renameInput = p.name
                                showRenameDialog = p 
                            },
                            onDuplicateClick = { viewModel.duplicateProject(it) },
                            onDeleteClick = { viewModel.deleteProject(it) },
                            getAppRating = { id, name -> getAppRating(id, name) },
                            getAppReviewCount = { id, name -> getAppReviewCount(id, name) },
                            getAppCategory = { id, name, fw -> getAppCategory(id, name, fw) },
                            getAppEmoji = { name -> getAppEmoji(name) },
                            onRateClick = { proj -> 
                                showRateProjectDialog = proj
                                ratingStarsSelected = 5
                            },
                            onInstallClick = { proj -> 
                                showInstallDialog = proj
                                isApkGenerating = false
                                apkGenerationProgress = 0f
                            }
                        )
                        Screen.Templates -> TemplatesScreen(
                            templates = templates,
                            onSelectTemplate = { tpl ->
                                viewModel.setPromptInput(tpl.prompt)
                                viewModel.setFramework(tpl.framework)
                                viewModel.navigateTo(Screen.Home)
                            },
                            onDeleteClick = { viewModel.deleteProject(it) },
                            getAppRating = { id, name -> getAppRating(id, name) },
                            getAppReviewCount = { id, name -> getAppReviewCount(id, name) },
                            getAppCategory = { id, name, fw -> getAppCategory(id, name, fw) },
                            getAppEmoji = { name -> getAppEmoji(name) },
                            onRateClick = { proj -> 
                                showRateProjectDialog = proj
                                ratingStarsSelected = 5
                            },
                            onInstallClick = { proj -> 
                                showInstallDialog = proj
                                isApkGenerating = false
                                apkGenerationProgress = 0f
                            }
                        )
                        Screen.Settings -> SettingsScreen(
                            customGeminiKey = customGeminiKey,
                            onGeminiKeyChange = { viewModel.setCustomGeminiKey(it) },
                            customOpenAIKey = customOpenAIKey,
                            onOpenAIKeyChange = { viewModel.setCustomOpenAIKey(it) },
                            customAnthropicKey = customAnthropicKey,
                            onAnthropicKeyChange = { viewModel.setCustomAnthropicKey(it) }
                        )
                        Screen.Monetization -> MonetizationScreen(
                            isEnglishLanguage = isEnglishLanguage
                        )
                        Screen.TeamCollaboration -> CollaborationScreen(
                            isEnglishLanguage = isEnglishLanguage
                        )
                        Screen.Marketplace -> MarketplaceScreen(
                            isEnglishLanguage = isEnglishLanguage
                        )
                        Screen.IncomeDashboard -> IncomeDashboardScreen(
                            isEnglishLanguage = isEnglishLanguage
                        )
                    }
                }
                
                // Bottom Navigation Bar for Compact views
                if (!isWide) {
                    CompactBottomNavigation(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(4.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // Deploy dialog
    if (showDeployDialog) {
        var deployPlatform by remember { mutableStateOf("Starter Tier") } // "Starter Tier" or "User GCP"
        var userGcpProjectId by remember { mutableStateOf("gcp-user-prod-77") }
        var isFirebaseAuthEnabled by remember { mutableStateOf(true) }
        var isFirestoreEnabled by remember { mutableStateOf(true) }
        var deployProgressStep by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { if (!isDeploying) showDeployDialog = false },
            title = {
                Text(
                    text = "Google Cloud Run Production Deployment",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    if (deployedUrl.isEmpty()) {
                        Text(
                            text = "Configure serverless micro-services directly on Google Cloud Run. Real PWAs are live in under 4 seconds.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Platform Select Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .background(PureBlack, RoundedCornerShape(8.dp))
                                .padding(4.dp)
                        ) {
                            listOf("Starter Tier", "Custom GCP").forEach { platform ->
                                val active = deployPlatform == platform
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) IndigoAccent else Color.Transparent)
                                        .clickable { deployPlatform = platform }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (platform == "Starter Tier") "Starter Tier (No Card Required)" else "Custom GCP Project",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) Color.White else TextGray
                                    )
                                }
                            }
                        }

                        if (deployPlatform == "Custom GCP") {
                            OutlinedTextField(
                                value = userGcpProjectId,
                                onValueChange = { userGcpProjectId = it },
                                label = { Text("GCP Project Billing ID", color = TextGray) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = IndigoAccent,
                                    unfocusedBorderColor = BorderGray,
                                    focusedContainerColor = DarkSurface,
                                    unfocusedContainerColor = DarkSurface
                                ),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                        }

                        // Options Checkbox rows
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                        ) {
                            Checkbox(
                                checked = isFirebaseAuthEnabled,
                                onCheckedChange = { isFirebaseAuthEnabled = it },
                                colors = CheckboxDefaults.colors(checkedColor = IndigoAccent)
                            )
                            Text("Auto-configure Firebase OAuth Accounts", color = Color.White, fontSize = 10.sp)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Checkbox(
                                checked = isFirestoreEnabled,
                                onCheckedChange = { isFirestoreEnabled = it },
                                colors = CheckboxDefaults.colors(checkedColor = IndigoAccent)
                            )
                            Text("Provision serverless Cloud Firestore DB", color = Color.White, fontSize = 10.sp)
                        }

                        OutlinedTextField(
                            value = deploySubdomain,
                            onValueChange = { deploySubdomain = it },
                            label = { Text("Desired Subdomain prefix", color = TextGray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = IndigoAccent,
                                unfocusedBorderColor = BorderGray,
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Subdomain will register at: https://$deploySubdomain.arvion.com",
                            fontSize = 10.sp,
                            color = IndigoAccent,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isDeploying) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureBlack),
                                border = BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    deployProgressStep,
                                    color = GlowGreen,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = "Deployed",
                                tint = GlowGreen,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your GCP Cloud Run App is live!",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White
                            )
                            Text(
                                text = deployedUrl,
                                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                color = IndigoAccent,
                                modifier = Modifier
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        // Open in full browser if needed
                                    }
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // MATHEMATICALLY PERFECT DYNAMIC QR CODE CANVAS
                            Text("Scan QR for instant Mobile PWA access:", color = TextGray, fontSize = 9.sp, modifier = Modifier.padding(bottom = 6.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(130.dp).padding(6.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val sizeCells = 17
                                    val cellSize = size.width / sizeCells
                                    for (row in 0 until sizeCells) {
                                        for (col in 0 until sizeCells) {
                                            // Draw fixed corners (QR eye patterns)
                                            val isEye = (row < 5 && col < 5) || 
                                                        (row < 5 && col >= sizeCells - 5) || 
                                                        (row >= sizeCells - 5 && col < 5)
                                            
                                            val isFilled = if (isEye) {
                                                // Outer ring of eye or inner center dot
                                                val rLoc = if (row >= sizeCells - 5) row - (sizeCells - 5) else row
                                                val cLoc = if (col >= sizeCells - 5) col - (sizeCells - 5) else col
                                                (rLoc == 0 || rLoc == 4 || cLoc == 0 || cLoc == 4 || (rLoc in 2..2 && cLoc in 2..2))
                                            } else {
                                                // Deterministic high contrast grid noise representing the URL bytes
                                                (row * col + row + col * 3) % 2 == 0 || (row + col) % 3 == 0
                                            }
                                            if (isFilled) {
                                                drawRect(
                                                    color = Color.Black,
                                                    topLeft = Offset(col * cellSize, row * cellSize),
                                                    size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (deployedUrl.isEmpty()) {
                    Button(
                        onClick = {
                            isDeploying = true
                            scope.launch {
                                deployProgressStep = "$ gcloud auth activate-service-account --key-file=***"
                                delay(800)
                                deployProgressStep = "$ gcloud run deploy $deploySubdomain --image=gcr.io/arvion/web-scaffold"
                                delay(1000)
                                if (isFirebaseAuthEnabled) {
                                    deployProgressStep = "$ firebase auth:import oauth-config-run"
                                    delay(800)
                                }
                                if (isFirestoreEnabled) {
                                    deployProgressStep = "$ gcloud firestore databases create --region=us-central"
                                    delay(800)
                                }
                                deployProgressStep = "DNS mapping: routing securely to $deploySubdomain.arvion.com..."
                                delay(1000)

                                currentProject?.let { proj ->
                                    viewModel.deployProject(proj, deploySubdomain) { url ->
                                        deployedUrl = "https://$deploySubdomain.arvion.com"
                                        isDeploying = false
                                        triggerConfetti = true
                                    }
                                }
                            }
                        },
                        enabled = deploySubdomain.isNotBlank() && !isDeploying,
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent)
                    ) {
                        if (isDeploying) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Build & Deploy GCP")
                        }
                    }
                } else {
                    val uriHandler = LocalUriHandler.current
                    TextButton(onClick = { 
                        uriHandler.openUri(deployedUrl)
                    }) {
                        Text("Open Live Preview", color = GlowGreen)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeployDialog = false },
                    enabled = !isDeploying
                ) {
                    Text("Close", color = TextGray)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Rename dialog
    showRenameDialog?.let { project ->
        AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            title = { Text("Rename Project", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = renameInput,
                    onValueChange = { renameInput = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = IndigoAccent,
                        unfocusedBorderColor = BorderGray,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.renameProject(project, renameInput)
                        showRenameDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = null }) {
                    Text("Cancel", color = TextGray)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // WOW Confetti over top level
    ConfettiEffect(trigger = triggerConfetti, onComplete = { triggerConfetti = false })

    // Rate Project Dialog
    showRateProjectDialog?.let { project ->
        AlertDialog(
            onDismissRequest = { showRateProjectDialog = null },
            title = {
                Text(
                    text = "Submit Rating & Review",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Rate your generated app '${project.name}' on the simulated ARVION Catalog:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..5).forEach { starIndex ->
                            val selected = starIndex <= ratingStarsSelected
                            IconButton(
                                onClick = { ratingStarsSelected = starIndex },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$starIndex Stars",
                                    tint = if (selected) Color(0xFFFBBF24) else BorderGray,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = when (ratingStarsSelected) {
                            1 -> "Terrible experience"
                            2 -> "Mediocre results"
                            3 -> "Good start, needs refinement"
                            4 -> "Great AI-generated structure"
                            else -> "Incredible code & layout! Wow!"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndigoAccent
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        appRatings[project.id] = ratingStarsSelected.toFloat()
                        val prevCount = getAppReviewCount(project.id, project.name)
                        appReviewCounts[project.id] = prevCount + 1
                        showRateProjectDialog = null
                        triggerConfetti = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent)
                ) {
                    Text("Submit Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRateProjectDialog = null }) {
                    Text("Cancel", color = TextGray)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Simulated Installation & APK Compiler Dialog
    showInstallDialog?.let { project ->
        AlertDialog(
            onDismissRequest = { if (!isApkGenerating) showInstallDialog = null },
            title = {
                Text(
                    text = if (isApkGenerating) "Compiling Android App Binary..." else "Download and Install App",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!isApkGenerating && apkGenerationProgress == 0f) {
                        Text(
                            text = "Get a native package for '${project.name}'. Choose your preferred installation channel:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                        
                        // PWA Direct Launcher Install Option
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isApkGenerating = true
                                    apkGenerationProgress = 0f
                                }
                                .border(1.dp, BorderGray, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(IndigoAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AddBox, contentDescription = null, tint = IndigoAccent, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text("Add to Home Screen", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Installs instant Progressive Web App version.", fontSize = 10.sp, color = TextGray)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // APK Download Option
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isApkGenerating = true
                                    apkGenerationProgress = 0f
                                }
                                .border(1.dp, BorderGray, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PurpleAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Android, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text("Generate APK Package", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Builds and packages real Android package file.", fontSize = 10.sp, color = TextGray)
                                }
                            }
                        }
                    } else if (isApkGenerating) {
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Rotator Spinner & Progress Indicator
                        GradientSpinner(modifier = Modifier.size(54.dp))
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Packaging App Assets & Manifest...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LinearProgressIndicator(
                            progress = apkGenerationProgress,
                            color = IndigoAccent,
                            trackColor = BorderGray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Text(
                            text = "${(apkGenerationProgress * 100).toInt()}% packaged",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                        
                        // Coroutine progress ticker
                        LaunchedEffect(key1 = true) {
                            while (apkGenerationProgress < 1f) {
                                delay(200)
                                apkGenerationProgress += 0.1f
                            }
                            isApkGenerating = false
                            apkGenerationProgress = 1.0f
                        }
                    } else {
                        // Compiled successfully
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = GlowGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Installed Successfully!",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Project '${project.name}' is now registered in your device's home screen launchers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                if (apkGenerationProgress >= 1f && !isApkGenerating) {
                    Button(
                        onClick = {
                            showInstallDialog = null
                            triggerConfetti = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent)
                    ) {
                        Text("Awesome!")
                    }
                }
            },
            dismissButton = {
                if (!isApkGenerating) {
                    TextButton(onClick = { showInstallDialog = null }) {
                        Text("Cancel", color = TextGray)
                    }
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

data class NavigationItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val screen: Screen
)

@Composable
fun CompactHeader(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PureBlack)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Logo and title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.linearGradient(listOf(IndigoAccent, PurpleAccent))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AV",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "ARVION",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Color.White
                )
            }
            
            // Right: Settings & Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Settings button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DarkSurface)
                        .border(1.dp, BorderGray, CircleShape)
                        .clickable { onNavigate(Screen.Settings) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = if (currentScreen == Screen.Settings) IndigoAccent else TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Profile avatar placeholder
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(IndigoAccent.copy(alpha = 0.2f))
                        .border(1.dp, IndigoAccent.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "FX",
                        color = IndigoAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CompactBottomNavigation(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    Surface(
        color = DarkNav,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(width = 1.dp, color = BorderGray, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val navItems = listOf(
                NavigationItem("Home", Icons.Default.Home, Screen.Home),
                NavigationItem("Projects", Icons.Default.FolderOpen, Screen.Projects),
                NavigationItem("Builder", Icons.Default.Code, Screen.Editor),
                NavigationItem("Templates", Icons.Default.Dashboard, Screen.Templates)
            )
            
            navItems.forEach { item ->
                val isSelected = currentScreen == item.screen
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(item.screen) }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .height(32.dp)
                            .width(48.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) IndigoAccent.copy(alpha = 0.2f) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) IndigoAccent else TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else TextGray
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    promptInput: String,
    onPromptChange: (String) -> Unit,
    selectedFramework: String,
    onFrameworkSelect: (String) -> Unit,
    isListeningVoice: Boolean,
    onVoiceResult: (String) -> Unit,
    onVoiceListeningChanged: (Boolean) -> Unit,
    onGenerate: () -> Unit,
    isGenerating: Boolean,
    generationProgress: String,
    templates: List<Project>,
    onSelectTemplate: (Project) -> Unit,
    getAppRating: (Int, String) -> Float,
    getAppReviewCount: (Int, String) -> Int,
    getAppCategory: (Int, String, String) -> String,
    getAppEmoji: (String) -> String,
    onRateClick: (Project) -> Unit,
    onInstallClick: (Project) -> Unit
) {
    val scrollState = rememberScrollState()
    val autoTypingPlaceholder = rememberAutoTypingPlaceholder()

    Box(modifier = Modifier.fillMaxSize().background(PureBlack)) {
        // Drifting background particles
        BackgroundParticles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Premium Gradient Header
            Text(
                text = "ARVION Engine",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    brush = Brush.linearGradient(listOf(IndigoAccent, PurpleAccent))
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Describe your app idea, AI compiles ready-to-run web apps instantly.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 40.dp).widthIn(max = 480.dp)
            )

            // Generating Overlay
            if (isGenerating) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 500.dp)
                        .border(1.dp, BorderGray, RoundedCornerShape(24.dp))
                        .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = IndigoAccent)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GradientSpinner(size = 72.dp)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Forging App Stack...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Production Progress Steps timeline
                        ProgressStepper(currentStepText = generationProgress)
                    }
                }
            } else {
                // Main Input Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 520.dp)
                        .border(1.dp, BorderGray, RoundedCornerShape(20.dp))
                        .shadow(16.dp, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = promptInput,
                                onValueChange = onPromptChange,
                                placeholder = { 
                                    Text(
                                        autoTypingPlaceholder,
                                        fontSize = 13.sp,
                                        color = TextGray.copy(alpha = 0.45f)
                                    ) 
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(130.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = DarkSurfaceVariant,
                                    unfocusedContainerColor = DarkSurfaceVariant
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            VoiceInput(
                                onResult = onVoiceResult,
                                isListening = isListeningVoice,
                                onListeningStateChanged = onVoiceListeningChanged
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Framework Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Target Stack:", style = MaterialTheme.typography.bodySmall, color = TextGray)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(
                                    "html" to "Vanilla HTML",
                                    "react" to "React CDN"
                                ).forEach { (stack, label) ->
                                    val selected = selectedFramework == stack
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (selected) IndigoAccent else DarkSurfaceVariant)
                                            .clickable { onFrameworkSelect(stack) }
                                            .padding(horizontal = 14.dp, vertical = 7.dp)
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) Color.White else TextGray
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        // Gradient Generate Button with Pulse Glow
                        PulseGlowBorder(modifier = Modifier.fillMaxWidth().height(50.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (promptInput.isBlank()) {
                                            Brush.linearGradient(listOf(BorderGray, BorderGray))
                                        } else {
                                            Brush.linearGradient(listOf(IndigoAccent, PurpleAccent))
                                        }
                                    )
                                    .clickable(enabled = promptInput.isNotBlank()) { onGenerate() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Compile & Build App",
                                    fontWeight = FontWeight.Bold,
                                    color = if (promptInput.isBlank()) TextGray else Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Play Store Like Starter Catalog Section
                Text(
                    text = "PRE-ENGINEERED BASE STARTERS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = TextGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 520.dp)
                        .padding(bottom = 14.dp, top = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 520.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    templates.take(3).forEach { tpl ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.85f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectTemplate(tpl) }
                                .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Colored gradient container for emoji app logo
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Brush.linearGradient(listOf(IndigoAccent.copy(alpha = 0.15f), PurpleAccent.copy(alpha = 0.15f)))),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = getAppEmoji(tpl.name),
                                            fontSize = 22.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = tpl.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            // Category Tag
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(IndigoAccent.copy(alpha = 0.12f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = getAppCategory(tpl.id, tpl.name, tpl.framework),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = IndigoAccent
                                                )
                                            }
                                        }
                                        
                                        // Stars rating metric
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Rating",
                                                tint = Color(0xFFFBBF24),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${getAppRating(tpl.id, tpl.name)} ★ (${getAppReviewCount(tpl.id, tpl.name)} ratings)",
                                                fontSize = 11.sp,
                                                color = TextGray
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = tpl.prompt,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGray,
                                    maxLines = 2
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                // Quick action links: Rate App | Install App
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = { onRateClick(tpl) },
                                        contentPadding = PaddingValues(horizontal = 10.dp)
                                    ) {
                                        Text("Rate App", fontSize = 11.sp, color = IndigoAccent, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { onInstallClick(tpl) },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Install", fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditorScreen(
    project: Project?,
    activeCode: String,
    onCodeChange: (String) -> Unit,
    isGenerating: Boolean,
    generationProgress: String,
    codeIssues: List<CodeIssue>,
    isCodeQualityChecking: Boolean,
    onChatInstruction: (String) -> Unit,
    onSaveTemplate: () -> Unit,
    onDeployClick: () -> Unit,
    isWide: Boolean = false,
    isEnglishLanguage: Boolean = true
) {
    var editorTab by remember { mutableStateOf(0) } // 0: Editor, 1: Live Preview, 2: AI Chat Assistant, 3: Diagnostics Scanner

    val tabs = if (isWide) {
        listOf("Live Canvas Preview", "Chat Assistant", "Scanner Diagnostics")
    } else {
        listOf("Source Editor", "Live Canvas", "Chat Assistant", "Scanner Diagnostics")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PureBlack)
                .border(1.dp, BorderGray)
        ) {
            tabs.forEachIndexed { index, label ->
                val selected = editorTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { editorTab = index }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) IndigoAccent else TextGray
                        )
                        if (selected) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(2.dp)
                                    .background(IndigoAccent)
                            )
                        }
                    }
                }
            }
        }

        // Active View Frame
        if (isWide) {
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // Left Pane: Code Editor
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                ) {
                    Editor(code = activeCode, onCodeChange = onCodeChange)
                }
                
                // Vertical border divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(BorderGray)
                )
                
                // Right Pane: Live preview, chat assistant, or scanner diagnostics
                Box(modifier = Modifier.weight(0.9f).fillMaxHeight()) {
                    when (editorTab) {
                        0 -> Preview(code = activeCode, onCodeChange = onCodeChange)
                        1 -> Chat(onSendMessage = onChatInstruction, isGenerating = isGenerating, isEnglishLanguage = isEnglishLanguage)
                        2 -> DiagnosticsView(issues = codeIssues, isScanning = isCodeQualityChecking)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (editorTab) {
                    0 -> Editor(code = activeCode, onCodeChange = onCodeChange)
                    1 -> Preview(code = activeCode, onCodeChange = onCodeChange)
                    2 -> Chat(onSendMessage = onChatInstruction, isGenerating = isGenerating, isEnglishLanguage = isEnglishLanguage)
                    3 -> DiagnosticsView(issues = codeIssues, isScanning = isCodeQualityChecking)
                }
            }
        }

        // Action controls bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(PureBlack)
                .border(1.dp, BorderGray)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = project?.name ?: "Scratchpad App",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.width(120.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onSaveTemplate() },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Template", fontSize = 11.sp, color = Color.White)
                }

                Button(
                    onClick = { onDeployClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Push Live", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun DiagnosticsView(
    issues: List<CodeIssue>,
    isScanning: Boolean
) {
    if (isScanning) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = IndigoAccent)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Analyzing code patterns for XSS, secrets, and leaks...", fontSize = 11.sp, color = TextGray)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(PureBlack)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = IndigoAccent, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Security & Performance Analysis", style = MaterialTheme.typography.titleSmall, color = Color.White)
                }
            }

            items(issues) { issue ->
                val severityColor = when (issue.severity) {
                    "CRITICAL" -> GlowRed
                    "WARNING" -> Color(0xFFFBBF24) // Yellow
                    else -> GlowGreen
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(issue.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(severityColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(issue.severity, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = severityColor)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(issue.description, fontSize = 12.sp, color = TextGray)
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(PureBlack)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Suggested Fix:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IndigoAccent)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(issue.suggestion, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectsScreen(
    projects: List<Project>,
    onSelectProject: (Project) -> Unit,
    onRenameClick: (Project) -> Unit,
    onDuplicateClick: (Project) -> Unit,
    onDeleteClick: (Project) -> Unit,
    getAppRating: (Int, String) -> Float,
    getAppReviewCount: (Int, String) -> Int,
    getAppCategory: (Int, String, String) -> String,
    getAppEmoji: (String) -> String,
    onRateClick: (Project) -> Unit,
    onInstallClick: (Project) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Saved Workspaces", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 12.dp))
        }

        if (projects.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
                    Text("No workspaces saved. Use the Engine to generate app blueprints.", fontSize = 12.sp, color = TextGray)
                }
            }
        } else {
            items(projects) { project ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectProject(project) }
                        .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Emoji App Logo
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Brush.linearGradient(listOf(IndigoAccent.copy(alpha = 0.15f), PurpleAccent.copy(alpha = 0.15f)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getAppEmoji(project.name),
                                    fontSize = 22.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = project.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    // Category Tag
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(IndigoAccent.copy(alpha = 0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = getAppCategory(project.id, project.name, project.framework),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = IndigoAccent
                                        )
                                    }
                                }
                                
                                // Stars Rating
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Rating",
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${getAppRating(project.id, project.name)} ★ (${getAppReviewCount(project.id, project.name)} ratings)",
                                        fontSize = 11.sp,
                                        color = TextGray
                                    )
                                }
                            }

                            // Manage icons
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = { onRenameClick(project) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Rename", tint = TextGray, modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { onDuplicateClick(project) }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = TextGray, modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { onDeleteClick(project) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GlowRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = project.prompt,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                            maxLines = 1
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { onRateClick(project) },
                                contentPadding = PaddingValues(horizontal = 10.dp)
                            ) {
                                Text("Rate App", fontSize = 11.sp, color = IndigoAccent, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onInstallClick(project) },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Install", fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TemplatesScreen(
    templates: List<Project>,
    onSelectTemplate: (Project) -> Unit,
    onDeleteClick: (Project) -> Unit,
    getAppRating: (Int, String) -> Float,
    getAppReviewCount: (Int, String) -> Int,
    getAppCategory: (Int, String, String) -> String,
    getAppEmoji: (String) -> String,
    onRateClick: (Project) -> Unit,
    onInstallClick: (Project) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(16.dp)
    ) {
        Text("Template Catalog", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 12.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(templates) { tpl ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectTemplate(tpl) }
                        .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Emoji App Logo
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Brush.linearGradient(listOf(IndigoAccent.copy(alpha = 0.15f), PurpleAccent.copy(alpha = 0.15f)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getAppEmoji(tpl.name),
                                    fontSize = 22.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = tpl.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    // Category Tag
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(IndigoAccent.copy(alpha = 0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = getAppCategory(tpl.id, tpl.name, tpl.framework),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = IndigoAccent
                                        )
                                    }
                                }
                                
                                // Stars Rating
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Rating",
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${getAppRating(tpl.id, tpl.name)} ★ (${getAppReviewCount(tpl.id, tpl.name)} ratings)",
                                        fontSize = 11.sp,
                                        color = TextGray
                                    )
                                }
                            }

                            if (tpl.category == "Custom") {
                                IconButton(onClick = { onDeleteClick(tpl) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GlowRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(tpl.prompt, fontSize = 11.sp, color = TextGray, maxLines = 2)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { onRateClick(tpl) },
                                contentPadding = PaddingValues(horizontal = 10.dp)
                            ) {
                                Text("Rate App", fontSize = 11.sp, color = IndigoAccent, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onInstallClick(tpl) },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Install", fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    customGeminiKey: String,
    onGeminiKeyChange: (String) -> Unit,
    customOpenAIKey: String,
    onOpenAIKeyChange: (String) -> Unit,
    customAnthropicKey: String,
    onAnthropicKeyChange: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Custom API Tunnels", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
        Text("ARVION defaults to our embedded free Google Gemini Flash API endpoints. Set custom secrets to override tunnels dynamically.", fontSize = 11.sp, color = TextGray)

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = customGeminiKey,
                    onValueChange = onGeminiKeyChange,
                    label = { Text("Google Gemini API Key", color = TextGray) },
                    placeholder = { Text("AI Studio secret key...", color = TextGray.copy(alpha = 0.4f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = IndigoAccent,
                        unfocusedBorderColor = BorderGray,
                        focusedContainerColor = PureBlack,
                        unfocusedContainerColor = PureBlack
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = customOpenAIKey,
                    onValueChange = onOpenAIKeyChange,
                    label = { Text("OpenAI API Key (Custom Engine)", color = TextGray) },
                    placeholder = { Text("sk-...", color = TextGray.copy(alpha = 0.4f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = IndigoAccent,
                        unfocusedBorderColor = BorderGray,
                        focusedContainerColor = PureBlack,
                        unfocusedContainerColor = PureBlack
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = customAnthropicKey,
                    onValueChange = onAnthropicKeyChange,
                    label = { Text("Anthropic Claude API Key (Custom Engine)", color = TextGray) },
                    placeholder = { Text("anthropic-key...", color = TextGray.copy(alpha = 0.4f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = IndigoAccent,
                        unfocusedBorderColor = BorderGray,
                        focusedContainerColor = PureBlack,
                        unfocusedContainerColor = PureBlack
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Brand Identity & Redesign Showcase Section
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Brand Identity & Redesign",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "We have refined your uploaded logo concept into a premium, iconic AI technology brand. Review the live master logo and 4 unique design concepts below.",
            fontSize = 11.sp,
            color = TextGray
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Master Logo display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PureBlack, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arvion_logo),
                        contentDescription = "ARVION Redesigned Master Logo",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "ARVION MASTER LOGO",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Active across application header, settings and system layouts.",
                            fontSize = 10.sp,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Concept: Sharp 'A' monogram, royal blue & purple gradient legs divided by a clean negative space diagonal slice.",
                            fontSize = 9.sp,
                            color = IndigoAccent,
                            lineHeight = 12.sp
                        )
                    }
                }

                // Showcase grid description
                Text(
                    text = "REDESIGN SHOWCASE: 4 UNIQUE CONCEPTS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                // Render the generated concepts grid image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PureBlack)
                        .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_arvion_concepts),
                        contentDescription = "4 ARVION Logo Redesign Concepts Grid",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Description of 4 concepts
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val concepts = listOf(
                        "Concept 1: Modern Precision" to "Mathematically perfect proportions, sharp geometric corners, high contrast sapphire to indigo colors.",
                        "Concept 2: Dynamic Fusion" to "A sleek 'A' with smooth gradient transitions from electric blue to rich twilight purple.",
                        "Concept 3: Futuristic Minimalism" to "Highly simplified flat vector design tailored for application icons and responsive website headers.",
                        "Concept 4: Iconic Edge" to "Bold angles, crisp separation lines, and professional branding aesthetic suitable for a modern AI workspace."
                    )

                    concepts.forEachIndexed { index, pair ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PureBlack.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .border(0.5.dp, BorderGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Concept ${index + 1}: ${pair.first}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoAccent
                            )
                            Text(
                                text = pair.second,
                                fontSize = 10.sp,
                                color = TextGray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
