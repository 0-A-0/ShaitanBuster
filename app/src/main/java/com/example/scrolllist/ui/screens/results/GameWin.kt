package com.example.scrolllist.ui.screens.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scrolllist.ui.screens.game.AxeButtons
import com.example.scrolllist.ui.theme.MyButton

@Composable
fun GameWin(RestartGame: () -> Unit, kills: Int, modifier: Modifier = Modifier /*time:Time*/) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            ":)",
            fontSize = 50.sp
        )
        Text(
            "Вы разогнали всю шпану",
            fontSize = 16.sp
        )
        Text(
            "Назойливые детишки больше не пристают к вам",
            fontSize = 16.sp,
            modifier = Modifier.padding(10.dp)
        )
        MyButton(onClick = RestartGame) {
            Text(
                "Выйти в меню",
                fontSize = 20.sp
            )
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun GameWinScreen(){
    GameWin(
        modifier = Modifier.fillMaxSize(),
        RestartGame = {},
        kills = 0
    )
}