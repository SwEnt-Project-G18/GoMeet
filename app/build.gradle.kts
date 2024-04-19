import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("org.sonarqube") version "4.4.1.3373"
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("jacoco")
    id("com.ncorti.ktfmt.gradle") version "0.16.0"
}

jacoco {
    toolVersion = "0.8.12"
}


secrets {
    // Optionally specify a different file name containing your secrets.
    // The plugin defaults to "local.properties"
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "secrets.defaults.properties"
}


android {
    namespace = "com.github.se.gomeet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.github.se.gomeet"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
    testOptions {
        packagingOptions {
            jniLibs {
                useLegacyPackaging = true
            }
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.4.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.0")
    implementation("androidx.compose.material:material:1.1.1")
    implementation("androidx.compose.material3:material3-android:1.2.0-rc01")
    implementation("androidx.navigation:navigation-compose:2.6.0-rc01")

    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("com.google.maps.android:maps-compose-utils:4.3.0")

    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-database-ktx:20.3.0")
    implementation("com.google.firebase:firebase-firestore:24.10.0")
    implementation("com.google.android.play:core-ktx:1.7.0")

    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui-graphics")
    implementation("com.google.android.gms:play-services-auth:20.6.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.0")
    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.navigation:navigation-testing:2.3.5")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("org.mockito:mockito-android:5.11.0")
    androidTestImplementation("org.mockito:mockito-core:3.11.2")
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.4.0")

    androidTestImplementation("com.kaspersky.android-components:kaspresso:1.4.3")
    // Allure support
    androidTestImplementation("com.kaspersky.android-components:kaspresso-allure-support:1.4.3")
    // Jetpack Compose support
    androidTestImplementation("com.kaspersky.android-components:kaspresso-compose-support:1.4.1")

    implementation("androidx.fragment:fragment:1.5.5")

    implementation("com.squareup.okhttp3:okhttp:3.10.0")


    testImplementation("org.mockito:mockito-inline:2.13.0")
    testImplementation("org.robolectric:robolectric:4.11.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")
    testImplementation("org.mockito:mockito-inline:3.11.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")


    implementation("com.firebaseui:firebase-ui-auth:7.2.0")
    implementation("com.google.android.play:core-ktx:1.7.0")
    // Dependency for using Intents in instrumented tests
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")

    // Dependencies for using MockK in instrumented tests
    androidTestImplementation("io.mockk:mockk:1.13.7")
    androidTestImplementation("io.mockk:mockk-android:1.13.7")
    androidTestImplementation("io.mockk:mockk-agent:1.13.7")

    implementation("com.google.maps.android:maps-compose:4.3.3")

    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.35.0-alpha")

    implementation("com.google.firebase:firebase-storage-ktx")
}


tasks.register("jacocoTestReport", JacocoReport::class) {
    mustRunAfter("testDebugUnitTest", "connectedDebugAndroidTest")

    reports {
        xml.required = true
        html.required = true
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/SignatureChecks.*",
    )
    val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.buildDir) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        include("outputs/code_coverage/debugAndroidTest/connected/*/coverage.ec")
    })
}


// Avoid redundant tests, debug is sufficient
tasks.withType<Test> {
    onlyIf {
        !name.toLowerCaseAsciiOnly().contains("release")
    }
}

sonar {
    properties {
        property("sonar.projectKey", "SwEnt-Project-G18_SwEntApp")
        property("sonar.projectName", "GoMeet")
        property("sonar.organization", "swent-project-g18")
        property("sonar.host.url", "https://sonarcloud.io")

        property("sonar.token", System.getenv("SONAR_TOKEN"))
        // Comma-separated paths to the various directories containing the *.xml JUnit report files. Each path may be absolute or relative to the project base directory.
        property("sonar.junit.reportPaths", "${project.layout.buildDirectory.get()}/test-results/testDebugunitTest/")
        // Paths to xml files with Android Lint issues. If the main flavor is changed, this file will have to be changed too.
        property("sonar.androidLint.reportPaths", "${project.layout.buildDirectory.get()}/reports/lint-results-debug.xml")
        // Paths to JaCoCo XML coverage report files.
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")

    }
}
