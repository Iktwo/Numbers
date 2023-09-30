package com.iktwo.numbers.model

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iktwo.numbers.NumberVault

data class Operands(
    val numbers: List<Int>,
    val fontSizes: List<Dp>,
    val alignments: List<TextAlign>
) {
    val sum = numbers.reduce { acc, number -> acc + number }

    companion object {
        fun buildRandom(size: Int = 5): Operands {
            return Operands(
                numbers = NumberVault.randomList(size, 1, 9),
                fontSizes = NumberVault.randomList(size, 16, 80).map { it.dp },
                alignments = NumberVault.randomList(size, 1, 3).map {
                    when (it) {
                        1 -> {
                            TextAlign.Start
                        }

                        2 -> {
                            TextAlign.Center
                        }

                        else -> {
                            TextAlign.End
                        }
                    }
                }
            )
        }
    }
}
