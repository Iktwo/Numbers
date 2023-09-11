package com.iktwo.numbers

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Operands(
    val numbers: List<Int>,
    val fontSizes: List<Dp>,
    val alignments: List<TextAlign>
) {
    companion object {
        fun build(size: Int = 7): Operands {
            return Operands(
                numbers = NumberVault.randomList(size, 1, 9),
                // TODO: figure out whatever font sizes make sense. Normally would use SP, but we don't want this to respect system settings ;)
                fontSizes = NumberVault.randomList(size, 16, 64).map { it.dp },
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