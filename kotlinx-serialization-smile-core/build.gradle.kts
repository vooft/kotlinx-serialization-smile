import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    // core kotlin plugins
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)

    // publish
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.central.publish)

    // test plugins
    alias(libs.plugins.kotest.multiplatform)
    alias(libs.plugins.mokkery)
}

kotlin {
    jvm()

    macosArm64()
    iosArm64()
    iosSimulatorArm64()

    mingwX64()
    linuxX64()

    js(IR) {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.serialization.core)
        }

        jvmMain.dependencies { }

        jsMain.dependencies { }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotlin.reflect)
        }

        jvmTest.dependencies {
            // must be present even for commonTests only
            implementation(libs.kotest.runner.junit5)
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

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    pom {
        name = "kotlinx-serialization-smile"
        description = "kotlinx.serialization plugin for Smile data format"
        url = "https://github.com/vooft/kotlinx-serialization-smile"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        scm {
            connection = "https://github.com/vooft/kotlinx-serialization-smile"
            url = "https://github.com/vooft/kotlinx-serialization-smile"
        }
        developers {
            developer {
                name = "kotlinx-serialization-smile team"
            }
        }
    }
}
