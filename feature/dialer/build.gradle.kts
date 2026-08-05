plugins {
    id("com.android.library")
    alias(libs.plugins.jetbrainsKotlinAndroid)
    `maven-publish`
}

android {
    namespace = "com.github.yongjhih.appdialer.feature.dialer"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:util"))
    implementation(project(":core:ui"))

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Koin DI
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit)

    // Compose UI Testing
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.yongjhih.app-dialer-app"
            artifactId = "feature-dialer"
            version = "1.0.0"
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
