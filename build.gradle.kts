plugins {
    val kotlinVersion = "1.8.10"

    id("com.android.library").version("7.4.1").apply(false)
    kotlin("multiplatform").version(kotlinVersion).apply(false)
    kotlin("plugin.serialization").version(kotlinVersion).apply(false)

    // Static Analysis plugins
    id("com.diffplug.spotless").version("6.16.0")
    id("io.gitlab.arturbosch.detekt").version("1.22.0")

    // Code Coverage plugin
    id("org.jetbrains.kotlinx.kover").version("0.6.1")
}

// Register `installGitHooks` gradle task
apply(from = "buildscripts/git-hooks/install.gradle.kts")

// Spotless configuration
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
        target("**/*.gradle.kts")

        ktlint(ktlintVersion)
    }
}

allprojects {

    // Detekt configuration
    apply(plugin = "io.gitlab.arturbosch.detekt").also {

        detekt {
            config = rootProject.files("buildscripts/detekt.yml")

            reports {
                sarif.required.set(true)
            }
        }

        tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
            setSource(files(project.projectDir))
            exclude("**/build/**")
            exclude {
                it.file.relativeTo(projectDir).startsWith(project.buildDir.relativeTo(projectDir))
            }
        }
    }

    // Kover configuration
    apply(plugin = "org.jetbrains.kotlinx.kover").apply {
        koverMerged {
            enable()

            filters {
                classes {
                    excludes += listOf(
                        "*.*BuildConfig*",
                    )
                }
            }
        }
    }
}
