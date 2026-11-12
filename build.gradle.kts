
import java.util.Properties

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
fun lp(key: String): String = localProps.getProperty(key) ?: ""

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.secrets) apply false
  alias(libs.plugins.google.services) apply false
}
android {
    // ... existing namespace, compileSdk, defaultConfig ...

    buildFeatures {
        compose = true          // keep if already there
        buildConfig = true      // ADD THIS
    }

    buildTypes {
        debug {
            // Google TEST IDs — safe to tap
            manifestPlaceholders["admobAppId"] = "ca-app-pub-3940256099942544~3347511713"
            buildConfigField("String", "ADMOB_BANNER_ID", "\"ca-app-pub-3940256099942544/9214589741\"")
            buildConfigField("String", "ADMOB_INTER_ID",  "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "ADMOB_REWARD_ID", "\"ca-app-pub-3940256099942544/5224354917\"")
        }
        release {
            // YOUR REAL IDs from local.properties
            manifestPlaceholders["admobAppId"] = lp("ADMOB_APP_ID")
            buildConfigField("String", "ADMOB_BANNER_ID", "\"${lp("ADMOB_BANNER_ID")}\"")
            buildConfigField("String", "ADMOB_INTER_ID",  "\"${lp("ADMOB_INTER_ID")}\"")
            buildConfigField("String", "ADMOB_REWARD_ID", "\"${lp("ADMOB_REWARD_ID")}\"")

            isMinifyEnabled = false   // keep whatever is already here
        }
    }
}

dependencies { 
  implementation("com.google.android.gms:play-services-ads:24.7.0")
implementation("com.google.android.ump:user-messaging-platform:3.2.0")
}