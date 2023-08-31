@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  id(libs.plugins.androidApplication.get().pluginId)
  id(libs.plugins.kotlinAndroid.get().pluginId)
  id(libs.plugins.spotless.get().pluginId)
  id(libs.plugins.kotlin.serialization.get().pluginId)
  id(libs.plugins.hilt.get().pluginId)
  id(libs.plugins.ksp.get().pluginId)
}

android {
  namespace = "io.getstream.meeting.room.compose"
  compileSdk = 34

  defaultConfig {
    applicationId = "io.getstream.meeting.room.compose"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  kotlinOptions {
    jvmTarget = "11"
  }

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  // Stream Video SDK
  implementation(libs.stream.video.compose)
  implementation(libs.stream.video.mock)

  // Compose
  implementation(platform(libs.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.material.iconsExtended)
  implementation(libs.androidx.lifecycle.runtime.compose)

  implementation(libs.androidx.hilt.navigation)
  implementation(libs.androidx.compose.navigation)

  // hilt
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)

  // network
  implementation(libs.okhttp)
  implementation(libs.retrofit)
  implementation(libs.sandwich)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.kotlinx.serialization.converter)
}
