plugins {
    id("com.android.application")
    id("com.google.gms.google-services")

    // Add the dependency for the Google services Gradle plugin
}

android {
    namespace = "com.example.amrozia"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.amrozia"
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
    buildFeatures{
        viewBinding = true  // Enable view binding
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-firestore:25.0.0")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    testImplementation("junit:junit:4.13.2")

    //The Glide library is used to load the product image from a URL into the ImageView.
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.google.code.gson:gson:2.9.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    implementation("androidx.navigation:navigation-ui:2.3.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")

    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:chromecast-sender:0.28")


}