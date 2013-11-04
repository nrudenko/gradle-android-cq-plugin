package com.nru.gradle.statistic
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
/**
 * See parameters at http://checkstyle.sourceforge.net/anttask.html
 */
class AndroidCheckstyleTask extends DefaultTask {
    @InputFile
    @Optional
    File configFile
    @InputFile
    @Optional
    File xslFile
    @OutputFile
    File outputFile = new File("$project.buildDir/reports/checkstyle/checkstyle-${project.name}.xml")
    FileCollection checkstyleClasspath = project.configurations.codequality
    Boolean ignoreFailures = true
    Boolean showViolations = false
    Project gradleProject = project

    def AndroidCheckstyleTask() {
        description = 'Runs checkstyle against Android sourcesets.'
        group = 'Code Quality'
    }

    @TaskAction
    def runCheckstyle() {
        println(getClass().getResource("findbugs/exclude.xml").text)
//        println(getClass().getResource("checkstyle/checkstyle-noframes-sorted.xsl").text)
//        println(getClass().getResource("checkstyle/checkstyle.xml").text)

//        println(getClass().getResource("checkstyle/checkstyle-noframes-sorted.xsl").text)
//        xslFile = new File(getClass().getResource("checkstyle/checkstyle-noframes-sorted.xsl").text)
//        configFile = new File(getClass().getResource("checkstyle/checkstyle.xml").text)
//
//        project.dependencies.add('codequality', 'com.puppycrawl.tools:checkstyle:5.6')
//
//        outputFile.parentFile.mkdirs()
//
//        def antBuilder = services.get(IsolatedAntBuilder)
//        antBuilder.withClasspath(checkstyleClasspath).execute {
//            ant.taskdef(name: 'checkstyle', classname: 'com.puppycrawl.tools.checkstyle.CheckStyleTask')
//            // see also, maxWarnings and failureProperty arguments
//            ant.checkstyle(config: configFile, failOnViolation: !ignoreFailures) {
//                fileset(dir: gradleProject.projectDir.getPath()) {
//                    gradleProject.android.sourceSets.each { sourceSet ->
//                        sourceSet.java.each { file ->
//                            include(name: gradleProject.relativePath(file))
//                        }
//                    }
//                }
//                if (showViolations) {
//                    formatter(type: 'plain', useFile: false)
//                }
//                formatter(type: 'xml', toFile: outputFile)
//            }
//
//            if (outputFile.exists() && xslFile != null && xslFile.exists()) {
//                ant.xslt(in: outputFile,
//                        style: xslFile,
//                        out: outputFile.absolutePath.replaceFirst(~/\.[^\.]+$/, ".html")
//                )
//            }
//        }
    }
}