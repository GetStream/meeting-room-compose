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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.messageOrNull
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.meeting.room.compose.MeetingRoomApp
import io.getstream.meeting.room.compose.network.StreamVideoNetwork
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

  private val _uiState: MutableStateFlow<LobbyUiState> = MutableStateFlow(LobbyUiState.Loading)
  val uiState: StateFlow<LobbyUiState> = _uiState

  init {
    viewModelScope.launch {
      // request a refreshed user token
      val userNumber = Random.nextInt(10000)
      val name = "stream$userNumber"
      val apiKey = MeetingRoomApp.apiKey
      val response = StreamVideoNetwork.tokenService.fetchToken(
        userId = name,
        apiKey = apiKey,
      )

      response.onSuccess {
        // initialize the Stream Video SDK
        val streamVideo = StreamVideoBuilder(
          context = MeetingRoomApp.app,
          apiKey = apiKey,
          token = data.token,
          user = User(
            id = data.userId,
            name = name,
            image = "http://placekitten.com/200/300",
            role = "admin",
            custom = mapOf("email" to data.userId),
          ),
        ).build()

        // create a call
        val call = streamVideo.call(MeetingRoomApp.callType, MeetingRoomApp.callId)
        _uiState.value = LobbyUiState.TokenRefreshed(call)
      }.onFailure {
        _uiState.value = LobbyUiState.Error(messageOrNull)
      }
    }
  }

  override fun onCleared() {
    super.onCleared()

    // uninstall Stream Video SDK
    StreamVideo.removeClient()
  }
}

sealed interface LobbyUiState {

  data object Loading : LobbyUiState

  data class TokenRefreshed(val call: Call) : LobbyUiState

  data object JoinCompleted : LobbyUiState

  data class Error(val message: String?) : LobbyUiState
}
