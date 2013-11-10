package com.nru.gradle.statistic

import org.gradle.api.file.FileCollection
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.tasks.TaskAction

class AndroidCpdTask extends BaseStatisticTask {
    static final String cpdTaskClassname = 'net.sourceforge.pmd.cpd.CPDTask';

    FileCollection codequalityClasspath = project.configurations.codequality

    @Override
    String getXslFilePath() {
        return "cpd/cpdhtml.xslt"
    }

    @Override
    String getOutputPath() {
        return "$project.buildDir/reports/cpd/cpd-${project.name}.xml"
    }

    @TaskAction
    runCpd() {
        project.dependencies.add('codequality', 'pmd:pmd:4.2.6')

        createOutputFileIfNeeded()
        getXlsFile()

        def antBuilder = services.get(IsolatedAntBuilder)
        antBuilder.withClasspath(codequalityClasspath).execute {
            ant.taskdef(name: 'cpd', classname: cpdTaskClassname)
            ant.cpd(minimumTokenCount: 100,
                    format: 'xml',
                    outputFile: outputPath
            ) {
                fileset(dir: gradleProject.projectDir.getPath()) {
                    gradleProject.android.sourceSets.each { sourceSet ->
                        sourceSet.java.each { file ->
                            include(name: gradleProject.relativePath(file))
                        }
                    }
                }
            }

            if (outputFile.exists() && xslFile != null && xslFile.exists()) {
                ant.xslt(in: outputFile,
                        style: xslFile,
                        out: outputFile.absolutePath.replaceFirst(~/\.[^\.]+$/, ".html"))
            }
        }
    }
}