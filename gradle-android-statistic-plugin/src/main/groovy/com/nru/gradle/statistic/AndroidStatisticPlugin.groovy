package com.nru.gradle.statistic

import org.gradle.api.Plugin
import org.gradle.api.Project

public class AndroidStatisticPlugin implements Plugin<Project> {


    @Override
    void apply(Project project) {
        project.configurations.create('findbugs');
        project.configurations.create('findbugsPlugins')

        project.task('findbugs', type: AndroidFindBugsTask)
//        Task pmdTask = project.task('pmd') << pmdClos
//        Task checkstyleTask = project.task('checkStyle') << checkStyleClos
    }

}
