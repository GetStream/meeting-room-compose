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

package io.getstream.meeting.room.compose.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

// In a production environment, it's recommended to generate the user token through your backend service.
// In this project, a straightforward playground API is utilized to provide user tokens conveniently.
// So this API should not be used from in a production level.
// To gain a deeper understanding, refer to https://getstream.io/video/docs/android/guides/client-auth/.
interface StreamTokenService {

  @GET("api/auth/create-token")
  suspend fun fetchToken(
    @Query("user_id") userId: String?,
    @Query("api_key") apiKey: String,
  ): ApiResponse<TokenResponse>
}

object StreamVideoNetwork {

  private val contentType = "application/json".toMediaType()
  private val retrofit =
    Retrofit.Builder()
      .baseUrl("https://stream-calls-dogfood.vercel.app/")
      .addConverterFactory(Json.asConverterFactory(contentType))
      .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
      .build()

  val tokenService = retrofit.create<StreamTokenService>()
}
