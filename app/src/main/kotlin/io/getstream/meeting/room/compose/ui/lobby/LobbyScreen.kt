/*
 * Copyright (c) 2014-2023 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.meeting.room.compose.ui.lobby

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.getstream.meeting.room.compose.R
import io.getstream.meeting.room.compose.ui.AppScreens
import io.getstream.meeting.room.compose.ui.MeetingRoomTheme
import io.getstream.meeting.room.compose.ui.component.StreamButton
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.avatar.UserAvatar
import io.getstream.video.android.compose.ui.components.call.lobby.CallLobby
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.call.state.ToggleCamera
import io.getstream.video.android.core.call.state.ToggleMicrophone
import io.getstream.video.android.mock.StreamMockUtils
import io.getstream.video.android.mock.mockCall

@Composable
fun LobbyScreen(
  navController: NavHostController,
  mainViewModel: MainViewModel = hiltViewModel(),
) {
  val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

  HandleUiStates(uiState = uiState, navController = navController)

  Box(modifier = Modifier.fillMaxSize()) {
    CallLobbyContent(
      uiState = uiState,
      navController = navController,
    )
  }
}

@Composable
private fun HandleUiStates(
  uiState: LobbyUiState,
  navController: NavHostController,
) {
  val context = LocalContext.current
  LaunchedEffect(key1 = uiState) {
    when (uiState) {
      LobbyUiState.JoinCompleted -> navController.navigate(AppScreens.Call.destination)

      is LobbyUiState.Error -> {
        Toast.makeText(context, uiState.message, Toast.LENGTH_SHORT).show()
      }

      else -> Unit
    }
  }
}

@Composable
private fun BoxScope.CallLobbyContent(
  uiState: LobbyUiState,
  navController: NavHostController,
) {
  if (uiState is LobbyUiState.TokenRefreshed) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(VideoTheme.colors.appBackground),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      CallLobbyHeader(uiState.call)

      CallLobbyBody(
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .fillMaxWidth()
          .weight(1f),
        call = uiState.call,
        navController = navController,
      )
    }
  } else if (uiState is LobbyUiState.Loading) {
    CircularProgressIndicator(
      modifier = Modifier.align(Alignment.Center),
      color = VideoTheme.colors.primaryAccent,
    )
  }
}

@Composable
private fun CallLobbyHeader(call: Call) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(24.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    UserAvatar(
      modifier = Modifier.size(32.dp),
      user = call.user,
    )

    Spacer(modifier = Modifier.width(14.dp))

    Text(
      modifier = Modifier.weight(1f),
      color = Color.White,
      text = call.user.id,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
      fontSize = 16.sp,
    )
  }
}

@Composable
private fun CallLobbyBody(
  modifier: Modifier,
  call: Call,
  navController: NavHostController,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(VideoTheme.colors.appBackground),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      modifier = Modifier.padding(horizontal = 30.dp),
      text = stringResource(id = R.string.stream_video),
      color = Color.White,
      fontSize = 26.sp,
      textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      modifier = Modifier.padding(horizontal = 30.dp),
      text = stringResource(id = R.string.call_lobby_description),
      color = VideoTheme.colors.callDescription,
      textAlign = TextAlign.Center,
      fontSize = 17.sp,
    )

    Spacer(modifier = Modifier.height(20.dp))

    val isCameraEnabled: Boolean by if (LocalInspectionMode.current) {
      remember { mutableStateOf(true) }
    } else {
      call.camera.isEnabled.collectAsStateWithLifecycle()
    }

    val isMicrophoneEnabled by if (LocalInspectionMode.current) {
      remember { mutableStateOf(true) }
    } else {
      call.microphone.isEnabled.collectAsStateWithLifecycle()
    }

    CallLobby(
      call = call,
      modifier = Modifier.fillMaxWidth(),
      isCameraEnabled = isCameraEnabled,
      isMicrophoneEnabled = isMicrophoneEnabled,
      onCallAction = { action ->
        when (action) {
          is ToggleCamera -> call.camera.setEnabled(action.isEnabled)
          is ToggleMicrophone -> call.microphone.setEnabled(action.isEnabled)
          else -> Unit
        }
      },
    )

    LobbyDescription(
      call = call,
      navController = navController,
    )
  }
}

@Composable
private fun LobbyDescription(
  call: Call,
  navController: NavHostController,
) {
  val session by call.state.session.collectAsState()

  Column(
    modifier = Modifier
      .padding(horizontal = 35.dp)
      .background(
        color = VideoTheme.colors.callLobbyBackground,
        shape = RoundedCornerShape(16.dp),
      ),
  ) {
    Text(
      modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 12.dp, bottom = 8.dp),
      text = stringResource(
        id = R.string.join_call_description,
        session?.participants?.size ?: 0,
      ),
      color = Color.White,
    )

    StreamButton(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp)
        .clip(RoundedCornerShape(12.dp)),
      text = stringResource(id = R.string.join_call),
      onClick = {
        navController.navigate(AppScreens.Call.destination)
      },
    )
  }
}

@Preview
@Composable
private fun CallLobbyScreenPreview() {
  StreamMockUtils.initializeStreamVideo(LocalContext.current)
  MeetingRoomTheme {
    Box(modifier = Modifier.fillMaxSize()) {
      CallLobbyContent(
        uiState = LobbyUiState.TokenRefreshed(mockCall),
        navController = NavHostController(LocalContext.current),
      )
    }
  }
}
