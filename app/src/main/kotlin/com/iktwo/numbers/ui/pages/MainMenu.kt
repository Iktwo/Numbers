package com.iktwo.numbers.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.iktwo.numbers.model.state.PageEntry
import com.iktwo.numbers.ui.theme.PaddingXLarge

@Composable
fun MainMenu(
    backgroundColor: Color,
    entries: List<PageEntry>,
    onPageSelected: (PageEntry) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingXLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            entries.forEach { page ->
                Button(
                    onClick = { onPageSelected(page) },
                    enabled = page.enabled
                ) {
                    Text(text = stringResource(page.displayName))
                }
            }
        }
    }
}

@Preview
@Composable
fun MainMenuPreview() {
    MainMenu(Color.Gray, PageEntry.values().filter { it != PageEntry.MAIN_MENU }.toList()) { }
}
