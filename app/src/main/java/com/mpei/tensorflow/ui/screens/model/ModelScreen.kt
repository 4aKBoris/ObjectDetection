@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.mpei.tensorflow.ui.screens.model

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mpei.tensorflow.ui.theme.PurpleGrey40
import com.mpei.tensorflow.ui.theme.PurpleGrey80
import com.mpei.tensorflow.ui.theme.Teal70

private val modelsInfo = arrayListOf(
    arrayListOf("Помидор", "Салат"),
    arrayListOf("Помидор", "Салат"),
    arrayListOf("Помидор", "Салат"),
    arrayListOf("Помидор", "Салат"),
    arrayListOf("Помидор", "Салат"),
    arrayListOf("Помидор", "Салат"),
    arrayListOf("Помидор", "Салат")
)

@Composable
fun ModelScreen(backgroundColor: Color, model: String, setModel: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .background(color = backgroundColor)
            .fillMaxSize()
    ) {
        items(7) {
            Item(index = it, model = model, setModel = setModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Item(index: Int, model: String, setModel: (String) -> Unit) {

    val modelName = "Model${index + 1}"

    val (info, setInfo) = remember { mutableStateOf(false) }

    Card(
        containerColor = PurpleGrey80,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessVeryLow,
                    visibilityThreshold = IntSize.VisibilityThreshold
                )
            ),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        contentColor = Color.Black
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainText(text = modelName)
            RadioButton(
                modifier = Modifier.padding(horizontal = 8.dp),
                selected = modelName == model,
                onClick = { setModel(modelName) },
                enabled = true,
                colors = RadioButtonDefaults.colors(selectedColor = Teal70)
            )
            IconButton(onClick = {
                setInfo(!info)
            }) {
                Icon(imageVector = Icons.Rounded.Info, contentDescription = "Информация")
            }
        }

        if (info) InfoItem(index = index)
    }
}

@Composable
private fun InfoItem(index: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(PurpleGrey40)
                .height(1.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            MainText(text = "Распознавание:")
            Column(
                modifier = Modifier.padding(start = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                for (text in modelsInfo[index]) {
                    Text(text = text, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun MainText(text: String) {
    Text(
        text = text,
        fontStyle = FontStyle.Italic,
        fontFamily = FontFamily.Serif,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium
    )
}