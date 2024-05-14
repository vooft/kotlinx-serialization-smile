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
    benchmark {
        targets.register("jvm")
        targets.register("macosArm64")

//  TODO: enable Kotlin/Native benchmark once the issue
//   with kotlinx.benchmarks 0.3.1 and Kotlin 1.5.21+ compatibility is resolved
//    targets.register("macosX64")
//    targets.register("linuxX64")
//    targets.register("mingwX64")

//        configurations["main"].apply {
//            warmups = 5
//            iterations = 10
//        }
    }
}
