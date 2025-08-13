// In your app-level build.gradle.kts file

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // ViewModel for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.1")

    // AdMob Ads SDK
    implementation("com.google.android.gms:play-services-ads:23.1.0")

    // GSON for saving data objects
    implementation("com.google.code.gson:gson:2.10.1")

    // ML Kit Barcode Scanning (for camera feature)
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // CameraX libraries for live camera preview (Updated versions)
    val cameraxVersion = "1.3.4"
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
android {
    namespace = "com.yourcompany.smartgrocer"
    compileSdk = 34 // UPDATE THIS

    defaultConfig {
        applicationId = "com.yourcompany.smartgrocer"
        minSdk = 24
        targetSdk = 34 // UPDATE THIS
        //...
    }
    //...
}
android {
    namespace = "com.adprofitx.smartgrocer"
    compileSdk = 34 // UPDATE THIS

    defaultConfig {
        applicationId = "com.adprofitx.smartgrocer"
        minSdk = 24
        targetSdk = 34 // UPDATE THIS
        //...
    }
    //...
}
