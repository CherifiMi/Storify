import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "1.9.0"
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation("io.insert-koin:koin-android:3.4.0")
            implementation("io.insert-koin:koin-androidx-compose:3.4.0")
            implementation("androidx.work:work-runtime:2.8.1")

            implementation(libs.ktor.client.okhttp)

            // Amplify
            val amplify_version = "1.31.3"
            implementation("com.amplifyframework:aws-storage-s3:$amplify_version")
            implementation("com.amplifyframework:aws-auth-cognito:$amplify_version")

        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            //ktor
            implementation(libs.ktor.core)

            //okhttp
            implementation("com.squareup.okhttp3:okhttp:4.10.0")

            // mongodb
            implementation("org.mongodb:mongodb-driver-kotlin-sync:5.1.1")

            // koin
            implementation("io.insert-koin:koin-core:3.4.0")
            implementation("io.insert-koin:koin-compose:1.1.5")

            // coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")

            // serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

            // aws
            implementation("aws.sdk.kotlin:s3:1.0.0")

            // coil
            implementation(libs.coil.compose.core)
            implementation(libs.coil.mp)
            implementation(libs.coil.network.ktor)
            implementation(libs.coil.compose)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.ktor.client.okhttp)
            runtimeOnly(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = "org.example.storify"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.example.storify"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/proguard/androidx-annotations.pro"
            excludes += "META-INF/native-image/**"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "storify"
            packageVersion = "1.0.0"
            windows {
                iconFile.set(project.file("src/main/resources/ic_app.ico"))
            }
        }
    }
}
