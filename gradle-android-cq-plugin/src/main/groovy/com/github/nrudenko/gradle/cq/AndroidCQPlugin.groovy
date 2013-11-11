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

        project.task('findbugs', type: AndroidFindBugsTask)
        project.task('checkstyle', type: AndroidCheckstyleTask)
        project.task('pmd', type: AndroidPmdTask)
        project.task('cpd', type: AndroidCpdTask)
    }

}
