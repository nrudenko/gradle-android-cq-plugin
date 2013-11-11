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
classpath 'com.github.nrudenko:gradle-android-cq-plugin:0.0.1-SNAPSHOT'
```

Apply the `android-cq` plugin:
```groovy
apply plugin: 'android-cq'
```

Run:
`./gradlew clean findbugs cmd cpd checkstyle`

results will be placed in build/reports dir

Config:
-------
For config tasks use folder `cq-config` which will be created after first tasks runing in project dir.
In `cq-config` will be placed main configs(like ruleset, exclude etc.) and xslt files. 
For customize just replace needed config in needed folder.