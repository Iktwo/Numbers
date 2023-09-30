package com.iktwo.numbers.ui.pages

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PageMultiply() {
    LazyColumn(userScrollEnabled = false) {
        itemsIndexed((1..99).toList()) { index, number ->
            Text(text = "${number}x$number")
        }
    }
}