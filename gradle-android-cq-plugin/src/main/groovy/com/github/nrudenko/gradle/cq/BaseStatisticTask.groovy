package com.github.nrudenko.gradle.cq

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile

abstract class BaseStatisticTask extends DefaultTask {

    Project gradleProject = project

    final def defaultConfigPath = "${gradleProject.projectDir}/cq-configs/"

    @InputFile
    @Optional
    protected File xslFile

    @OutputFile
    @Optional
    File outputFile

    File getXlsFile() {
        xslFile = getDefaultFileIfNeeded(xslFile, getXslFilePath())
    }

    File createOutputFileIfNeeded() {
        if (outputFile == null || !outputFile.exists()) {
            outputFile = new File(getOutputPath())
            outputFile.parentFile.mkdirs()
        }
        outputFile
    }

    File getDefaultFileIfNeeded(File file, String pathFromRes) {
        if (file == null || !file.exists()) {
            def String pathCache = defaultConfigPath + pathFromRes;

            file = new File(pathCache)
            file.parentFile.mkdirs()
            file.createNewFile()

            this.getClass().getResource(pathFromRes).withInputStream { ris ->
                new File(pathCache).withOutputStream { fos ->
                    fos << ris
                }
            }
        }
        file
    }

    void makeHtml(def ant) {
        if (outputFile.exists() && xslFile != null && xslFile.exists()) {
            ant.xslt(in: outputFile,
                    style: xslFile,
                    out: outputFile.absolutePath.replaceFirst(~/\.[^\.]+$/, ".html")
            )
        }
    }

    abstract String getXslFilePath()

    abstract String getOutputPath()
}
