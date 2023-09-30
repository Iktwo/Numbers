package com.iktwo.numbers.model.state

import androidx.annotation.StringRes
import com.iktwo.numbers.R

enum class PageEntry(@StringRes val displayName: Int, val enabled: Boolean) {
    MAIN_MENU(R.string.main_menu, true),
    SUMS(R.string.sum_them_all, true),
    MULTIPLY(R.string.multiply, true),
    TAP_SMALLEST(R.string.tap_the_smallest_number, false),
    TAP_LARGEST(R.string.tap_the_largest_number, false)
}
