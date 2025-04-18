import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidTarget()

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64
        else -> ::iosX64
    }

    iosTarget("ios") {
        binaries {
            framework {
                baseName = "shared"
                isStatic = true
            }
        }
    }

    cocoapods {
        version = "1.0"
        ios.deploymentTarget = "14.1"

        framework {
            baseName = "shared"
        }

        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE

        pod("FirebaseCore")
        pod("FirebaseAnalytics")
        pod("FirebaseCrashlytics")
        pod("FirebaseRemoteConfig")
        pod("FirebaseAuth")
        pod("FirebaseFirestore")
        pod("FirebaseStorage")
        pod("FirebaseMessaging")
    }

    sourceSets {
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.bundles.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.bundles.common)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native)
        }
    }
}

android {
    namespace = libs.versions.applicationId.get()
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}


sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("database")
            schemaOutputDirectory.set(file("${project.projectDir}/src/commonMain/sqldelight/schema"))
            verifyMigrations.set(true)
        }
    }
}
