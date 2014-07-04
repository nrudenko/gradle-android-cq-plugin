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
        xslFile = getFileFromConfigCache(getXslFilePath())
    }

    File prepareTaskFiles() {
        getXlsFile()
        if (outputFile == null || !outputFile.exists()) {
            outputFile = new File(getOutputPath())
            outputFile.parentFile.mkdirs()
        }
        outputFile
    }

    File getFileFromConfigCache(String path) {
        def String pathCache = defaultConfigPath + path;
        File file = new File(pathCache)
        if (file == null || !file.exists()) {

            file.parentFile.mkdirs()
            file.createNewFile()

            this.getClass().getResource(path).withInputStream { ris ->
                new File(pathCache).withOutputStream { fos ->
                    fos << ris
                }
            }
        }
        file
    }

    def applyToFileSet(Closure func){
        gradleProject.android.sourceSets.each { sourceSet ->
            sourceSet.javaDirectories.each { dir ->
                if (dir.exists()) {
                    dir.eachDirRecurse() { subDir ->
                        subDir.eachFileMatch(~/.*.java/) { file ->
                            func(file)
                        }
                    }
                }
            }
        }
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
