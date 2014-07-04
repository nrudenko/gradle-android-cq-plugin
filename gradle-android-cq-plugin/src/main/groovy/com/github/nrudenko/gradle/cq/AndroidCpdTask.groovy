package com.github.nrudenko.gradle.cq

import org.gradle.api.file.FileCollection
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.tasks.TaskAction

class AndroidCpdTask extends BaseStatisticTask {
    static final String cpdTaskClassname = 'net.sourceforge.pmd.cpd.CPDTask';

    FileCollection codequalityClasspath = project.configurations.codequality

    @Override
    String getXslFilePath() {
        return "cpd/cpd-html.xsl"
    }

    @Override
    String getOutputPath() {
        return "$project.buildDir/reports/cpd/cpd-result.xml"
    }

    @TaskAction
    runCpd() {
        prepareTaskFiles()

        def antBuilder = services.get(IsolatedAntBuilder)
        antBuilder.withClasspath(codequalityClasspath).execute {
            ant.taskdef(name: 'cpd', classname: cpdTaskClassname)
            ant.cpd(minimumTokenCount: 100,
                    format: 'xml',
                    outputFile: outputPath
            ) {
                fileset(dir: gradleProject.projectDir.getPath()) {
                    applyToFileSet({file -> include(name: gradleProject.relativePath(file))})
                }
            }

            makeHtml(ant)
        }
    }
}