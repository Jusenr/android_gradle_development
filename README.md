# android_gradle_development #


### Using Help ###

> First Steps:

Add 'build.gradle' to the project.

    dependencies {
        ...
        classpath 'com.jusenr.gradle.plugin:simple-build-gradle:1.1.0'
        ...
    }

Add gradle.properties to the project.

    mainmodulename=app

> Second Steps:

Each build.gradle can be run independently of modules.

    apply plugin: 'com.android.application' instead of apply plugin: 'build.gradle'

    combuild {
        applicatonName = 'xxx.xxx.xxx.TotalApplication'
        isRegisterCompoAuto = true
    }

> Third Steps:

Each gradle.properties can be run independently of modules.

    isRunAlone=true
    # debugComponent=logincomponent
    # compileComponent=com.jusenr.login:logincomponent


