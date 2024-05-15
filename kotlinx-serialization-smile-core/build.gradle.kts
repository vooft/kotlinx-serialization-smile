plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotest.multiplatform)
    alias(libs.plugins.mokkery)
}

kotlin {
    jvm()

    macosArm64()

    js(IR) {
        browser()
        nodejs()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.serialization.core)
            api(libs.kotlinx.io.bytestring)
            implementation(libs.kotlin.logging.core)
        }

        jvmMain.dependencies {
            implementation(libs.kotlin.logging.jvm)
        }

        jsMain.dependencies {
            implementation(libs.kotlin.logging.js)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.framework.datatest)
            implementation(libs.kotlin.reflect)
        }

        jvmTest.dependencies {
            // must be present even for commonTests only
            implementation(libs.kotest.runner.junit5)
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
