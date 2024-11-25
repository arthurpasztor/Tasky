import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("app.cash.sqldelight")
}

android {
    namespace = "com.example.tasky"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tasky"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    val apiKey = gradleLocalProperties(rootDir).getProperty("API_KEY")

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            buildConfigField("String", "API_KEY", "\"$apiKey\"")
            buildConfigField("String", "BASE_URL", "\"https://tasky.pl-coding.com\"")
        }

        debug {
            buildConfigField("String", "API_KEY", "\"$apiKey\"")
            buildConfigField("String", "BASE_URL", "\"https://tasky.pl-coding.com\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.foundation:foundation:1.7.0-beta03")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.3")
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Navigation
    val destinationsVersion = "2.1.0-beta02"
    implementation("io.github.raamcosta.compose-destinations:core:$destinationsVersion")
    ksp("io.github.raamcosta.compose-destinations:ksp:$destinationsVersion")

    // Koin
    implementation("io.insert-koin:koin-android:3.4.0")
    implementation("io.insert-koin:koin-androidx-compose:3.4.2")

    // Remote
    val ktorVersion = "2.3.0"
    val ktorCoreVersion = "2.3.9"
    implementation("io.ktor:ktor-client-android:${ktorVersion}")
    implementation("io.ktor:ktor-client-core:${ktorCoreVersion}")
    implementation("io.ktor:ktor-client-cio:${ktorCoreVersion}")
    implementation("io.ktor:ktor-client-auth:${ktorCoreVersion}")
    implementation("io.ktor:ktor-client-logging:${ktorVersion}")
    implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
    implementation("io.ktor:ktor-client-serialization:${ktorVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    // Encrypted Shared Preferences
    implementation("androidx.security:security-crypto:1.0.0-alpha02")

    // Splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Date Picker
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")

    // Coil
    implementation("io.coil-kt:coil-compose:2.1.0")

    // SQL delight
    implementation("app.cash.sqldelight:android-driver:2.0.0")
    implementation("app.cash.sqldelight:coroutines-extensions-jvm:2.0.0")
    implementation("app.cash.sqldelight:primitive-adapters:2.0.0-alpha05")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
}

sqldelight {
    databases {
        create("TaskyDatabase") {
            packageName.set("com.example.tasky.db")
            deriveSchemaFromMigrations.set(true)
            verifyMigrations = true
            schemaOutputDirectory = file("src/main/sqldelight/com.example.tasky.db")
            migrationOutputDirectory = file("src/main/sqldelight/com.example.tasky.migrations")
        }
    }
}