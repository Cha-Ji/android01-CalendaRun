package com.drunkenboys.ckscalendar.yearcalendar.composeView

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarDesignObject
import com.drunkenboys.ckscalendar.utils.dp

@Composable
fun PaddingText(
    day: CalendarDate,
    design: CalendarDesignObject
) {
    Text(
        text = "${day.date.dayOfMonth}",
        modifier = Modifier
            .layoutId(day.date.toString())
            .alpha(0f),
        textAlign = TextAlign.Center,
        fontSize = design.textSize.dp()
    )
}

@Preview
@Composable
fun PreviewPaddingText() {
    Text(
        text = "11",
        modifier = Modifier.alpha(0f),
        textAlign = TextAlign.Center
    )
}