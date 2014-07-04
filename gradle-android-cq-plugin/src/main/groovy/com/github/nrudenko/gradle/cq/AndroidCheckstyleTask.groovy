package com.github.nrudenko.gradle.cq

import org.gradle.api.file.FileCollection
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
/**
 * See parameters at http://checkstyle.sourceforge.net/anttask.html
 */
class AndroidCheckstyleTask extends BaseStatisticTask {
    public static
    final String checkstyleClassname = 'com.puppycrawl.tools.checkstyle.CheckStyleTask'
    public static final String configFilePath = 'checkstyle/checkstyle.xml'

    @InputFile
    @Optional
    File configFile

    FileCollection checkstyleClasspath = project.configurations.codequality
    Boolean ignoreFailures = true
    Boolean showViolations = false

    @Override
    String getXslFilePath() {
        return "checkstyle/checkstyle-html.xsl"
    }

    @Override
    String getOutputPath() {
        return "$project.buildDir/reports/checkstyle/checkstyle-result.xml"
    }

    def AndroidCheckstyleTask() {
        description = 'Runs checkstyle against Android sourcesets.'
        group = 'Code Quality'
    }

    @TaskAction
    def runCheckstyle() {
        prepareTaskFiles()
        configFile = getFileFromConfigCache(configFilePath)

        def antBuilder = services.get(IsolatedAntBuilder)
        antBuilder.withClasspath(checkstyleClasspath).execute {
            ant.taskdef(name: 'checkstyle', classname: checkstyleClassname)
            // see also, maxWarnings and failureProperty arguments
            ant.checkstyle(config: configFile, failOnViolation: !ignoreFailures) {
                fileset(dir: gradleProject.projectDir.getPath()) {
                    applyToFileSet({file -> include(name: gradleProject.relativePath(file))})
                }
                if (showViolations) {
                    formatter(type: 'plain', useFile: false)
                }
                formatter(type: 'xml', toFile: outputFile)
            }

            makeHtml(ant)
        }
    }
}