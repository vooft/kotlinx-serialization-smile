import kotlinx.benchmark.gradle.JvmBenchmarkTarget
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

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
            implementation(libs.kotlinx.benchmark.runtime)
            implementation(project(":kotlinx-serialization-smile-core"))
            implementation(project(":kotlinx-serialization-smile-test"))
        }

        jvmMain.dependencies {
            implementation(libs.jackson.dataformat.smile)
            implementation(libs.jackson.module.kotlin)
            implementation(libs.kotlin.reflect)
        }

        jsMain.dependencies { }

        nativeMain { }
    }
}

benchmark {
    targets.register("jvm") {
        this as JvmBenchmarkTarget
        jmhVersion = "1.37" // TODO: remove once kotlinx.benchmark updates bundled JMH
    }

    targets.register("js")
    targets.register("wasmJs")

    // not working for some reason
    targets.register("macosArm64")
}
