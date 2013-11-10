package com.nru.gradle.statistic

import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
/**
 * See parameters at http://findbugs.sourceforge.net/manual/anttask.html
 */
class AndroidFindBugsTask extends BaseStatisticTask {
    public static final String excludeFilePath = "findbugs/default.xsl"
    public static final String findbugTaskClassname = 'edu.umd.cs.findbugs.anttask.FindBugsTask'
    @InputFile
    @Optional
    File excludeFile;

    FileCollection findbugsClasspath = project.configurations.findbugs
    FileCollection pluginClasspath = project.configurations.findbugsPlugins
    Boolean ignoreFailures = true
    String errorProp = 'findbugsError'
    String warningsProp = 'findbugsWarnings'

    @Override
    String getXslFilePath() {
        return "findbugs/default.xsl"
    }

    @Override
    String getOutputPath() {
        return "$project.buildDir/reports/findbugs/findbugs-${project.name}.xml"
    }

    def AndroidFindBugsTask() {
        description = 'Runs FindBugs against Android sourcesets.'
        group = 'Code Quality'
        dependsOn 'assemble'
        dependsOn 'assembleTest'
        dependsOn 'assembleDebug'
    }

    @TaskAction
    def findBugs() {
        project.dependencies.add('compile', 'com.google.code.findbugs:annotations:2.0.0')
        project.dependencies.add('findbugs', 'com.google.code.findbugs:findbugs-ant:2.0.2')

        createOutputFileIfNeeded();
        getXlsFile()
        excludeFile = getDefaultFileIfNeeded(excludeFile, excludeFilePath)

        def antBuilder = services.get(IsolatedAntBuilder)
        antBuilder.withClasspath(findbugsClasspath).execute {
            ant.taskdef(name: 'findbugs', classname: findbugTaskClassname)
            ant.findbugs(debug: 'true',
                    errorProperty: errorProp,
                    warningsProperty: warningsProp,
                    output: 'xml:withMessages',
                    outputFile: outputFile,
                    excludeFilter: excludeFile,
                    jvmargs: '-Xmx768M') {
                findbugsClasspath.addToAntBuilder(ant, 'classpath')
                pluginClasspath.addToAntBuilder(ant, 'pluginList')
                auxclassPath(path: gradleProject.configurations.compile.asPath)
                gradleProject.android.sourceSets*.java.srcDirs.each { srcDir ->
                    sourcePath(path: srcDir)
                }
                "class"(location: "$gradleProject.buildDir/classes")
            }

            if (ant.project.properties[errorProp]) {
                throw new GradleException("FindBugs encountered an error. Run with --debug to get more information.")
            }

            if (ant.project.properties[warningsProp] && !ignoreFailures) {
                if (outputFile) {
                    throw new GradleException("FindBugs rule violations were found. See the report at ${outputFile}.")
                } else {
                    throw new GradleException("FindBugs rule violations were found.")
                }
            }

            if (outputFile.exists() && xslFile != null && xslFile.exists()) {
                ant.xslt(in: outputFile,
                        style: xslFile,
                        out: outputFile.absolutePath.replaceFirst(~/\.[^\.]+$/, ".html")
                )
            }

        }
    }
}