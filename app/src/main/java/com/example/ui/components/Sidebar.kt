package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun Sidebar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    isEnglishLanguage: Boolean,
    onLanguageToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(240.dp)
            .background(PureBlack)
            .padding(16.dp)
    ) {
        // App Header Logo / Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 20.dp, start = 8.dp, top = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arvion_logo),
                contentDescription = "ARVION Logo",
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "ARVION",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
                Text(
                    text = if (isEnglishLanguage) "Build Apps with AI" else "এআই ওয়েব ও অ্যাপ ইঞ্জিন",
                    fontSize = 9.sp,
                    color = IndigoAccent,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        // Navigation Items
        val navItems = listOf(
            NavigationItemData(
                enLabel = "Homepage",
                bnLabel = "হোমপেজ",
                icon = Icons.Default.Home,
                screen = Screen.Home
            ),
            NavigationItemData(
                enLabel = "My Projects",
                bnLabel = "আমার প্রজেক্টস",
                icon = Icons.Default.FolderOpen,
                screen = Screen.Projects
            ),
            NavigationItemData(
                enLabel = "Template Library",
                bnLabel = "টেমপ্লেট লাইব্রেরি",
                icon = Icons.Default.Dashboard,
                screen = Screen.Templates
            ),
            NavigationItemData(
                enLabel = "Premium Plans",
                bnLabel = "প্রিমিয়াম প্ল্যান",
                icon = Icons.Default.MonetizationOn,
                screen = Screen.Monetization
            ),
            NavigationItemData(
                enLabel = "Team Workspace",
                bnLabel = "টিম ওয়ার্কস্পেস",
                icon = Icons.Default.Groups,
                screen = Screen.TeamCollaboration
            ),
            NavigationItemData(
                enLabel = "Income Dashboard",
                bnLabel = "আয় ড্যাশবোর্ড",
                icon = Icons.Default.BarChart,
                screen = Screen.IncomeDashboard
            ),
            NavigationItemData(
                enLabel = "Templates Market",
                bnLabel = "টেমপ্লেট মার্কেট",
                icon = Icons.Default.Storefront,
                screen = Screen.Marketplace
            ),
            NavigationItemData(
                enLabel = "Engine Settings",
                bnLabel = "ইঞ্জিন সেটিংস",
                icon = Icons.Default.Settings,
                screen = Screen.Settings
            )
        )

        // Scrollable Tab List
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            navItems.forEach { item ->
                val isSelected = currentScreen == item.screen
                val label = if (isEnglishLanguage) item.enLabel else item.bnLabel
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) {
                                Brush.linearGradient(listOf(IndigoAccent.copy(alpha = 0.2f), PurpleAccent.copy(alpha = 0.2f)))
                            } else {
                                Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                            }
                        )
                        .clickable { onNavigate(item.screen) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = label,
                        tint = if (isSelected) IndigoAccent else TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) Color.White else TextGray,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Language Switcher & Footer info
        Column(modifier = Modifier.padding(top = 12.dp)) {
            // Language Selection Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurface)
                    .clickable { onLanguageToggle() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = IndigoAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEnglishLanguage) "Language: EN" else "ভাষা: বাংলা",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = if (isEnglishLanguage) "Change" else "পরিবর্তন",
                    color = PurpleAccent,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(color = BorderGray, modifier = Modifier.padding(vertical = 12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Brush.linearGradient(listOf(IndigoAccent, PurpleAccent))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "V",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isEnglishLanguage) "Live Sandbox" else "লাইভ স্যান্ডবক্স",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Gemini 2.0 Pro Enabled",
                        fontSize = 8.sp,
                        color = TextGray
                    )
                }
            }
        }
    }
}

data class NavigationItemData(
    val enLabel: String,
    val bnLabel: String,
    val icon: ImageVector,
    val screen: Screen
)
