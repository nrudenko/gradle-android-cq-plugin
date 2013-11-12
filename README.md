THANKS FOR GREAT WORK!!!
=========
https://github.com/sethrylan/rosette.git



gradle-android-cq-plugin
===============================

Gradle plugin for running tasks PMD, CPD, Findbugs, Checkstyle with android project

`findbugs,
pmd,
cpd,
checkstyle`


Usage:
------

Add the plugin to your `buildscript`'s `dependencies` section:
```groovy
classpath 'com.github.nrudenko:gradle-android-cq-plugin:0.1+'
```

Apply the `android-cq` plugin:
```groovy
apply plugin: 'android-cq'
```

Run:
`./gradlew clean findbugs pmd cpd checkstyle`

results will be placed in `build/reports dir`

Config:
-------
For configs tasks use folder `cq-config` which will be created in project root dir after first tasks runing.
In `cq-config` will be placed main configs(like ruleset, exclude etc.) and xsl files. 
For customizing tasks just replace appropriate config in task's folder.
