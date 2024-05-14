plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.benchmark)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.allopen)
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
    annotation("kotlinx.benchmark.State")
}

kotlin {
    jvm()
    macosArm64()

    applyDefaultHierarchyTemplate()

    targets.configureEach {
        compilations.configureEach {
            kotlinOptions.allWarningsAsErrors = true
        }
    }

    sourceSets.configureEach {
        languageSettings {
            progressiveMode = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.benchmark.runtime)
                implementation(project(":kotlinx-serialization-smile-core"))
                implementation(project(":kotlinx-serialization-smile-test"))
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.jackson.dataformat.smile)
                implementation(libs.jackson.module.kotlin)
                implementation(libs.kotlin.reflect)
                implementation(libs.bundles.logging.jvm)
            }
        }

        nativeMain { }
    }
}

benchmark {
    targets.register("jvm")

    // not working for some reason
//    targets.register("macosArm64")
}
