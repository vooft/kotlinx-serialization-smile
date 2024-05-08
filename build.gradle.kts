plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.detekt)
}

group = "io.github.vooft"
version = "1.0-SNAPSHOT"

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    repositories {
        mavenCentral()
    }

    detekt {
        buildUponDefaultConfig = true
        config.from(files("$rootDir/detekt.yml"))
        basePath = rootDir.absolutePath

        source.setFrom("src/commonMain/kotlin", "src/commonTest/kotlin")
    }
}

subprojects {
    afterEvaluate {
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
}
