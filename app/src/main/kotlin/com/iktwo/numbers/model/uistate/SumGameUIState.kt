package com.iktwo.numbers.model.uistate

import com.iktwo.numbers.model.Operands
import com.iktwo.numbers.model.state.InputState

data class SumGameUIState(
    val inputState: InputState,
    val operands: Operands
)
