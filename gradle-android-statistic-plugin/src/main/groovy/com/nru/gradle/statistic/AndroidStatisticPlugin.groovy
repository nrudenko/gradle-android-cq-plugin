package com.nru.gradle.statistic

import org.gradle.api.Plugin
import org.gradle.api.Project

public class AndroidStatisticPlugin implements Plugin<Project> {


    @Override
    void apply(Project project) {
        project.repositories.add(project.repositories.mavenCentral())

        project.configurations.create('findbugs')
        project.configurations.create('findbugsPlugins')
        project.configurations.create('codequality')

        project.task('findbugs', type: AndroidFindBugsTask)
        project.task('checkstyle', type: AndroidCheckstyleTask)
//        Task pmdTask = project.task('pmd') << pmdClos
    }

}
