package com.github.nrudenko.gradle.cq

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
    public static final String excludeFilePath = "findbugs/exclude.xml"
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
        return "findbugs/findbugs-html.xsl"
    }

    @Override
    String getOutputPath() {
        return "$project.buildDir/reports/findbugs/findbugs-result.xml"
    }

    def AndroidFindBugsTask() {
        description = 'Runs FindBugs against Android sourcesets.'
        group = 'Code Quality'
        dependsOn 'assemble'
        dependsOn 'assembleDebug'
    }

    @TaskAction
    def runFindBugs() {

        prepareTaskFiles();
        excludeFile = getFileFromConfigCache(excludeFilePath)

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

            makeHtml(ant)
        }
    }
}