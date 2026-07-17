package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun Chat(
    onSendMessage: (String) -> Unit,
    isGenerating: Boolean,
    isEnglishLanguage: Boolean = true,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    
    // Advanced AI Control state
    var showAdvancedSettings by remember { mutableStateOf(false) }
    var selectedModel by remember { mutableStateOf("Gemini 3.5 Pro") }
    var temperature by remember { mutableStateOf(0.7f) }
    var systemInstructions by remember { mutableStateOf("You are an expert full-stack developer producing elegant Material 3 Android layout designs and secure HTML apps.") }
    var isThinkingModeActive by remember { mutableStateOf(true) }
    
    // Chat Branching state
    var activeBranch by remember { mutableStateOf("Branch A") }
    val branchAMessages = remember { mutableStateListOf<Pair<String, Boolean>>() }
    val branchBMessages = remember { mutableStateListOf<Pair<String, Boolean>>() }
    
    // Export Code dialog state
    var showExportDialog by remember { mutableStateOf(false) }
    var exportType by remember { mutableStateOf("Python") } // Python, JS, REST
    
    // Active history reference based on branching
    val chatHistory = if (activeBranch == "Branch A") branchAMessages else branchBMessages

    fun send() {
        if (textInput.isBlank() || isGenerating) return
        val msg = textInput.trim()
        chatHistory.add(Pair(msg, true))
        onSendMessage(msg)
        
        // Simulating Thinking logs and AI reply
        if (isThinkingModeActive) {
            val thinkingSteps = if (activeBranch == "Branch A") {
                "**Thinking step-by-step (Branch A):**\n" +
                "1. Read current layout specs and user prompt: \"$msg\".\n" +
                "2. Parse current DOM tree and locate key UI node nodes.\n" +
                "3. Injecting high-fidelity components to optimize rendering.\n" +
                "4. Compile testing buffers."
            } else {
                "**Thinking step-by-step (Branch B):**\n" +
                "1. Analyze alternate prompt branching path for: \"$msg\".\n" +
                "2. Draft modern asymmetric color schemes with elevated shadows.\n" +
                "3. Compile structural AST blocks."
            }
            chatHistory.add(Pair(thinkingSteps, false))
        }
        
        textInput = ""
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(PureBlack)
            .padding(12.dp)
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI Assistant",
                    tint = IndigoAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ARVION Chat Engine",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
            }
            
            // Advanced control toggle
            IconButton(
                onClick = { showAdvancedSettings = !showAdvancedSettings },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (showAdvancedSettings) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle Settings",
                    tint = IndigoAccent
                )
            }
        }

        // Advanced AI Controls panel
        AnimatedVisibility(visible = showAdvancedSettings) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Advanced AI Parameters",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndigoAccent
                    )
                    
                    // Model Dropdown Selection Simulation
                    Column {
                        Text("Model variant", fontSize = 9.sp, color = TextGray)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(PureBlack)
                                .clickable {
                                    // Cycle models on click
                                    selectedModel = when (selectedModel) {
                                        "Gemini 3.5 Pro" -> "Gemini 3.5 Flash"
                                        "Gemini 3.5 Flash" -> "Imagen 3 (Visuals)"
                                        "Imagen 3 (Visuals)" -> "Veo (Animation)"
                                        else -> "Gemini 3.5 Pro"
                                    }
                                }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedModel, fontSize = 11.sp, color = Color.White)
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = TextGray, modifier = Modifier.size(14.dp))
                        }
                    }

                    // Temperature Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Temperature", fontSize = 9.sp, color = TextGray)
                            Text(String.format("%.2f", temperature), fontSize = 9.sp, color = IndigoAccent)
                        }
                        Slider(
                            value = temperature,
                            onValueChange = { temperature = it },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = IndigoAccent,
                                activeTrackColor = IndigoAccent,
                                inactiveTrackColor = BorderGray
                            )
                        )
                    }

                    // System Instructions text input
                    Column {
                        Text("System Instructions Persona", fontSize = 9.sp, color = TextGray)
                        OutlinedTextField(
                            value = systemInstructions,
                            onValueChange = { systemInstructions = it },
                            textStyle = TextStyle(fontSize = 10.sp, color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = PureBlack,
                                unfocusedContainerColor = PureBlack,
                                focusedBorderColor = IndigoAccent,
                                unfocusedBorderColor = BorderGray
                            ),
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Thinking Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Thinking mode", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Exposes step-by-step reasoning tree logs", fontSize = 9.sp, color = TextGray)
                        }
                        Switch(
                            checked = isThinkingModeActive,
                            onCheckedChange = { isThinkingModeActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = IndigoAccent,
                                checkedTrackColor = IndigoAccent.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }

        // Branching Tabs row (Branch A vs Branch B comparison)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .background(DarkSurface, RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            listOf("Branch A", "Branch B").forEach { branch ->
                val active = activeBranch == branch
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (active) IndigoAccent else Color.Transparent)
                        .clickable { activeBranch = branch }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (branch == "Branch A") "Branch A (Production)" else "Branch B (Experimental)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) Color.White else TextGray
                    )
                }
            }
        }

        Divider(color = BorderGray, thickness = 1.dp)

        // Chat History List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (chatHistory.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Describe modifications for $activeBranch. E.g., 'Make app header neon blue and add a custom profile page.'",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                items(chatHistory) { (msg, isUser) ->
                    val isLog = msg.startsWith("**Thinking")
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUser) DarkSurfaceVariant else if (isLog) PureBlack else DarkSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = if (isLog) BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.3f)) else null,
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (isUser) "You" else if (isLog) "AI Thinking Log" else "ARVION AI",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUser) IndigoAccent else if (isLog) TextGray else PurpleAccent,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = msg,
                                    fontSize = 11.sp,
                                    color = if (isLog) TextGray else Color.White,
                                    fontFamily = if (isLog) FontFamily.Monospace else FontFamily.Default,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Export Code & Prompt utilities
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    exportType = "Python"
                    showExportDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Icon(Icons.Default.Code, null, modifier = Modifier.size(10.dp), tint = TextGray)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Python SDK", fontSize = 10.sp, color = TextGray)
            }

            Button(
                onClick = {
                    exportType = "JavaScript"
                    showExportDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Icon(Icons.Default.Code, null, modifier = Modifier.size(10.dp), tint = TextGray)
                Spacer(modifier = Modifier.width(4.dp))
                Text("NodeJS", fontSize = 10.sp, color = TextGray)
            }

            Button(
                onClick = {
                    exportType = "REST API"
                    showExportDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(10.dp), tint = TextGray)
                Spacer(modifier = Modifier.width(4.dp))
                Text("REST cURL", fontSize = 10.sp, color = TextGray)
            }
        }

        // Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text(if (isEnglishLanguage) "Command AI modification..." else "কোড পরিবর্তনের নির্দেশ দিন...", fontSize = 12.sp, color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedBorderColor = IndigoAccent,
                    unfocusedBorderColor = BorderGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f),
                maxLines = 3,
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { send() },
                enabled = textInput.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (textInput.isNotBlank() && !isGenerating) {
                            Brush.linearGradient(listOf(IndigoAccent, PurpleAccent))
                        } else {
                            Brush.linearGradient(listOf(BorderGray, BorderGray))
                        }
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (textInput.isNotBlank() && !isGenerating) Color.White else TextGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }

    // Export SDK Code Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text(
                    text = "Export ARVION AI Pipeline ($exportType)",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            },
            text = {
                val codeSnippet = when (exportType) {
                    "Python" -> "import google.generativeai as genai\n\n" +
                            "genai.configure(api_key=\"YOUR_ARVION_KEY\")\n" +
                            "model = genai.GenerativeModel(\n" +
                            "    model_name=\"gemini-2.0-flash\",\n" +
                            "    system_instruction=\"$systemInstructions\"\n" +
                            ")\n\n" +
                            "response = model.generate_content(\"Build responsive app scaffolding\")\n" +
                            "print(response.text)"
                    "JavaScript" -> "import { GoogleGenAI } from '@google/genai';\n\n" +
                            "const ai = new GoogleGenAI({ apiKey: 'YOUR_KEY' });\n" +
                            "const response = await ai.models.generateContent({\n" +
                            "  model: 'gemini-2.0-flash',\n" +
                            "  contents: 'Build responsive app scaffolding',\n" +
                            "  config: { systemInstruction: \"$systemInstructions\" }\n" +
                            "});\n" +
                            "console.log(response.text);"
                    else -> "curl -X POST \"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=YOUR_KEY\" \\\n" +
                            "  -H 'Content-Type: application/json' \\\n" +
                            "  -d '{\n" +
                            "    \"contents\": [{\"parts\":[{\"text\": \"Build responsive app scaffolding\"}]}],\n" +
                            "    \"systemInstruction\": {\"parts\": [{\"text\": \"$systemInstructions\"}]},\n" +
                            "    \"generationConfig\": {\"temperature\": $temperature}\n" +
                            "  }'"
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Add this dynamic AI code block into your native production servers to securely execute generated tasks:",
                        fontSize = 11.sp,
                        color = TextGray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PureBlack),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = codeSnippet,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = GlowGreen,
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent)
                ) {
                    Text("Copy snippet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close", color = TextGray)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

