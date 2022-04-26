package ru.sacmi5.compiler

import tornadofx.Stylesheet
import tornadofx.px

class CompilerViewStyles: Stylesheet() {
    init {
        Companion.root {
            prefWidth = 800.px
            prefHeight = 600.px
        }
    }
}