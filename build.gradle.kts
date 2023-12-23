// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.jetbrainsKotlinPluginParcelize) apply false
    alias(libs.plugins.jetbrainsKotlinPluginSerialization) apply false
    alias(libs.plugins.androidxNavigationSafeargs) apply false
    alias(libs.plugins.googleDaggerHilt) apply false
    alias(libs.plugins.googleDevtoolsKsp) apply false
    alias(libs.plugins.composeCompiler) apply false
}