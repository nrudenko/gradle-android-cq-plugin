package com.nru.gradle.statistic

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.publish.maven.MavenDependency
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * See parameters at http://findbugs.sourceforge.net/manual/anttask.html
 */
class AndroidFindBugsTask extends DefaultTask {

//    @InputFile
//    @Optional
//    File excludeFile = new File("$project.rootDir/config/findbugs/exclude.xml")
//    @InputFile
//    @Optional
//    File xslFile = new File("$project.rootDir/config/findbugs/default.xsl")
    @InputFile
    @Optional
    File excludeFile;
    @InputFile
    @Optional
    File xslFile;

    @OutputFile
    File outputFile = new File("$project.buildDir/reports/findbugs/findbugs-${project.name}.xml")
    FileCollection findbugsClasspath = project.configurations.findbugs
    FileCollection pluginClasspath = project.configurations.findbugsPlugins
    Boolean ignoreFailures = false
    Project gradleProject = project
    String errorProp = 'findbugsError'
    String warningsProp = 'findbugsWarnings'

    def AndroidFindBugsTask() {
        description = 'Runs FindBugs against Android sourcesets.'
        group = 'Code Quality'
        dependsOn 'assemble'
        dependsOn 'assembleTest'
        dependsOn 'assembleDebug'
    }

    @TaskAction
    def findBugs() {
        excludeFile = new File(getClass().getResource("findbugs/default.xsl").text)
        xslFile = new File(getClass().getResource("findbugs/exclude.xml").text);

//        project.afterEvaluate {
            project.dependencies.add('compile', 'com.google.code.findbugs:annotations:2.0.0')
            project.dependencies.add('findbugs', 'com.google.code.findbugs:findbugs-ant:2.0.2')
//        }

        outputFile.parentFile.mkdirs()
        def antBuilder = services.get(IsolatedAntBuilder)
        antBuilder.withClasspath(findbugsClasspath).execute {
            ant.taskdef(name: 'findbugs', classname: 'edu.umd.cs.findbugs.anttask.FindBugsTask')
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