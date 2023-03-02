import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
}

kotlin {
    android()
    
    val xcf = XCFramework()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "PhotoFeed"
            xcf.add(this)
        }
    }

    sourceSets {
        val ktorVersion = "2.2.4"

        /* Main source sets */
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
            }
        }

        val androidMain by getting{
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }

        /* Main hierarchy */
        androidMain.dependsOn(commonMain)

        iosMain.dependsOn(commonMain)
        iosX64Main.dependsOn(iosMain)
        iosArm64Main.dependsOn(iosMain)
        iosSimulatorArm64Main.dependsOn(iosMain)


        /* Test source sets */
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
            }
        }

        val androidTest by getting

        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating

        /* Test hierarchy */
        androidTest.dependsOn(commonTest)

        iosTest.dependsOn(commonTest)
        iosArm64Test.dependsOn(iosTest)
        iosX64Test.dependsOn(iosTest)
        iosSimulatorArm64Test.dependsOn(iosTest)
    }
}

android {
    namespace = "com.asturiancoder.photofeed"
    compileSdk = 32
    defaultConfig {
        minSdk = 28
        targetSdk = 32
    }
}