plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
}

kotlin {
    android()

    iosX64()
    iosArm64()
    iosSimulatorArm64()


    sourceSets {

        /* Test source sets */
        val commonTest by getting {
            dependencies {
                implementation(project(":photofeed"))

                implementation(kotlin("test"))
                implementation("io.ktor:ktor-client-mock:2.2.3")
            }
        }
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