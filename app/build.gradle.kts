import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")

    // Add the dependency for the Google services Gradle plugin
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").inputStream().use { load(it) } // Load the local.properties file
}

android {
    namespace = "com.globalfashion.amrozia"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.globalfashion.amrozia"
        minSdk = 24
        targetSdk = 34
        versionCode = 7
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add the following line to the defaultConfig block.
        buildConfigField("String", "CONTENT_TYPE", "\"${localProperties["CONTENT_TYPE"]}\"")
        buildConfigField("String", "CLIENT_ID", "\"${localProperties["CLIENT_ID"]}\"")
        buildConfigField("String", "CLIENT_SECRET", "\"${localProperties["CLIENT_SECRET"]}\"")
        buildConfigField("String", "API_VERSION", "\"${localProperties["API_VERSION"]}\"")
        buildConfigField("String", "API_KEY_SENDINBLUE", "\"${localProperties["API_KEY_SENDINBLUE"]}\"")
        buildConfigField("String", "BUSINESS_EMAIL", "\"${localProperties["BUSINESS_EMAIL"]}\"")
        buildConfigField("String", "WORK_EMAIL", "\"${localProperties["WORK_EMAIL"]}\"")
        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${localProperties["GOOGLE_CLIENT_ID"]}\"")
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
        buildConfig = true  // Enable build config
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    // Material Design
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth:23.0.0")
    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    // Declare the dependency for the Firebase Authentication library
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    // Firebase Storage
    implementation("com.google.firebase:firebase-storage:21.0.0")
    // Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging:24.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.firebase:firebase-perf:21.0.1")

    // junit
    testImplementation("junit:junit:4.13.2")
    //The Glide library is used to load the product image from a URL into the ImageView.
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Glide's annotation processor generates GlideApp class, which is used to load the image.
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    // Gson library is used to convert JSON objects to Java objects.
    implementation("com.google.code.gson:gson:2.9.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    // Navigation
    implementation("androidx.navigation:navigation-ui:2.3.5")
    // Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Sdp and Ssp libraries are used to create a responsive layout.
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")
    // Youtube Player
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:chromecast-sender:0.28")

    // Firebase Crashlytics and Firebase Analytics
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    // Sendinblue SDK
    implementation("com.sun.mail:android-mail:1.6.2")
    implementation("com.sun.mail:android-activation:1.6.2")

    // Retrofit is used to make network requests.
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.google.firebase:firebase-storage:20.1.0")
    // Glide library is used to load the product image from a URL into the ImageView.
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // Add this dependency for Cashfree Payment SDK
    implementation("com.cashfree.pg:api:2.1.17")

    // Volley is used to make network requests.
    implementation("com.android.volley:volley:1.2.1")
    // Lottie is used to display animations.
    implementation("com.airbnb.android:lottie:6.5.1")
    // OkHttp is used to make network requests.
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // PhotoView library
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
}