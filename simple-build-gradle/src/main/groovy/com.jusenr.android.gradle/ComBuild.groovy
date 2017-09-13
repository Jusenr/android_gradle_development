package com.jusenr.android.gradle

import com.jusenr.android.gradle.exten.ComExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

public class ComBuild implements Plugin<Project> {

    //The default is app, which is equivalent to running app:assembleRelease when running assembleRelease directly.
    String compilemodule = "app"

    void apply(Project project) {
        project.gradle.addListener(new TimeListener())
        project.extensions.create('combuild', ComExtension)

        String taskNames = project.gradle.startParameter.taskNames.toString()
        System.out.println("taskNames is " + taskNames);
        String module = project.path.replace(":", "")
        System.out.println("current module is " + module);
        AssembleTask assembleTask = getTaskInfo(project.gradle.startParameter.taskNames)

        if (assembleTask.isAssemble) {
            fetchMainmodulename(project, assembleTask);
            System.out.println("compilemodule  is " + compilemodule);
        }

        if (!project.hasProperty("isRunAlone")) {
            throw new RuntimeException("you should set isRunAlone in " + module + "'s gradle.properties")
        }

        //For the case of isRunAlone==true, you need to modify its value according to the actual situation, but if it is false, you do not need to modify the module as a lib, run module:assembleRelease, then publish AAR to the central warehouse.
        boolean isRunAlone = Boolean.parseBoolean((project.properties.get("isRunAlone")))
        if (isRunAlone && assembleTask.isAssemble) {
            String mainmodulename = project.rootProject.property("mainmodulename")
            //For components and main projects to be compiled, isRunAlone is modified to true, and other components are forced to modify to false.
            //This means that the component cannot refer to the main project, as is within the hierarchy.
            if (module.equals(compilemodule) || module.equals(mainmodulename)) {
                isRunAlone = true;
            } else {
                isRunAlone = false;
            }
        }
        project.setProperty("isRunAlone", isRunAlone)

        //Add various component dependencies based on configuration, and automate the generation of components to load code.
        if (isRunAlone) {
            project.apply plugin: 'com.android.application'
            System.out.println("apply plugin is " + 'com.android.application');
            if (assembleTask.isAssemble && module.equals(compilemodule)) {
                compileComponents(assembleTask, project)
                project.android.registerTransform(new ComCodeTransform(project))
            }
        } else {
            project.apply plugin: 'com.android.library'
            System.out.println("apply plugin is " + 'com.android.library');
            project.afterEvaluate {
                Task assembleReleaseTask = project.tasks.findByPath("assembleRelease")
                if (assembleReleaseTask != null) {
                    assembleReleaseTask.doLast {
                        File infile = project.file("build/outputs/aar/$module-release.aar")
                        File outfile = project.file("../componentrelease")
                        File desFile = project.file("$module-release.aar");
                        project.copy {
                            from infile
                            into outfile
                            rename {
                                String fileName -> desFile.name
                            }
                        }
                        System.out.println("$module-release.aar copy success ");
                    }
                }
            }
        }

    }

    /**
     * According to the current task, get the components to run, the rules are as follows:
     *
     * assembleRelease ---app
     * app:assembleRelease :app:assembleRelease ---app
     * sharecomponent:assembleRelease :sharecomponent:assembleRelease ---sharecomponent
     *
     * @param assembleTask
     */
    private void fetchMainmodulename(Project project, AssembleTask assembleTask) {
        if (!project.rootProject.hasProperty("mainmodulename")) {
            throw new RuntimeException("you should set compilemodule in rootproject's gradle.properties")
        }
        if (assembleTask.modules.size() > 0 && assembleTask.modules.get(0) != null
                && assembleTask.modules.get(0).trim().length() > 0
                && !assembleTask.modules.get(0).equals("all")) {
            compilemodule = assembleTask.modules.get(0);
        } else {
            compilemodule = project.rootProject.property("mainmodulename")
        }
        if (compilemodule == null || compilemodule.trim().length() <= 0) {
            compilemodule = "app"
        }
    }

    private AssembleTask getTaskInfo(List<String> taskNames) {
        AssembleTask assembleTask = new AssembleTask();
        for (String task : taskNames) {
            if (task.toUpperCase().contains("ASSEMBLE") || task.toUpperCase().contains("RESGUARD")) {
                if (task.toUpperCase().contains("DEBUG")) {
                    assembleTask.isDebug = true;
                }
                assembleTask.isAssemble = true;
                String[] strs = task.split(":")
                assembleTask.modules.add(strs.length > 1 ? strs[strs.length - 2] : "all");
                break;
            }
        }
        return assembleTask
    }

    /**
     * Automatically add dependencies to add dependencies only when running the assemble task, so it is completely invisible between components during development, which is the key to complete isolation.
     * Support two kinds of syntax: module or modulePackage:module; the former refers to the module project, and the latter uses the AAR already published in componentrelease.
     *
     * @param assembleTask
     * @param project
     */
    private void compileComponents(AssembleTask assembleTask, Project project) {
        String components;
        if (assembleTask.isDebug) {
            components = (String) project.properties.get("debugComponent")
        } else {
            components = (String) project.properties.get("compileComponent")
        }

        if (components == null || components.length() == 0) {
            System.out.println("there is no add dependencies ");
            return;
        }
        String[] compileComponents = components.split(",")
        if (compileComponents == null || compileComponents.length == 0) {
            System.out.println("there is no add dependencies ");
            return;
        }
        for (String str : compileComponents) {
            System.out.println("comp is " + str);
            if (str.contains(":")) {
                File file = project.file("../componentrelease/" + str.split(":")[1] + "-release.aar")
                if (file.exists()) {
                    project.dependencies.add("compile", str + "-release@aar")
                    System.out.println("add dependencies : " + str + "-release@aar");
                } else {
                    throw new RuntimeException(str + " not found ! maybe you should generate a new one ")
                }
            } else {
                project.dependencies.add("compile", project.project(':' + str))
                System.out.println("add dependencies project : " + str);
            }
        }
    }

    private class AssembleTask {
        boolean isAssemble = false;
        boolean isDebug = false;
        List<String> modules = new ArrayList<>();
    }

}