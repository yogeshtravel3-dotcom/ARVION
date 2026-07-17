package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.ui.theme.*

// ==========================================
// 1. MONETIZATION (SUBSCRIPTION & PAYMENTS)
// ==========================================
@Composable
fun MonetizationScreen(
    isEnglishLanguage: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedPlan by remember { mutableStateOf<String?>(null) }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("Stripe") } // Stripe, PayPal, Razorpay, Crypto
    var activeApiKey by remember { mutableStateOf<String?>(null) }
    var isGeneratingKey by remember { mutableStateOf(false) }
    
    var showHireDialog by remember { mutableStateOf<ExpertDeveloper?>(null) }
    var isHiringSuccess by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()

    if (showCheckoutDialog && selectedPlan != null) {
        CheckoutDialog(
            planName = selectedPlan!!,
            price = when(selectedPlan) {
                "Pro" -> "$15.00/mo"
                "Team" -> "$45.00/mo"
                "Enterprise" -> "$99.00/mo"
                "White-Label Business" -> "$1,000.00/yr"
                "White-Label Enterprise" -> "$5,000.00/yr"
                "API Key Refill" -> "$50.00/mo"
                else -> "$0.00"
            },
            paymentMethod = selectedPaymentMethod,
            onPaymentMethodChange = { selectedPaymentMethod = it },
            isEnglish = isEnglishLanguage,
            onDismiss = { showCheckoutDialog = false },
            onConfirm = {
                coroutineScope.launch {
                    delay(1200)
                    showCheckoutDialog = false
                    selectedPlan = null
                }
            }
        )
    }

    if (showHireDialog != null) {
        Dialog(onDismissRequest = { showHireDialog = null; isHiringSuccess = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isEnglishLanguage) "Hire an Expert Partner" else "এক্সপার্ট পার্টনার নিয়োগ করুন",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(IndigoAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(showHireDialog!!.avatar, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(showHireDialog!!.name, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(showHireDialog!!.role, color = TextGray, fontSize = 11.sp)
                    Text("${showHireDialog!!.rate} / hr", color = PurpleAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (isHiringSuccess) {
                        Text(
                            text = if (isEnglishLanguage) "✓ Proposal Sent Successfully!" else "✓ প্রস্তাব সফলভাবে পাঠানো হয়েছে!",
                            color = GlowGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = if (isEnglishLanguage) 
                                "ARVION escrow ensures developer receives funds only after your milestone approval. Platform takes 10% commission."
                            else 
                                "আরভিয়ন এসক্রো নিশ্চিত করে যে কাজ দেখে আপনার অনুমোদনের পরেই কেবল ডেভেলপার পেমেন্ট পাবেন। প্লাটফর্ম কমিশন ১০%।",
                            color = TextGray,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isHiringSuccess = true
                                    delay(1500)
                                    showHireDialog = null
                                    isHiringSuccess = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isEnglishLanguage) "Send Contract Request" else "চুক্তিপত্র অনুরোধ পাঠান", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(PureBlack).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Section
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Text(
                    text = if (isEnglishLanguage) "Premium Services & Upgrades" else "প্রিমিয়াম সার্ভিস ও আপগ্রেড",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
                Text(
                    text = if (isEnglishLanguage) 
                        "Unlock infinite computing powers, API interfaces, white-label licenses, and expert developers."
                    else 
                        "সীমাহীন কম্পিউটিং ক্ষমতা, এপিআই ইন্টারফেস, হোয়াইট-লেবেল লাইসেন্স এবং দক্ষ ডেভেলপার আনলক করুন।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
        }

        // Subscriptions Section
        item {
            Text(
                text = if (isEnglishLanguage) "Subscription Plans" else "সাবস্ক্রিপশন প্ল্যানসমূহ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                listOf(
                    SubPlan("Free", "$0/mo", if (isEnglishLanguage) "5 generations/day\nBasic templates\nCommunity assistance" else "দৈনিক ৫টি জেনারেশন\nবেসিক টেমপ্লেট\nকমিউনিটি সহায়তা", false, false),
                    SubPlan("Pro", "$15/mo", if (isEnglishLanguage) "Unlimited generations\nPremium templates\nPriority server access\n1 Click PWA Publish" else "সীমাহীন জেনারেশন\nপ্রিমিয়াম টেমপ্লেট\nঅগ্রাধিকার সার্ভার\n১ ক্লিক পিডব্লিউএ পাবলিশ", true, true),
                    SubPlan("Team", "$45/mo", if (isEnglishLanguage) "5 team users\nReal-time Workspace\nPrivate fine-tuned models\nDedicated deployments" else "৫ জন টিম ইউজার\nরিয়েল-টাইম ওয়ার্কস্পেস\nব্যক্তিগত ফাইন-টিউনড মডেল\nডেডিকেটেড ডেপ্লয়মেন্ট", false, true),
                    SubPlan("Enterprise", "$99/mo", if (isEnglishLanguage) "On-prem custom models\nDedicated server cores\n24/7 SLA Support\nStripe/PayPal Auto-config" else "অন-প্রেম কাস্টম মডেল\nডেডিকেটেড সার্ভার কোর\n২৪/৭ এসএলএ সাপোর্ট\nস্ট্রাইপ/পেপাল অটো-কনফিগ", false, true)
                ).forEach { plan ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, if (plan.isPopular) IndigoAccent else BorderGray),
                        modifier = Modifier.width(220.dp).height(240.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(plan.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                                    if (plan.isPopular) {
                                        Box(
                                            modifier = Modifier.background(IndigoAccent, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("Popular", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text(plan.price, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = PurpleAccent, modifier = Modifier.padding(vertical = 4.dp))
                                Text(plan.features, fontSize = 10.sp, color = TextGray, lineHeight = 14.sp)
                            }
                            Button(
                                onClick = {
                                    if (plan.isPaid) {
                                        selectedPlan = plan.name
                                        showCheckoutDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (plan.isPopular) IndigoAccent else DarkSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (!plan.isPaid) {
                                        if (isEnglishLanguage) "Current Plan" else "বর্তমান প্ল্যান"
                                    } else {
                                        if (isEnglishLanguage) "Upgrade" else "আপগ্রেড করুন"
                                    },
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // White Label licensing Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, PurpleAccent.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = "WhiteLabel", tint = PurpleAccent, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isEnglishLanguage) "White-Label Reseller License" else "হোয়াইট-লেবেল রিসেলার লাইসেন্স",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isEnglishLanguage) 
                            "Rebrand ARVION web builder engine under your own domain, logos, custom pricing tiers, and claim 100% platform profit. Full access to raw Compose + NextJS engines."
                        else 
                            "আপনার নিজস্ব ডোমেইন, লোগো, কাস্টম প্রাইসিং টায়ারের অধীনে আরভিয়ন ওয়েব বিল্ডার ইঞ্জিন রিব্র্যান্ড করুন এবং ১০০% প্ল্যাটফর্ম প্রফিট আপনার কাছে রাখুন।",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                selectedPlan = "White-Label Business"
                                showCheckoutDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                            border = BorderStroke(1.dp, BorderGray),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("SME Tier ($1,000/yr)", fontSize = 10.sp, color = Color.White)
                        }
                        Button(
                            onClick = {
                                selectedPlan = "White-Label Enterprise"
                                showCheckoutDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Corp Tier ($5,000/yr)", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Custom API Keys Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (isEnglishLanguage) "ARVION Core Engine API" else "আরভিয়ন কোর ইঞ্জিন এপিআই",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isEnglishLanguage) 
                            "Deploy, edit, and query ARVION micro-services programmatically. Rates: $5 per 1,000 requests, or $50/mo unlimited."
                        else 
                            "প্রোগ্রামেটিকভাবে আরভিয়ন মাইক্রো-সার্ভিসেস ডেপ্লয় ও এডিট করুন। রেট: প্রতি ১,০০০ রিকোয়েস্টে $৫, অথবা মাসে $৫০ সীমাহীন।",
                        fontSize = 11.sp,
                        color = TextGray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (activeApiKey != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth().background(PureBlack, RoundedCornerShape(6.dp)).padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(activeApiKey!!, fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = GlowGreen)
                            Icon(
                                imageVector = Icons.Default.ContentCopy, 
                                contentDescription = "Copy", 
                                tint = IndigoAccent, 
                                modifier = Modifier.size(16.dp).clickable { /* Simulated Copy */ }
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                isGeneratingKey = true
                                coroutineScope.launch {
                                    delay(1000)
                                    activeApiKey = "af_live_key_" + System.currentTimeMillis().toString().takeLast(6) + "xX91bF"
                                    isGeneratingKey = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isGeneratingKey) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 1.5.dp)
                            } else {
                                Text(if (isEnglishLanguage) "Provision API Key ($50/mo)" else "এপিআই কি তৈরি করুন ($৫০/মাস)", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Hire expert freelance assistance section
        item {
            Text(
                text = if (isEnglishLanguage) "Hire Certified ARVION Experts" else "সার্টিফাইড আরভিয়ন এক্সপার্ট নিয়োগ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            val experts = listOf(
                ExpertDeveloper("Farhan Chowdhury", "Senior Compose Architect", "🇧🇩 Dhaka", "$45", "👨‍💻"),
                ExpertDeveloper("Jessica Miller", "NextJS Cloud Architect", "🇺🇸 Austin", "$65", "👩‍💻"),
                ExpertDeveloper("Siddharth Roy", "Database Sync Optimizer", "🇮🇳 Bangalore", "$35", "👨‍💻")
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                experts.forEach { exp ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurface)
                            .border(1.dp, BorderGray, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(PureBlack),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(exp.avatar, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(exp.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("${exp.role} • ${exp.location}", color = TextGray, fontSize = 9.sp)
                            }
                        }
                        Button(
                            onClick = { showHireDialog = exp },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("${exp.rate}/hr", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

data class SubPlan(
    val name: String,
    val price: String,
    val features: String,
    val isPopular: Boolean,
    val isPaid: Boolean
)

data class ExpertDeveloper(
    val name: String,
    val role: String,
    val location: String,
    val rate: String,
    val avatar: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutDialog(
    planName: String,
    price: String,
    paymentMethod: String,
    onPaymentMethodChange: (String) -> Unit,
    isEnglish: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var paymentProgressStep by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, IndigoAccent),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (isEnglish) "Confirm Checkout" else "চেকআউট নিশ্চিত করুন",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().background(PureBlack, RoundedCornerShape(8.dp)).padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(planName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(price, color = PurpleAccent, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(if (isEnglish) "Select Payment System:" else "পেমেন্ট গেটওয়ে নির্বাচন করুন:", color = TextGray, fontSize = 11.sp)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Payment Method options
                listOf("Stripe", "PayPal", "Razorpay", "Crypto (BTC/ETH)").forEach { method ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (paymentMethod == method) IndigoAccent.copy(alpha = 0.15f) else Color.Transparent)
                            .border(1.dp, if (paymentMethod == method) IndigoAccent else BorderGray, RoundedCornerShape(8.dp))
                            .clickable { if (!isProcessing) onPaymentMethodChange(method) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(method, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        RadioButton(
                            selected = paymentMethod == method,
                            onClick = { if (!isProcessing) onPaymentMethodChange(method) },
                            colors = RadioButtonDefaults.colors(selectedColor = IndigoAccent)
                        )
                    }
                }

                if (isProcessing) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PureBlack),
                        border = BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = IndigoAccent, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(paymentProgressStep, color = GlowGreen, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.dp, BorderGray),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing
                    ) {
                        Text(if (isEnglish) "Cancel" else "বাতিল", color = Color.White)
                    }
                    Button(
                        onClick = {
                            isProcessing = true
                            scope.launch {
                                if (paymentMethod == "Stripe") {
                                    paymentProgressStep = "POST: /api/stripe-intent..."
                                    delay(600)
                                    paymentProgressStep = "Stripe Elements token validation..."
                                } else if (paymentMethod == "Razorpay") {
                                    paymentProgressStep = "Razorpay order creation: rzp_98xFa..."
                                    delay(600)
                                    paymentProgressStep = "Initiating OTP secure checkout window..."
                                } else {
                                    paymentProgressStep = "Resolving RPC handshake tunnels..."
                                    delay(600)
                                    paymentProgressStep = "Validating secure digital signature..."
                                }
                                delay(800)
                                paymentProgressStep = "Payment successful! Updating account..."
                                delay(500)
                                onConfirm()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.5f),
                        enabled = !isProcessing
                    ) {
                        Text(if (isEnglish) "Pay Now" else "পরিশোধ করুন")
                    }
                }
            }
        }
    }
}


// ==========================================
// 2. TEAM COLLABORATION (REAL-TIME ROOMS)
// ==========================================
@Composable
fun CollaborationScreen(
    isEnglishLanguage: Boolean,
    modifier: Modifier = Modifier
) {
    var newRoomName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Editor") } // Owner, Editor, Viewer
    val activeRooms = remember { mutableStateListOf("Production Core Sync", "Alpha Bug Bash") }
    var selectedRoom by remember { mutableStateOf("Production Core Sync") }
    
    val comments = remember { 
        mutableStateListOf(
            CollaborativeComment("Neo", "line 124: Let's optimize this Compose remember state.", "10:42 AM", "Editor"),
            CollaborativeComment("Sarah Connor", "We should run the XSS scanner before deploy.", "10:44 AM", "Owner"),
            CollaborativeComment("Arpan Bengali", "আমি ডাটাবেস স্ক্রিন চেক করছি, অল ক্লিয়ার।", "10:45 AM", "Editor")
        )
    }
    var activeCommentText by remember { mutableStateOf("") }
    
    val activityLogs = remember {
        mutableStateListOf(
            "Neo joined the live workspace session.",
            "Sarah Connor added a line comment.",
            "Arpan Bengali synchronized localized assets."
        )
    }
    
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier.fillMaxSize().background(PureBlack).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title Section
        item {
            Column {
                Text(
                    text = if (isEnglishLanguage) "Real-Time Collaborative Workspace" else "রিয়েল-টাইম সহযোগী ওয়ার্কস্পেস",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
                Text(
                    text = if (isEnglishLanguage) 
                        "Collaborate Google-Docs style. Discuss, comment, and edit synchronized code in real-time."
                    else 
                        "গুগল ডক্স স্টাইলে একসাথে কাজ করুন। কোড নিয়ে আলোচনা ও রিয়েল-টাইমে এডিট করুন।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
        }

        // Room Creator row
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(if (isEnglishLanguage) "Create New Room" else "নতুন কোলাবোরেট রুম তৈরি করুন", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newRoomName,
                            onValueChange = { newRoomName = it },
                            placeholder = { Text(if (isEnglishLanguage) "e.g., Marketing Landing Page" else "রুমের নাম লিখুন...", color = TextGray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = IndigoAccent,
                                unfocusedBorderColor = BorderGray
                            ),
                            modifier = Modifier.weight(1.5f),
                            textStyle = TextStyle(fontSize = 11.sp)
                        )
                        Button(
                            onClick = {
                                if (newRoomName.isNotBlank()) {
                                    activeRooms.add(newRoomName)
                                    selectedRoom = newRoomName
                                    activityLogs.add("You created and joined Room '$newRoomName'")
                                    newRoomName = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isEnglishLanguage) "Create" else "তৈরি করুন", fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Role selectors
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isEnglishLanguage) "Your Workspace Role:" else "আপনার ওয়ার্কস্পেস রোল:", color = TextGray, fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        listOf("Owner", "Editor", "Viewer").forEach { role ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (selectedRole == role) IndigoAccent else DarkSurfaceVariant)
                                    .clickable { selectedRole = role }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(role, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Active Workspace Split view
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Rooms Column
                Column(modifier = Modifier.weight(1.2f)) {
                    Text(if (isEnglishLanguage) "Active Rooms" else "সক্রিয় রুমসমূহ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    activeRooms.forEach { room ->
                        val isCurrent = selectedRoom == room
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) IndigoAccent.copy(alpha = 0.2f) else DarkSurface)
                                .border(1.dp, if (isCurrent) IndigoAccent else BorderGray, RoundedCornerShape(8.dp))
                                .clickable { selectedRoom = room }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = "Room", tint = if (isCurrent) IndigoAccent else TextGray, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(room, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            // Active green dot
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(GlowGreen))
                        }
                    }
                }

                // Collaborators List
                Column(modifier = Modifier.weight(1f)) {
                    Text(if (isEnglishLanguage) "Collaborators (3)" else "সহযোগীবৃন্দ (৩)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    listOf(
                        CollaboratorData("Sarah Connor", "Owner", "👑"),
                        CollaboratorData("Neo", "Editor", "🕶️"),
                        CollaboratorData("Arpan Bengali", "Editor", "💻")
                    ).forEach { user ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(24.dp).clip(CircleShape).background(DarkSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(user.emoji, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(user.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(user.role, color = TextGray, fontSize = 8.sp)
                            }
                        }
                    }
                }
            }
        }

        // Live Comment Stream Chat Room
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isEnglishLanguage) "Discussion Comment Board" else "আলোচনা ও কমেন্ট বোর্ড", 
                            color = Color.White, 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 12.sp
                        )
                        Box(
                            modifier = Modifier.background(GlowGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Synced", color = GlowGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Comments list container
                    Column(
                        modifier = Modifier.fillMaxWidth().height(140.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        comments.forEach { comment ->
                            Column(
                                modifier = Modifier.fillMaxWidth().background(PureBlack, RoundedCornerShape(8.dp)).padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(comment.author, color = IndigoAccent, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier.background(DarkSurfaceVariant, RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(comment.role, fontSize = 7.sp, color = TextGray)
                                        }
                                    }
                                    Text(comment.timestamp, color = TextGray, fontSize = 8.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(comment.text, color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = activeCommentText,
                            onValueChange = { activeCommentText = it },
                            placeholder = { Text(if (isEnglishLanguage) "Post line suggestion..." else "পরামর্শ লিখুন...", color = TextGray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = IndigoAccent,
                                unfocusedBorderColor = BorderGray
                            ),
                            modifier = Modifier.weight(1.5f),
                            textStyle = TextStyle(fontSize = 10.sp)
                        )
                        IconButton(
                            onClick = {
                                if (activeCommentText.isNotBlank()) {
                                    comments.add(
                                        CollaborativeComment(
                                            author = "You",
                                            text = activeCommentText,
                                            timestamp = "Just Now",
                                            role = selectedRole
                                        )
                                    )
                                    activityLogs.add("You posted a comment in $selectedRoom.")
                                    activeCommentText = ""
                                }
                            },
                            modifier = Modifier.size(40.dp).background(IndigoAccent, CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Live Activities Log List
        item {
            Text(if (isEnglishLanguage) "Live Team Activity Feed" else "লাইভ টিম ক্রিয়াকলাপ ফিড", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    activityLogs.reversed().forEach { log ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PurpleAccent))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(log, color = TextGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

data class CollaborativeComment(
    val author: String,
    val text: String,
    val timestamp: String,
    val role: String
)

data class CollaboratorData(
    val name: String,
    val role: String,
    val emoji: String
)


// ==========================================
// 3. TEMPLATE MARKETPLACE (CREATOR COMMISSIONS)
// ==========================================
@Composable
fun MarketplaceScreen(
    isEnglishLanguage: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "E-Commerce", "Productivity", "Gaming", "Utility")
    
    var showPurchaseReceipt by remember { mutableStateOf<MarketplaceTemplate?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf("Stripe") }
    var showCheckoutCompleteConfetti by remember { mutableStateOf(false) }
    
    var sellAppName by remember { mutableStateOf("") }
    var sellAppPrice by remember { mutableStateOf("") }
    var sellAppCategory by remember { mutableStateOf("E-Commerce") }
    var isSubmittedToMarket by remember { mutableStateOf(false) }

    val initialTemplates = listOf(
        MarketplaceTemplate(1, "Retro Snake 8-Bit Game", "Retro arcade snake game complete with dynamic canvas controllers.", "Gaming", "$9.00", "PlayMaster", 4.9f, 214),
        MarketplaceTemplate(2, "Secure Crypto Web Wallet", "Full cryptographic suite client with live gas fee trackers.", "E-Commerce", "$49.00", "SolidityScribe", 4.7f, 85),
        MarketplaceTemplate(3, "Kanban Task Stream board", "Collaborative board synchronized via local SQLite/Room engine.", "Productivity", "$19.00", "AgilePro", 4.8f, 150),
        MarketplaceTemplate(4, "AI Resume Builder Engine", "Automatic PDF layouts paired with Gemini categorization.", "Utility", "$29.00", "AITrailblazer", 4.6f, 96),
        MarketplaceTemplate(5, "Hospital Appointment CRM", "Doctor slot booking widget with auto-SMS reminder models.", "Utility", "$99.00", "MedTechDev", 5.0f, 42)
    )
    val templatesList = remember { mutableStateListOf<MarketplaceTemplate>().apply { addAll(initialTemplates) } }
    
    val filteredTemplates = if (selectedCategory == "All") templatesList else templatesList.filter { it.category == selectedCategory }
    val coroutineScope = rememberCoroutineScope()

    if (showPurchaseReceipt != null) {
        Dialog(onDismissRequest = { showPurchaseReceipt = null; showCheckoutCompleteConfetti = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, IndigoAccent),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (isEnglishLanguage) "Review Marketplace Transaction" else "মার্কেটপ্লেস পেমেন্ট রিভিউ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Column(
                        modifier = Modifier.fillMaxWidth().background(PureBlack, RoundedCornerShape(8.dp)).padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Template Name:", color = TextGray, fontSize = 11.sp)
                            Text(showPurchaseReceipt!!.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Creator Account:", color = TextGray, fontSize = 11.sp)
                            Text(showPurchaseReceipt!!.creator, color = IndigoAccent, fontSize = 11.sp)
                        }
                        Divider(color = BorderGray, modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal Price:", color = TextGray, fontSize = 11.sp)
                            Text(showPurchaseReceipt!!.price, color = Color.White, fontSize = 11.sp)
                        }
                        val basePrice = showPurchaseReceipt!!.price.replace("$", "").toDoubleOrNull() ?: 0.0
                        val creatorShare = basePrice * 0.8
                        val platformFee = basePrice * 0.2
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Creator Earnings (80%):", color = TextGray, fontSize = 10.sp)
                            Text("$${String.format("%.2f", creatorShare)}", color = GlowGreen, fontSize = 10.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Platform Commission (20%):", color = TextGray, fontSize = 10.sp)
                            Text("$${String.format("%.2f", platformFee)}", color = PurpleAccent, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(if (isEnglishLanguage) "Select Payment Wallet:" else "পেমেন্ট ওয়ালেট:", color = TextGray, fontSize = 10.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Stripe", "PayPal", "Crypto").forEach { opt ->
                            val active = selectedPaymentMethod == opt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (active) IndigoAccent else PureBlack)
                                    .border(1.dp, if (active) IndigoAccent else BorderGray, RoundedCornerShape(6.dp))
                                    .clickable { selectedPaymentMethod = opt }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(opt, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    if (showCheckoutCompleteConfetti) {
                        Text(
                            text = if (isEnglishLanguage) "🎉 Payment complete! Template added to your Workspace!" else "🎉 পেমেন্ট সফল! টেমপ্লেটটি আপনার অ্যাকাউন্টে যুক্ত হয়েছে!",
                            color = GlowGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    showCheckoutCompleteConfetti = true
                                    delay(1500)
                                    showPurchaseReceipt = null
                                    showCheckoutCompleteConfetti = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isEnglishLanguage) "Complete Purchase Checkout" else "ক্রয় নিশ্চিত করুন")
                        }
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(PureBlack).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Header
        item {
            Column {
                Text(
                    text = if (isEnglishLanguage) "Developer Template Marketplace" else "ডেভেলপার টেমপ্লেট মার্কেটপ্লেস",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
                Text(
                    text = if (isEnglishLanguage) 
                        "Buy customized app modules or sell your built projects. 20% platform commission applies on all public listings."
                    else 
                        "কাস্টমাইজড অ্যাপ মডিউল ক্রয় করুন অথবা আপনার তৈরি প্রজেক্ট বিক্রি করুন। ২০% প্ল্যাটফর্ম কমিশন প্রযোজ্য।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
        }

        // Category Selection
        item {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val active = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (active) IndigoAccent else DarkSurface)
                            .border(1.dp, if (active) IndigoAccent else BorderGray, RoundedCornerShape(20.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isEnglishLanguage) cat else when(cat) {
                                "All" -> "সব"
                                "E-Commerce" -> "ই-কমার্স"
                                "Productivity" -> "প্রোডাক্টিভিটি"
                                "Gaming" -> "গেমিং"
                                "Utility" -> "ইউটিলিটি"
                                else -> cat
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // List Grid of Templates
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredTemplates.forEach { tpl ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, BorderGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.background(PurpleAccent.copy(alpha = 0.15f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(tpl.category, color = PurpleAccent, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                                Text("⭐ ${tpl.rating} (${tpl.downloads} sold)", color = TextGray, fontSize = 9.sp)
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(tpl.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(tpl.desc, color = TextGray, fontSize = 10.sp, maxLines = 2)
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("by ${tpl.creator}", color = IndigoAccent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                Button(
                                    onClick = { showPurchaseReceipt = tpl },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("Buy ${tpl.price}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sell Your App Form Section
        item {
            Divider(color = BorderGray, modifier = Modifier.padding(vertical = 12.dp))
            Text(
                text = if (isEnglishLanguage) "Sell Your Current Project" else "আপনার নিজস্ব প্রজেক্ট বিক্রি করুন",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isSubmittedToMarket) {
                        Text(
                            text = if (isEnglishLanguage) 
                                "✓ App Listing Request submitted successfully! Verification takes 4-6 hours. Commission is 20% on sale."
                            else 
                                "✓ অ্যাপ লিস্টিং আবেদন জমা হয়েছে! ৪-৬ ঘণ্টার মধ্যে যাচাই সম্পন্ন হবে। প্ল্যাটফর্ম কমিশন ২০%।",
                            color = GlowGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        )
                    } else {
                        Text(
                            text = if (isEnglishLanguage) 
                                "Easily list any of your saved ARVION templates. Set a custom price and start earning real passive income."
                            else 
                                "আপনার সংরক্ষিত যেকোনো প্রজেক্ট সহজে তালিকাভুক্ত করুন। নিজের মতো দাম নির্ধারণ করে আয় শুরু করুন।",
                            color = TextGray,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = sellAppName,
                            onValueChange = { sellAppName = it },
                            placeholder = { Text("App Name (e.g. Pixel Physics Engine)", fontSize = 10.sp, color = TextGray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = IndigoAccent,
                                unfocusedBorderColor = BorderGray
                            ),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            textStyle = TextStyle(fontSize = 11.sp)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = sellAppPrice,
                                onValueChange = { sellAppPrice = it },
                                placeholder = { Text("Price (USD, e.g. 19.00)", fontSize = 10.sp, color = TextGray) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = IndigoAccent,
                                    unfocusedBorderColor = BorderGray
                                ),
                                modifier = Modifier.weight(1f),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                            
                            // Category Select
                            Box(modifier = Modifier.weight(1.2f).height(50.dp).background(PureBlack, RoundedCornerShape(8.dp)).clickable {
                                sellAppCategory = if (sellAppCategory == "E-Commerce") "Productivity" else if (sellAppCategory == "Productivity") "Gaming" else "E-Commerce"
                            }, contentAlignment = Alignment.Center) {
                                Text("Cat: $sellAppCategory", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (sellAppName.isNotBlank() && sellAppPrice.isNotBlank()) {
                                    coroutineScope.launch {
                                        templatesList.add(
                                            MarketplaceTemplate(
                                                id = templatesList.size + 1,
                                                title = sellAppName,
                                                desc = "Custom compiled template uploaded directly from developer workspace.",
                                                category = sellAppCategory,
                                                price = "$${sellAppPrice.replace("$", "")}",
                                                creator = "You (Verified)",
                                                rating = 5.0f,
                                                downloads = 0
                                            )
                                        )
                                        isSubmittedToMarket = true
                                        delay(2500)
                                        sellAppName = ""
                                        sellAppPrice = ""
                                        isSubmittedToMarket = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isEnglishLanguage) "List on Template Market" else "টেমপ্লেট মার্কেটে লিস্টিং করুন")
                        }
                    }
                }
            }
        }
    }
}

data class MarketplaceTemplate(
    val id: Int,
    val title: String,
    val desc: String,
    val category: String,
    val price: String,
    val creator: String,
    val rating: Float,
    val downloads: Int
)


// ==========================================
// 4. INCOME DASHBOARD (REAL-TIME PLATFORM MRR)
// ==========================================
@Composable
fun IncomeDashboardScreen(
    isEnglishLanguage: Boolean,
    modifier: Modifier = Modifier
) {
    var totalSales by remember { mutableStateOf(14850.0) }
    var activeSubscribersCount by remember { mutableStateOf(264) }
    var mrrValue by remember { mutableStateOf(9420.0) }
    var platformCommissionsEarnings by remember { mutableStateOf(1970.0) }
    
    var affiliateReferralId by remember { mutableStateOf<String?>(null) }
    var isGeneratingAffLink by remember { mutableStateOf(false) }
    
    // Live Scrolling Sales ticker state
    val transactionFeed = remember {
        mutableStateListOf(
            "Transaction: Pro Upgrade ($15.00/mo) via Stripe. 10s ago",
            "Template Sold: 'SaaS Dashboard' ($49.00). Commission: $9.80. 1m ago",
            "Transaction: Team Upgrade ($45.00/mo) via Razorpay. 4m ago"
        )
    }

    val coroutineScope = rememberCoroutineScope()

    // Dynamically simulate incoming traffic/revenue ticking over time
    LaunchedEffect(Unit) {
        while (true) {
            delay(12000) // tick every 12 seconds
            totalSales += 15.0
            mrrValue += 5.0
            platformCommissionsEarnings += 3.0
            transactionFeed.add(0, "Transaction: Plan upgraded ($15.00/mo) from live node. Just Now")
            if (transactionFeed.size > 5) {
                transactionFeed.removeAt(transactionFeed.size - 1)
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(PureBlack).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Section
        item {
            Column {
                Text(
                    text = if (isEnglishLanguage) "ARVION Real-Time Platform Revenue" else "আরভিয়ন রিয়েল-টাইম রাজস্ব ড্যাশবোর্ড",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
                Text(
                    text = if (isEnglishLanguage) 
                        "Monitor recurring subscriptions, API logs billing, marketplace royalties, and affiliate pay-outs."
                    else 
                        "রিকারিং সাবস্ক্রিপশন, এপিআই লগ বিলিং, মার্কেটপ্লেস রয়্যালটি এবং এফিলিয়েট পে-আউট মনিটর করুন।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
        }

        // Numerical Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricBox(
                        title = if (isEnglishLanguage) "Platform Sales" else "মোট বিক্রি",
                        value = "$${String.format("%,.2f", totalSales)}",
                        glowColor = IndigoAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = if (isEnglishLanguage) "Monthly Recurring Revenue (MRR)" else "মাসিক আবর্তনশীল রাজস্ব (MRR)",
                        value = "$${String.format("%,.2f", mrrValue)}",
                        glowColor = PurpleAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricBox(
                        title = if (isEnglishLanguage) "Market Commissions" else "মার্কেটপ্লেস কমিশন",
                        value = "$${String.format("%,.2f", platformCommissionsEarnings)}",
                        glowColor = GlowGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = if (isEnglishLanguage) "Paid Subscribers" else "সক্রিয় গ্রাহক",
                        value = "$activeSubscribersCount active nodes",
                        glowColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Progress toward $25,000 Milestone
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isEnglishLanguage) "MRR Goal Progress: $25,000 Milestone" else "MRR লক্ষ্য অগ্রগতি: $২৫,০০০ মাইলস্টোন", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("${String.format("%.1f", (mrrValue / 25000.0) * 100)}%", color = PurpleAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = (mrrValue / 25000.0).toFloat(),
                        color = IndigoAccent,
                        trackColor = PureBlack,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }

        // Live Transactions Feed Ticker
        item {
            Text(if (isEnglishLanguage) "Live Transaction Feed Ticker" else "লাইভ লেনদেন ফিড টিকার", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, GlowGreen.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    transactionFeed.forEach { tx ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "Up", tint = GlowGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(tx, color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // Affiliate Referral Engine Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isEnglishLanguage) "Affiliate Referral Program (30% Commission)" else "অ্যাফিলিয়েট রেফারেল প্রোগ্রাম (৩০% কমিশন)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isEnglishLanguage) 
                            "Earn a whopping 30% recurring commission on every Pro, Team, or Enterprise subscriber you refer. Paid instantly monthly via PayPal, Stripe, or Bank Transfer."
                        else 
                            "আপনার রেফার করা প্রতিটি গ্রাহকের প্রিমিয়াম আয়ের ৩০% রিকারিং কমিশন লাইফটাইম পান। প্রতি মাসে পেপাল বা ব্যাংকে পেমেন্ট করুন।",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (affiliateReferralId != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth().background(PureBlack, RoundedCornerShape(6.dp)).padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(affiliateReferralId!!, color = IndigoAccent, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                            Icon(
                                imageVector = Icons.Default.ContentCopy, 
                                contentDescription = "Copy", 
                                tint = PurpleAccent, 
                                modifier = Modifier.size(16.dp).clickable { /* Simulated Copy */ }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isEnglishLanguage) "Referral Stats: 12 Leads • 3 Subscribers • $45.00 unpaid earnings" else "রেফারেল পরিসংখ্যান: ১২ লিড • ৩ গ্রাহক • $৪৫.০০ বকেয়া আয়",
                            color = GlowGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Button(
                            onClick = {
                                isGeneratingAffLink = true
                                coroutineScope.launch {
                                    delay(1000)
                                    affiliateReferralId = "https://arvion.com/ref?partner_id=av_" + System.currentTimeMillis().toString().takeLast(5)
                                    isGeneratingAffLink = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isGeneratingAffLink) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 1.5.dp)
                            } else {
                                Text(if (isEnglishLanguage) "Activate Partner Link" else "রেফারেল লিংক তৈরি করুন", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricBox(
    title: String,
    value: String,
    glowColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, glowColor.copy(alpha = 0.3f)),
        modifier = modifier.height(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
