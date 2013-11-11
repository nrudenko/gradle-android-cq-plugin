package com.github.nrudenko.gradle.cq

import org.gradle.api.Plugin
import org.gradle.api.Project

public class AndroidCQPlugin implements Plugin<Project> {


    @Override
    void apply(Project project) {
        project.repositories.add(project.repositories.mavenCentral())

        project.configurations.create('findbugs')
        project.configurations.create('findbugsPlugins')
        project.configurations.create('codequality')

        project.dependencies.add('compile', 'com.google.code.findbugs:annotations:2.0.0')
        project.dependencies.add('findbugs', 'com.google.code.findbugs:findbugs-ant:2.0.2')
        project.dependencies.add('codequality', 'com.puppycrawl.tools:checkstyle:5.6')
        project.dependencies.add('codequality', 'pmd:pmd:4.2.6')

        project.task('findbugs', type: AndroidFindBugsTask)
        project.task('checkstyle', type: AndroidCheckstyleTask)
        project.task('pmd', type: AndroidPmdTask)
        project.task('cpd', type: AndroidCpdTask)
    }

}
