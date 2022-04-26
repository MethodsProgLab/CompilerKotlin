package ru.sacmi5.compiler

import javafx.geometry.Orientation
import tornadofx.*

class CompilerView: View() {
    private val controller: CompilerController by inject()

    init {
        title = "Компилятор"
    }

    override val root = vbox {
        menubar {
            menu("Файл") {
                item("Создать") {
                    action {
                        controller.createFile()
                    }
                }
                item("Открыть") {
                    action {
                        controller.openFile()
                    }
                }
                item("Сохранить") {
                    action {
                        controller.saveFile()
                    }
                }
                item("Сохранить как") {
                    action {
                        controller.saveAsFile()
                    }
                }
            }
            menu("Правка") {
                item("Отменить") {
                    action {
                        controller.undo()
                    }
                }
                item("Вырезать") {
                    action {
                        controller.redo()
                    }
                }
                item("Копировать") {
                    action {
                        controller.copy()
                    }
                }
                item("Вставить") {
                    action {
                        controller.cut()
                    }
                }
                item("Повторить") {
                    action {
                        controller.paste()
                    }
                }
                item("Удалить") {
                    action {
                        controller.clear()
                    }
                }
                item("Выделить все") {
                    action {
                        controller.selectAll()
                    }
                }
            }
            menu("Действия") {
                item("Пример") {
                    action {
                        controller.sample()
                    }
                }
                item("Пример с ошибками") {
                    action {
                        controller.sampleWithErrors()
                    }
                }
                item("Запустить") {
                    action {
                        controller.run()
                    }
                }
            }
        }

        hbox(spacing = 6) {
            // создать
            button("\uf15b") {
                action { controller.createFile() }
            }

            // открыть
            button("\uf07c") {
                action { controller.openFile() }
            }

            // сохранить
            button("\uf0c7") {
                action { controller.saveFile() }
            }

            // отменить
            button("\uf0e2") {
                action { controller.undo() }
            }

            // повторить
            button("\uf01e") {
                action { controller.redo() }
            }

            // копировать
            button("\uf0c5") {
                action { controller.copy() }
            }

            // вырезать
            button("\uf0c4") {
                action { controller.cut() }
            }

            // вставить
            button("\uf0c5") {
                action { controller.paste() }
            }

            style {
                padding = box(6.px)
            }

            val boxPx = 32.px

            children.style {
                minWidth = boxPx
                maxWidth = boxPx
                minHeight = boxPx
                maxHeight = boxPx
                fontFamily = "FontAwesome"
            }
        }
        splitpane(Orientation.VERTICAL) {
            controller.textArea = textarea {
                textProperty().addListener { _, _, _ ->
                    controller.textChanged()
                }
            }
            controller.errorList = listview {
                onDoubleClick { controller.clickOnError() }
            }
        }
    }
}