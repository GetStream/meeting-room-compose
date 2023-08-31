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

package io.getstream.meeting.room.compose

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MeetingRoomApp : Application() {

  override fun onCreate() {
    super.onCreate()

    app = this
  }

  companion object {

    @JvmStatic
    lateinit var app: MeetingRoomApp

    // This API key should be only used for demonstrating this project.
    // If you want to get your own api key, follow the instructions below:
    // https://getstream.io/video/docs/android/playground/stream-api-key/
    const val apiKey = "mmhfdzb5evj2"

    // call type and call id decide which meeting room you want to join.
    // ideally you should not define them statically in the Application class,
    // instead you need to use different call id for each meeting room.
    // For more information, check out https://getstream.io/video/docs/android/guides/joining-creating-calls/
    const val callType = "default"
    const val callId = "GzGQPrISLSHk"
  }
}
