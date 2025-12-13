package com.example.workwell.View

import Habit
import HabitSection
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.workwell.ViewModel.HabitsViewModel
import com.example.workwell.ui.theme.AzulNav

object HabitRoutes {
    const val LIST = "habit_list"
    const val DETAIL_LEVEL_1 = "habit_detail/{habitId}"
    const val DETAIL_LEVEL_2 = "section_detail/{habitId}/{sectionTitle}"
}

@Composable
fun HabitContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HabitsViewModel = viewModel()
) {
    val uiState by viewModel.state.observeAsState(initial = HabitsViewModel.HabitsUiState.Loading)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
    ) {
        Text(
            text = "Hábitos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, top = 24.dp)
        )

        when (uiState) {
            is HabitsViewModel.HabitsUiState.Loading ->
                Text("Cargando hábitos...", modifier = Modifier.padding(top = 32.dp))

            is HabitsViewModel.HabitsUiState.Error ->
                Text((uiState as HabitsViewModel.HabitsUiState.Error).message, color = Color.Red, modifier = Modifier.padding(top = 32.dp))

            is HabitsViewModel.HabitsUiState.Success ->
                HabitsList(
                    habits = (uiState as HabitsViewModel.HabitsUiState.Success).habits,
                    navController = navController
                )
        }

        Spacer(modifier = Modifier.weight(1f))
        Footer()
    }
}

@Composable
fun HabitsList(habits: List<Habit>, navController: NavHostController) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(habits) { habit ->
            HabitCard(
                habit = habit,
                onClick = {
                    navController.navigate(HabitRoutes.DETAIL_LEVEL_1.replace("{habitId}", habit.id))
                }
            )
        }
    }
}

@Composable
fun HabitCard(habit: Habit, onClick: () -> Unit) {
    val cardColor = if (habit.title.contains("Sueño")) Color(0xFF1F41BB) else Color(0xFF4C58FF)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Placeholder para la imagen de la izquierda
                /*Image(
                    painter = painterResource(id = R.drawable.sleep_image_placeholder), // Reemplaza con tu R.drawable
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )*/

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = habit.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = habit.subtitle ?: "",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }

            Column(
                modifier = Modifier.width(IntrinsicSize.Min),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(110.dp))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .clickable(onClick = onClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ver detalles",
                        tint = cardColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HabitDetailScreen(
    habit: Habit,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = habit.subtitle ?: "Detalle del Hábito",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
            )
        }

        item {
            Text(
                text = parseMarkdownForHeadingsAndLists(habit.description),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        items(habit.sections) { section ->
            DetailSectionCard(
                section = section,
                onClick = {
                    navController.navigate(
                        HabitRoutes.DETAIL_LEVEL_2
                            .replace("{habitId}", habit.id)
                            .replace("{sectionTitle}", section.title)
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Footer()
    }
}


@Composable
fun DetailSectionCard(section: HabitSection, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F4FF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = section.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1F41BB)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = parseMarkdownForHeadingsAndLists(section.description),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3
            )
        }
    }
}

@Composable
fun SectionDetailScreen(section: HabitSection) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
    ) {
        Text(
            text = section.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
            color = AzulNav
        )

        Text(
            text = parseMarkdownForHeadingsAndLists(section.description),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
        Footer()
    }
}


fun parseMarkdownForHeadingsAndLists(source: String): AnnotatedString {
    return buildAnnotatedString {
        val lines = source.split('\n')

        val headingStyle = SpanStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AzulNav
        )

        val bodyStyle = SpanStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )

        lines.forEach { line ->
            val trimmedLine = line.trimStart()

            when {
                trimmedLine.startsWith("##") -> {
                    withStyle(style = headingStyle) {
                        append("\n" + trimmedLine.substringAfter("##").trim() + "\n\n")
                    }
                }

                trimmedLine.startsWith("*") -> {
                    withStyle(style = bodyStyle) {
                        val listItemText = trimmedLine.substringAfter("*").trim()
                        append("  • $listItemText\n")
                    }
                }

                else -> {
                    withStyle(style = bodyStyle) {
                        append(line + "\n")
                    }
                }
            }
        }
    }
}
