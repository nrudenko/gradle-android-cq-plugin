package com.nru.gradle.statistic

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile

abstract class BaseStatisticTask extends DefaultTask {
    @InputFile
    @Optional
    protected File xslFile

    @OutputFile
    @Optional
    File outputFile

    Project gradleProject = project

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

    abstract String getXslFilePath()

    abstract String getOutputPath()

    File getDefaultFileIfNeeded(File file, String pathFromRes) {
        if (file == null || !file.exists()) {
            def String pathCache = project.gradle.gradleUserHomeDir.path + pathFromRes;

            file = new File(pathCache)
            file.parentFile.mkdirs()
            file.createNewFile()

            this.getClass().getResource(pathFromRes).withInputStream { ris ->
                new File(pathCache).withOutputStream { fos ->
                    fos << ris
                }
            }
        }else{
            println("ALREADY OK "+file.path)
        }
        file
    }
}
