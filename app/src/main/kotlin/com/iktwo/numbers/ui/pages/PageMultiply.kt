package com.iktwo.numbers.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun PageMultiply() {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        val lineHeightSp: TextUnit = 22.sp
        val lineHeightDp: Dp = with(LocalDensity.current) {
            lineHeightSp.toDp()
        }
        val listState = rememberLazyListState()
        val data = remember {
            mutableStateListOf<Int>()
        }

        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {

            scope.launch(Dispatchers.Default) {
                    while (true) {
                        delay(2000)
                        data.add(Random.nextInt())
                    }
            }

        }




        Box(modifier = Modifier.height(lineHeightDp * 4)) {

            LazyColumn(state = listState, userScrollEnabled = true) {
                itemsIndexed(data) { index, number ->
                    Text(
                        modifier = Modifier.background(Color(
                            red = Random.nextInt(255),
                            green = Random.nextInt(255),
                            blue = Random.nextInt(255),
                            alpha = 100
                        )),
                        fontSize = lineHeightSp,
                        text = "${number}x$number",
//                        style = TextStyle(
//                            fontSize = lineHeightSp,
//                            lineHeightStyle = LineHeightStyle(
//                                alignment = LineHeightStyle.Alignment.Proportional,
//                                trim = LineHeightStyle.Trim.Both
//                            )
//                        )
                    )
                }
            }
        }
    }
}