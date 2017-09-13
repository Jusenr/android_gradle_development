package com.jusenr.android.gradle.exten

public class ComExtension {

    /**
     * Automatic registration of components, and true automatically registers code in the form of bytecode insertion.
     * 'false' needs to be registered manually using reflection.
     */
    boolean isRegisterCompoAuto = false;

    /**
     * The applicatonName of the current component, used for bytecode insertion.
     * When 'isRegisterCompoAuto==true' is necessary.
     */
    String applicatonName

}