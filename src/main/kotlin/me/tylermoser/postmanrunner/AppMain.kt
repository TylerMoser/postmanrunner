package me.tylermoser.postmanrunner

import me.tylermoser.postmanrunner.style.AppStylesheet
import me.tylermoser.postmanrunner.view.MainView
import tornadofx.*

/**
 * The main entry point for the application. If needed, additional configuration can be added here.
 */
class AppMain: App(MainView::class, AppStylesheet::class)
