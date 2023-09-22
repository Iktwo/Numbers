package com.iktwo.numbers.model.uistate

import com.iktwo.numbers.model.Operands
import com.iktwo.numbers.model.state.InputState
import com.iktwo.numbers.model.state.ModelState
import com.iktwo.numbers.model.state.PageEntry

data class SumGameUIState(
    val inputState: InputState,
    val modelState: ModelState,
    val operands: Operands
)
