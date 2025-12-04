package com.example.workwell.View

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Calendar(modifier: Modifier = Modifier)
{
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp).padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Calendario",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,// Texto en negrita
            color = Color(31, 65, 187, 255))

        Spacer(modifier = Modifier.height(20.dp).weight(1f))
        Footer()
    }
}