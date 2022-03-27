@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.mpei.tensorflow.ui.screens.model

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mpei.tensorflow.ui.theme.PurpleGrey40
import com.mpei.tensorflow.ui.theme.PurpleGrey80
import com.mpei.tensorflow.ui.theme.Teal70

private val modelsInfo = arrayListOf(
    "Модель обнаружения объектов, обученная на наборе данных COCO." to "https://tfhub.dev/tensorflow/lite-model/ssd_mobilenet_v1/1/metadata/2",
    "Модель обнаружения объектов EfficientDet-Lite3 (магистраль EfficientNet-Lite3 с экстрактором функций BiFPN, общим предиктором коробки и фокальными потерями), обученная на наборе данных COCO 2017, оптимизирована для TFLite, предназначена для работы на мобильных процессорах, GPU и EdgeTPU." to "https://tfhub.dev/tensorflow/lite-model/efficientdet/lite3/detection/metadata/1",
    "Детектор мобильных объектов, не зависящий от класса." to "https://tfhub.dev/google/lite-model/object_detection/mobile_object_localizer_v1/1/metadata/2",
    "Модель обнаружения объектов EfficientDet-Lite2 (магистраль EfficientNet-Lite2 с экстрактором функций BiFPN, общим предиктором коробки и фокальными потерями), обученная на наборе данных COCO 2017, оптимизирована для TFLite, предназначена для работы на мобильных процессорах, GPU и EdgeTPU." to "https://tfhub.dev/tensorflow/lite-model/efficientdet/lite2/detection/metadata/1",
    "Модель обнаружения объектов EfficientDet-Lite4 (магистраль EfficientNet-Lite4 с экстрактором функций BiFPN, общим предиктором коробки и фокальными потерями), обученная на наборе данных COCO 2017, оптимизирована для TFLite, предназначена для работы на мобильных процессорах CPU, GPU и EdgeTPU." to "https://tfhub.dev/tensorflow/lite-model/efficientdet/lite4/detection/metadata/2",
    "Модель обнаружения объектов EfficientDet-Lite1 (магистраль EfficientNet-Lite1 с экстрактором функций BiFPN, общим предиктором коробки и фокальными потерями), обученная на наборе данных COCO 2017, оптимизирована для TFLite, предназначена для работы на мобильных процессорах CPU, GPU и EdgeTPU." to "https://tfhub.dev/tensorflow/lite-model/efficientdet/lite1/detection/metadata/1",
    "Модель обнаружения объектов EfficientDet-Lite0 (магистраль EfficientNet-Lite0 с экстрактором функций BiFPN, общим предиктором коробки и фокальными потерями), обученная на наборе данных COCO 2017, оптимизирована для TFLite, предназначена для работы на мобильных процессорах, GPU и EdgeTPU." to "https://tfhub.dev/tensorflow/lite-model/efficientdet/lite0/detection/metadata/1"
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
                    dampingRatio = Spring.DampingRatioLowBouncy,
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(PurpleGrey40)
                .height(1.dp)
        )
        MainText(text = "Описание", modifier = Modifier.padding(vertical = 8.dp))
        Text(text = modelsInfo[index].first)
        DescriptionModel(uriString = modelsInfo[index].second)
    }
}

@Composable
fun DescriptionModel(uriString: String) {
    val context = LocalContext.current
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(uriString)) }

    MainText(
        text = "Подробное описание",
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .padding(vertical = 16.dp)
            .clickable { context.startActivity(intent) })
}

@Composable
private fun MainText(text: String, modifier: Modifier = Modifier, textDecoration: TextDecoration = TextDecoration.None) {
    Text(
        text = text,
        fontStyle = FontStyle.Italic,
        fontFamily = FontFamily.Serif,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier,
        textDecoration = textDecoration
    )
}