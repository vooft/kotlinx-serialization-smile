plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotest.multiplatform)
}

kotlin {
    jvm()
    macosArm64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":kotlinx-serialization-smile-core"))
        }
        jvmTest.dependencies {
            implementation(libs.jackson.dataformat.smile)
            implementation(libs.jackson.module.kotlin)
            implementation(libs.kotest.runner.junit5)
            implementation(libs.kotest.framework.datatest)
            implementation(libs.kotlin.reflect)
            implementation(libs.bundles.logging.jvm)
        }
    }

    // TODO: move to buildSrc
    tasks.named<Test>("jvmTest") {
        useJUnitPlatform()
        filter {
            isFailOnNoMatchingTests = false
        }
        testLogging {
            showExceptions = true
            showStandardStreams = true
            events = setOf(
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
            )
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
}

