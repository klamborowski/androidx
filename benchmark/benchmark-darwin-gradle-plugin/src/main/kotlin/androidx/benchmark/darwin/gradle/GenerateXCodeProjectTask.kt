/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.benchmark.darwin.gradle

import java.io.File
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations

@CacheableTask
abstract class GenerateXCodeProjectTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val yamlFile: RegularFileProperty

    @get:Input
    abstract val projectName: Property<String>

    @get:OutputFile
    abstract val infoPlistPath: RegularFileProperty

    @get:OutputDirectory
    abstract val xcProjectPath: DirectoryProperty

    @TaskAction
    fun buildXCodeProject() {
        val outputFile = xcProjectPath.get().asFile
        if (outputFile.exists()) {
            require(outputFile.deleteRecursively()) {
                "Unable to delete xcode project $outputFile"
            }
        }
        val args = listOf(
            "xcodegen",
            "--spec",
            yamlFile.get().asFile.absolutePath,
            "--project",
            outputFile.parent
        )
        execOperations.exec { spec ->
            spec.commandLine = args
        }
        require(outputFile.exists()) {
            "Project $projectName must match the `name` declaration in $yamlFile"
        }
        copyProjectMetadata()
    }

    private fun copyProjectMetadata() {
        val sourceFile = File(yamlFile.get().asFile.parent, "Info.plist")
        require(sourceFile.exists())
        val targetFile = infoPlistPath.get().asFile
        val copied = sourceFile.copyRecursively(targetFile, overwrite = true)
        require(copied) {
            "Unable to copy $sourceFile to $targetFile"
        }
    }
}
