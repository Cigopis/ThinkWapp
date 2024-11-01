plugins {
    id("com.android.application")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.wongcoco.thinkwapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.wongcoco.thinkwapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Import Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    implementation ("com.google.firebase:firebase-auth:22.0.0")
    implementation ("com.google.firebase:firebase-firestore:24.4.0")
    implementation ("com.google.android.gms:play-services-auth:20.5.0")

    implementation("org.mindrot:jbcrypt:0.4")

    implementation ("com.mailjet:mailjet-client:5.2.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.google.android.gms:play-services-location:21.0.1") // Versi terbaru dapat diperiksa
    implementation ("com.github.bumptech.glide:glide:4.15.0")








}
