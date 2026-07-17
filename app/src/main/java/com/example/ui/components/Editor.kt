package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderGray
import com.example.ui.theme.PureBlack
import com.example.ui.theme.TextGray

@Composable
fun Editor(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    // Split code into lines for displaying line numbers
    val lines = remember(code) { code.lines() }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
    ) {
        // Line Numbers column
        Column(
            modifier = Modifier
                .width(42.dp)
                .fillMaxHeight()
                .background(PureBlack)
                .verticalScroll(scrollState)
                .padding(vertical = 12.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.Top
        ) {
            lines.indices.forEach { index ->
                Text(
                    text = "${index + 1}",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextGray.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.height(18.dp),
                    maxLines = 1
                )
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(BorderGray)
        )

        // Editor TextArea
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(vertical = 12.dp, horizontal = 12.dp)
        ) {
            BasicTextField(
                value = code,
                onValueChange = onCodeChange,
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (code.isEmpty()) {
                        Text(
                            text = "<!-- Code will appear here -->",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = TextGray.copy(alpha = 0.4f)
                            )
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}
