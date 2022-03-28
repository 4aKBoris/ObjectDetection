package com.mpei.tensorflow.ui.bottombar

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ModelTraining
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mpei.tensorflow.navigation.Screen
import com.mpei.tensorflow.ui.theme.Green40
import com.mpei.tensorflow.ui.theme.Purple40

@Composable
fun BottomBar(
    tabPage: Screen,
    backgroundColor: Color,
    onTabSelected: (screen: Screen) -> Unit
) {
    TabRow(
        selectedTabIndex = tabPage.ordinal,
        indicator = { tabPositions ->
            TabIndicator(tabPositions, tabPage)
        },
        containerColor = backgroundColor,
        contentColor = Color.Black
    ) {
        Tab(
            icon = Icons.Default.Photo,
            title = "Фото",
            onClick = { onTabSelected(Screen.Photo) }
        )
        Tab(
            icon = Icons.Default.ModelTraining,
            title = "Модель",
            onClick = { onTabSelected(Screen.Model) }
        )
    }
}

@Composable
private fun Tab(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title)
    }
}

@Composable
private fun TabIndicator(
    tabPositions: List<TabPosition>,
    screen: Screen
) {
    val transition = updateTransition(screen, label = "Анимация вкладки")
    val indicatorLeft by transition.animateDp(
        transitionSpec = {
            if (Screen.Photo isTransitioningTo Screen.Model) spring(stiffness = Spring.StiffnessVeryLow)
            else spring(stiffness = Spring.StiffnessMedium)
        },
        label = "Сдвиг влево"
    ) { page ->
        tabPositions[page.ordinal].left
    }
    val indicatorRight by transition.animateDp(
        transitionSpec = {
            if (Screen.Photo isTransitioningTo Screen.Model) spring(stiffness = Spring.StiffnessMedium)
            else spring(stiffness = Spring.StiffnessVeryLow)
        },
        label = "Сдвиг вправо"
    ) { page ->
        tabPositions[page.ordinal].right
    }
    val color by transition.animateColor(
        transitionSpec = { spring(stiffness = Spring.StiffnessVeryLow) },
        label = "Цвет рамки"
    ) { page ->
        if (page == Screen.Photo) Purple40 else Green40
    }
    Box(
        Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = indicatorLeft)
            .width(indicatorRight - indicatorLeft)
            .padding(4.dp)
            .fillMaxSize()
            .border(
                BorderStroke(2.dp, color),
                RoundedCornerShape(4.dp)
            )
    )
}