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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
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
import com.example.workwell.ViewModel.FirebaseHabitsRepository
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

    ScreenWithFooter {
        val uiState by viewModel.state.observeAsState(initial = HabitsViewModel.HabitsUiState.Loading)

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Consejos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
                color = AzulNav
            )

            when (uiState) {
                is HabitsViewModel.HabitsUiState.Loading ->
                    Text("Cargando hábitos...", modifier = Modifier.padding(top = 32.dp))

                is HabitsViewModel.HabitsUiState.Error ->
                    Text((uiState as HabitsViewModel.HabitsUiState.Error).message, color = Color.Red, modifier = Modifier.padding(top = 32.dp))

                is HabitsViewModel.HabitsUiState.Success ->
                    HabitsList(
                        habits = (uiState as HabitsViewModel.HabitsUiState.Success).habits,
                        navController = navController,
                        viewModel
                    )
            }
        }
    }
}

@Composable
fun HabitsList(habits: List<Habit>, navController: NavHostController, viewModel: HabitsViewModel) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(habits) { habit ->
            HabitCard(
                habit = habit,
                onClick = {
                    navController.navigate(HabitRoutes.DETAIL_LEVEL_1.replace("{habitId}", habit.id))
                },
                viewModel
            )
        }
    }
}

@Composable
fun HabitCard(habit: Habit, onClick: () -> Unit, viewModel: HabitsViewModel) {
    val isDarkCard = habit.title.contains("Sueño")
    val cardColor = if (isDarkCard) Color(0xFF1F41BB) else Color(0xFFE7EDFD)

    val imageBitmap = remember(habit.id) {
        viewModel.decodeBase64ToBitmap(habit.image)
    }

    val titleColor = if (isDarkCard) Color.White else Color(0xFF1F41BB)
    val subtitleColor = if (isDarkCard) Color.White.copy(alpha = 0.8f) else Color(0xFF49454F)
    val arrowIconTint = if (isDarkCard) Color(0xFF1F41BB) else Color.White
    val arrowBgColor = if (isDarkCard) Color.White else Color(0xFF1F41BB)
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(130.dp)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = habit.title,
                    color = titleColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = habit.subtitle ?: "",
                    color = subtitleColor,
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
                        .background(arrowBgColor)
                        .clickable(onClick = onClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = "Ver detalles",
                        tint = arrowIconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habit: Habit,
    navController: NavHostController,
    viewModel: HabitsViewModel
) {
    val imageBitmap = remember(habit.id) {
        viewModel.decodeBase64ToBitmap(habit.image)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(habit.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() })
                    {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text(
                    text = habit.subtitle ?: "Detalle del Hábito",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )
            }

            item {
                Text(
                    text = parseMarkdownForHeadingsAndLists(habit.description, AzulNav),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.1f))
                        .padding(bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                }
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
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE7EDFD))
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
                text = parseMarkdownForHeadingsAndLists(section.description, AzulNav),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionDetailScreen(section: HabitSection, navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(section.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() })
                    {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = AzulNav
                    )
                }
                item {
                    Text(
                        text = parseMarkdownForHeadingsAndLists(section.description, AzulNav),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }
        }
    }
}


fun parseMarkdownForHeadingsAndLists(source: String, AzulNav: Color): AnnotatedString {
    return buildAnnotatedString {
        val lines = source.split('\n')

        val headingStyle = SpanStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AzulNav
        )

        val bodyStyle = SpanStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )

        val boldStyle = bodyStyle.copy(
            fontWeight = FontWeight.ExtraBold
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
                    append("  • ")

                    applyBoldStyleToLine(
                        line = trimmedLine.substringAfter("*").trim(),
                        defaultStyle = bodyStyle,
                        boldStyle = boldStyle
                    )
                    append("\n")
                }

                else -> {
                    applyBoldStyleToLine(
                        line = line.trim(),
                        defaultStyle = bodyStyle,
                        boldStyle = boldStyle
                    )
                    if (line.isNotEmpty()) {
                        append("\n")
                    }
                }
            }
        }
    }
}

fun AnnotatedString.Builder.applyBoldStyleToLine(
    line: String,
    defaultStyle: SpanStyle,
    boldStyle: SpanStyle
) {
    val regex = "\\*\\*(.*?)\\*\\*".toRegex()

    var currentIndex = 0

    regex.findAll(line).forEach { matchResult ->
        val boldText = matchResult.groupValues[1]
        val startIndex = matchResult.range.first
        val endIndex = matchResult.range.last + 1

        if (startIndex > currentIndex) {
            withStyle(style = defaultStyle) {
                append(line.substring(currentIndex, startIndex))
            }
        }

        withStyle(style = boldStyle) {
            append(boldText)
        }

        currentIndex = endIndex
    }

    if (currentIndex < line.length) {
        withStyle(style = defaultStyle) {
            append(line.substring(currentIndex))
        }
    }
}