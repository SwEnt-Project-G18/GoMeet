package com.github.se.gomeet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gomeet.R

@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier
            .border(width = 8.dp, color = Color(0x80747775), shape = RoundedCornerShape(size = 18.dp))
            .padding(8.dp)
            .fillMaxSize()
            //.width(412.dp)
            //.height(892.dp)
            .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 18.dp)),
        verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
        content = {
            Text(
                text = "GoMeet",
                modifier = Modifier
                    .wrapContentSize()
                    //.fillMaxSize()
                    //.width(144.dp)
                    //.height(16.dp)
                    .padding(8.dp),
                style = TextStyle(
                    fontSize = 40.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontWeight = FontWeight(600),
                    color = Color(0xFF3C4043),
                    letterSpacing = 0.5.sp,
                )
            )

            Text(
                text = "Log in",
                modifier = Modifier
                    .wrapContentSize()
                    //.fillMaxSize()
                    //.width(144.dp)
                    //.height(16.dp)
                    .padding(8.dp),
                style = TextStyle(
                    fontSize = 36.sp,
                    lineHeight = 17.sp,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontWeight = FontWeight(500),
                    color = Color(0xFF2F6673),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.25.sp,
                )
            )
        }
    )
}

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen()
}