plugins {
    val kotlinVersion = "1.8.10"

    id("com.android.library").version("7.4.1").apply(false)
    id("com.diffplug.spotless").version("6.16.0").apply(true)
    kotlin("multiplatform").version(kotlinVersion).apply(false)
    kotlin("plugin.serialization").version(kotlinVersion).apply(false)
}

apply(from = "scripts/git-hooks/install.gradle.kts")

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    val ktlintVersion = "0.48.2"

    kotlin {
        target("**/*.kt")
        targetExclude("$buildDir/**/*.kt")
        targetExclude("bin/**/*.kt")

        ktlint(ktlintVersion)
    }

    kotlinGradle {
        target("*.gradle.kts")

        ktlint(ktlintVersion)
    }
}
