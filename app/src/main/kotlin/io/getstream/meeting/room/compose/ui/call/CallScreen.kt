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

package io.getstream.meeting.room.compose.ui.call

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.controls.ControlActions
import io.getstream.video.android.compose.ui.components.call.controls.actions.CancelCallAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.FlipCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleMicrophoneAction
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantVideo
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantsGrid
import io.getstream.video.android.compose.ui.components.call.renderer.RegularVideoRendererStyle

@Composable
fun CallScreen(
  navController: NavHostController,
  callViewModel: CallViewModel = hiltViewModel(),
) {
  val uiState by callViewModel.uiState.collectAsStateWithLifecycle()

  Box(modifier = Modifier.fillMaxSize()) {
    when (uiState) {
      CallUiState.Success, CallUiState.Loading -> {
        CallScreenContent(
          navController = navController,
          callViewModel = callViewModel,
        )
      }

      is CallUiState.Error -> {
        Text(
          modifier = Modifier
            .align(Alignment.Center)
            .padding(horizontal = 24.dp),
          text = (uiState as CallUiState.Error).message,
          color = VideoTheme.colors.errorAccent,
          fontSize = 14.sp,
        )
      }
    }
  }
}

@Composable
private fun CallScreenContent(
  navController: NavHostController,
  callViewModel: CallViewModel,
) {
  val call = remember { callViewModel.call }
  val speakingWhileMuted by call.state.speakingWhileMuted.collectAsState()
  val isCameraEnabled by call.camera.isEnabled.collectAsState()
  val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()

  CallContent(
    modifier = Modifier.background(color = VideoTheme.colors.appBackground),
    call = callViewModel.call,
    enableInPictureInPicture = true,
    onBackPressed = {
      navController.navigateUp()
    },
    videoContent = {
      ParticipantsGrid(
        call = call,
        modifier = Modifier
          .fillMaxSize()
          .weight(1f)
          .padding(6.dp),
        style = RegularVideoRendererStyle(),
        videoRenderer = { modifier, _, participant, style ->
          ParticipantVideo(
            modifier = modifier
              .padding(4.dp)
              .clip(RoundedCornerShape(8.dp)),
            call = call,
            participant = participant,
            style = style,
          )
        },
      )
    },
    controlsContent = {
      ControlActions(
        call = call,
        actions = listOf(
          {
            ToggleMicrophoneAction(
              modifier = Modifier.size(
                VideoTheme.dimens.controlActionsButtonSize,
              ),
              isMicrophoneEnabled = isMicrophoneEnabled,
              onCallAction = { call.microphone.setEnabled(it.isEnabled) },
            )
          },
          {
            ToggleCameraAction(
              modifier = Modifier.size(
                VideoTheme.dimens.controlActionsButtonSize,
              ),
              isCameraEnabled = isCameraEnabled,
              onCallAction = { call.camera.setEnabled(it.isEnabled) },
            )
          },
          {
            FlipCameraAction(
              modifier = Modifier.size(
                VideoTheme.dimens.controlActionsButtonSize,
              ),
              onCallAction = { call.camera.flip() },
            )
          },
          {
            CancelCallAction(
              modifier = Modifier.size(
                VideoTheme.dimens.controlActionsButtonSize,
              ),
              onCallAction = {
                callViewModel.leave()
                navController.navigateUp()
              },
            )
          },
        ),
      )
    },
  )

  if (speakingWhileMuted) {
    SpeakingWhileMuted()
  }
}

@Composable
private fun SpeakingWhileMuted() {
  Snackbar {
    Text(text = "You're talking while muting the microphone!")
  }
}
