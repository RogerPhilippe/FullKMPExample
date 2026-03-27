import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

abstract class GenerateDynamicFormSourcesTask : DefaultTask() {
    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val targetDir = outputDir.get().asFile
        targetDir.mkdirs()

        val xmlContent = inputFile.get().asFile.readText()
        val escapedXml = xmlContent.replace("$", "\${'$'}")

        val generatedSource = buildString {
            appendLine("package br.com.phs.fullexample")
            appendLine()
            appendLine("internal object GeneratedDynamicForms {")
            appendLine("    val sampleLoginFormXml: String = \"\"\"")
            appendLine(escapedXml)
            appendLine("\"\"\"")
            appendLine("}")
        }

        File(targetDir, "GeneratedDynamicForms.kt").writeText(generatedSource)
    }
}

val generatedDynamicFormsDir = layout.buildDirectory.dir("generated/dynamicForms/kotlin")
val loginFormDefinitionFile = project.file("form-definitions/login-screen.xml")

val generateDynamicFormSources by tasks.registering(GenerateDynamicFormSourcesTask::class) {
    inputFile.set(loginFormDefinitionFile)
    outputDir.set(generatedDynamicFormsDir)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    js {
        outputModuleName = "shared"
        browser()
        binaries.library()
        generateTypeScriptDefinitions()
        compilerOptions {
            target = "es2015"
        }
    }
    
    sourceSets {
        named("commonMain") {
            kotlin.srcDir(generatedDynamicFormsDir)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
        }
        commonMain.dependencies {
            // put your Multiplatform dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

tasks.matching { it.name.startsWith("compile") }.configureEach {
    dependsOn(generateDynamicFormSources)
}

android {
    namespace = "br.com.phs.fullexample.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
