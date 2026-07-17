package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    val radius: Float,
    val alpha: Float
)

@Composable
fun BackgroundParticles(modifier: Modifier = Modifier) {
    val particles = remember {
        List(25) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                vx = (Random.nextFloat() - 0.5f) * 0.002f,
                vy = (Random.nextFloat() - 0.5f) * 0.002f,
                radius = Random.nextFloat() * 6f + 2f,
                alpha = Random.nextFloat() * 0.25f + 0.05f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Read progress to trigger redrawing on every tick of the animation loop
        val step = progress

        particles.forEach { p ->
            p.x += p.vx
            p.y += p.vy

            if (p.x < 0f) p.x = 1f
            if (p.x > 1f) p.x = 0f
            if (p.y < 0f) p.y = 1f
            if (p.y > 1f) p.y = 0f

            drawCircle(
                color = Color(0xFF6366F1).copy(alpha = p.alpha),
                radius = p.radius,
                center = Offset(p.x * width, p.y * height)
            )
        }
    }
}

@Composable
fun rememberAutoTypingPlaceholder(): String {
    val texts = remember {
        listOf(
            "Build a SaaS landing page for fitness coaches...",
            "Build a beautiful retro arcade shooter game...",
            "Build a task manager with task statuses and categories...",
            "Build a cryptocurrency tracking app with dark glass cards..."
        )
    }
    var currentTextIndex by remember { mutableStateOf(0) }
    var displayedText by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        while (true) {
            val targetText = texts[currentTextIndex]
            for (i in 0..targetText.length) {
                displayedText = targetText.substring(0, i) + "|"
                delay(70)
            }
            delay(2000)
            for (i in targetText.length downTo 0) {
                displayedText = targetText.substring(0, i) + "|"
                delay(30)
            }
            delay(500)
            currentTextIndex = (currentTextIndex + 1) % texts.size
        }
    }
    
    return displayedText
}

@Composable
fun PulseGlowBorder(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF6366F1),
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.99f,
        targetValue = 1.01f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = color.copy(alpha = alpha),
                spotColor = color.copy(alpha = alpha)
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun GradientSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinner_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .size(size)
            .rotate(rotation)
            .background(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF6366F1).copy(alpha = 0.2f),
                        Color(0xFF6366F1),
                        Color(0xFF8B5CF6),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
            .padding(4.dp)
            .background(Color(0xFF0A0A0A), CircleShape)
    )
}

data class Confetti(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float,
    var alpha: Float = 1f
)

@Composable
fun ConfettiEffect(
    trigger: Boolean,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val confettiList = remember {
        List(60) {
            Confetti(
                x = 0.5f,
                y = 0.2f,
                vx = (Random.nextFloat() - 0.5f) * 0.08f,
                vy = -Random.nextFloat() * 0.08f - 0.03f,
                color = listOf(
                    Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFF10B981),
                    Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFFEC4899)
                ).random(),
                size = Random.nextFloat() * 12f + 8f
            )
        }
    }

    var active by remember { mutableStateOf(true) }

    LaunchedEffect(trigger) {
        delay(3500)
        active = false
        onComplete()
    }

    if (active) {
        val infiniteTransition = rememberInfiniteTransition(label = "confetti")
        val tick by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(16, easing = LinearEasing)
            ),
            label = "tick"
        )

        Canvas(modifier = modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Use tick value to trigger continuous redraw pass
            val t = tick

            confettiList.forEach { c ->
                c.x += c.vx
                c.y += c.vy
                c.vy += 0.002f // gravity
                c.alpha = (c.alpha - 0.006f).coerceIn(0f, 1f)

                drawRect(
                    color = c.color.copy(alpha = c.alpha),
                    topLeft = Offset(c.x * width, c.y * height),
                    size = androidx.compose.ui.geometry.Size(c.size, c.size)
                )
            }
        }
    }
}

@Composable
fun ProgressStepper(
    currentStepText: String,
    modifier: Modifier = Modifier
) {
    val steps = remember {
        listOf(
            "Planning" to "Establishing secure sandbox parameters...",
            "Generating" to "Streaming Gemini 2.0 component blueprints...",
            "Optimizing" to "Checking linter diagnostics & XSS patterns...",
            "Ready! 🎉" to "Launching production live-preview canvas..."
        )
    }
    
    var activeStepIndex by remember { mutableStateOf(0) }
    
    LaunchedEffect(currentStepText) {
        activeStepIndex = 0
        delay(1200)
        activeStepIndex = 1
        delay(2200)
        delay(1500)
        activeStepIndex = 2
        delay(1800)
        activeStepIndex = 3
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        steps.forEachIndexed { index, (title, subtitle) ->
            val isCompleted = index < activeStepIndex
            val isActive = index == activeStepIndex
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            color = when {
                                isCompleted -> Color(0xFF10B981)
                                isActive -> Color(0xFF6366F1)
                                else -> Color(0xFF1F1F1F)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        androidx.compose.material3.Text(
                            text = "✓",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    } else {
                        androidx.compose.material3.Text(
                            text = (index + 1).toString(),
                            color = if (isActive) Color.White else Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    androidx.compose.material3.Text(
                        text = title,
                        color = when {
                            isCompleted || isActive -> Color.White
                            else -> Color(0xFF94A3B8)
                        },
                        fontSize = 13.sp,
                        fontWeight = if (isActive) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                    )
                    androidx.compose.material3.Text(
                        text = if (isActive) currentStepText.ifEmpty { subtitle } else subtitle,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceFrameWrapper(
    deviceType: String,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070707))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (deviceType) {
            "mobile" -> {
                androidx.compose.material3.Card(
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color(0xFF0F0F11)),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier
                        .width(310.dp)
                        .height(560.dp)
                        .border(3.dp, Color(0xFF2E3039), RoundedCornerShape(32.dp))
                        .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = Color(0xFF6366F1).copy(alpha = 0.4f))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Notch top camera bar
                        Box(
                            modifier = Modifier
                                .width(110.dp)
                                .height(22.dp)
                                .background(Color.Black, RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                                .align(Alignment.TopCenter)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF222222), CircleShape)
                                    .align(Alignment.Center)
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 22.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(Color.Black)
                        ) {
                            content()
                        }
                    }
                }
            }
            "tablet" -> {
                androidx.compose.material3.Card(
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color(0xFF0F0F11)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .width(460.dp)
                        .height(600.dp)
                        .border(4.dp, Color(0xFF2E3039), RoundedCornerShape(24.dp))
                        .shadow(20.dp, RoundedCornerShape(24.dp))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Tiny lens indicator
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color.Black, CircleShape)
                                .align(Alignment.TopCenter)
                                .padding(top = 8.dp)
                        )
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black)
                        ) {
                            content()
                        }
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, Color(0xFF222228), RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    content()
                }
            }
        }
    }
}

