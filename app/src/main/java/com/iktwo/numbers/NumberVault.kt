package com.iktwo.numbers

import kotlin.random.Random

object NumberVault {
    fun randomList(size: Int, min: Int = 0, max: Int = 99): List<Int> {
        return List(size) { Random.nextInt(min, max + 1) }
    }
}