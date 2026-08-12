package com.example.scrolllist.ui.screens.audio_lobby

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scrolllist.R
import com.example.scrolllist.data.user_tracks.UserTrack
import com.example.scrolllist.domain.utils.formatDuration
import com.example.scrolllist.ui.theme.MyButton

@Composable
fun AudioLobby(
    exit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AudioLobbyViewModel = viewModel()
) {

    val trakcs by viewModel.tracks.collectAsState()
    val laucher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModel.addTrack(uri)
        }
    }
    var controlTrack: UserTrack? by remember { mutableStateOf(null) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Доступный плейлист",
            textAlign = TextAlign.Center,
            modifier = Modifier
//                .drawWithCache {
//                val rectangle = Path().apply {
//                    moveTo(0f,0f)
//                    lineTo(size.width*0.2f,size.height/2f)
//                    lineTo(0f,size.height)
//                    close()
//                }
//                val brush = Brush.radialGradient(
//                    colors = listOf(Color.Black,Color.DarkGray),
//                    center = size.center,
//                    radius = size.width/2f
//                )
//                onDrawBehind {
//                    drawPath(
//                        brush = brush,
//                        path = rectangle,
//                    )
////                    translate(left = size.width) {
//                        scale(-1f,1f){
//                            drawPath(
//                                brush = brush,
//                                path = rectangle,
//                            )
//                        }
////                    }
//                }
//            }
                .fillMaxWidth().padding(top = 50.dp, bottom = 50.dp)
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(trakcs) { track ->
                TrackItem(
                    track = track,
                    controlTrack = controlTrack,
                    modifier = Modifier.clickable {
                        if (track == controlTrack) controlTrack = null
                        else controlTrack = track
                    }
                )
            }
        }
        HorizontalDivider(
            color = Color.DarkGray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
        AnimatedVisibility(
            modifier = Modifier,
            visible = controlTrack != null,
        ) {
            controlTrack?.let { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF282525))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MyButton(
                        onClick = { viewModel.chooseTrack(track.id) }
                    ) {
                        Text("Поставить")
                    }
                    MyButton(
                        onClick = { viewModel.deleteTrack(track) }
                    ) {
                        Text("Убрать")
                    }
                }
            }
        }
        HorizontalDivider(
            color = Color.DarkGray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 20.dp)
        ) {
            MyButton(
                onClick = {
                    laucher.launch(
                        arrayOf("audio/*")
                    )
                }
            ) {
                Text("Добавить трек")
            }
            MyButton(
                onClick = { viewModel.setDefaultTrack() }
            ) {
                Text("Вернуть стандартный трек")
            }
        }
        MyButton(
            onClick = exit,
            modifier = Modifier.padding(50.dp)
        ) {
            Text("Выйти")
        }
    }
}

@Composable
fun TrackItem(track: UserTrack, modifier: Modifier = Modifier, controlTrack: UserTrack?) {
    val colorText = if (track.isChosen) Color.Green else Color.Gray
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (controlTrack?.id == track.id) Color(0xFF3A3A3C) else Color.Unspecified
            ),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.weight(1f).padding(start = 20.dp).basicMarquee(
                iterations = Int.MAX_VALUE,
//                animationMode = MarqueeAnimationMode.WhileFocused
            ),
            text = track.name,
            maxLines = 1,
            color = colorText
        )
        Text(
            modifier = Modifier.padding(end = 20.dp),
            text = track.duration.formatDuration(),
            color = colorText
        )
    }
}

@Preview
@Composable
fun LobbyPreview() {
    AudioLobby(
        exit = {},
        modifier = Modifier.fillMaxSize()
    )
}