package ru.sacmi5.compiler

import javafx.scene.text.Font
import javafx.stage.Stage
import tornadofx.App

class CompilerApplication: App(CompilerView::class, CompilerViewStyles::class) {
    override val primaryView = CompilerView::class

    override fun start(stage: Stage) {
        Font.loadFont(javaClass.getResource("/fontawesome/font.ttf")!!.toExternalForm(), 12.0)

        with(stage) {
            isResizable = false
            super.start(this)
        }
    }
}
