package com.example.ting

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 *
 * @author fanshun
 * @date 2024/11/20 14:11
 */
@Preview
@Composable
fun Test() {
    val rouletteList = listOf(
        LiveRoomRouletteItem(0, 1),
        LiveRoomRouletteItem(1, 2),
        LiveRoomRouletteItem(1, 3),
        LiveRoomRouletteItem(1, 4),
        LiveRoomRouletteItem(1, 6),
        LiveRoomRouletteItem(1, 4),
        LiveRoomRouletteItem(1, 2),
        LiveRoomRouletteItem(1, 5),
        LiveRoomRouletteItem(1, 5),
    )
    val arcColor1 = Color(0xFFFCF5FF)
    val arcColor2 = Color(0xFFE8CFFF)
    val arcColor3 = Color(0xFFF1E1FF)
    Canvas(modifier = Modifier.fillMaxSize()) {
        translate(size.width / 2, size.height / 2) {
            val size = size.copy(width = size.minDimension, height = size.minDimension)
            var startAngle = 0f
            rouletteList.forEachIndexed { index, liveRoomRouletteItem ->
                val arcColor = if (index % 2 == 1) {
                    arcColor2
                } else if (index == rouletteList.lastIndex) {
                    arcColor3
                } else {
                    arcColor1
                }
                val sweepAngle = 360f * liveRoomRouletteItem.weight / rouletteList.sumOf { it.weight }
                rotate(degrees = startAngle + sweepAngle / 2, pivot = Offset.Zero) {
                    drawArc(color = arcColor, startAngle = -90 - sweepAngle / 2, sweepAngle = sweepAngle, topLeft = -size.center, useCenter = true, size = size)
                    drawArc(color = Color.White, startAngle = -90 - sweepAngle / 2, sweepAngle = sweepAngle, useCenter = true, topLeft = -size.center, size = size, style = Stroke(2.dp.toPx()))
                }
                startAngle += sweepAngle
            }
        }
    }
}