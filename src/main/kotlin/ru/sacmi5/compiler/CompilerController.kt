package ru.sacmi5.compiler

import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File


class CompilerController : Controller() {
    var textArea: TextArea by singleAssign()
    var errorList: ListView<String> by singleAssign()

    private var path = ""
    private var isTextChanged = false
    private val errorsIndex = mutableListOf<ErrorRange>()

    fun clear() {
        textArea.text = ""
        clearErrors()
    }

    fun undo() {
        textArea.undo()
    }

    fun redo() {
        textArea.redo()
    }

    fun cut() {
        textArea.cut()
    }

    fun copy() {
        textArea.copy()
    }

    fun paste() {
        textArea.paste()
    }

    fun selectAll() {
        textArea.selectAll()
    }

    private fun addError(error: String) {
        errorList.items.add(error)
    }

    private fun clearErrors() {
        errorList.items.clear()
    }

    fun createFile() {
        if (isTextChanged) {
            val alert = Alert(
                AlertType.CONFIRMATION, "Сохранить изменения в файле?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL
            )
            alert.showAndWait()

            when (alert.result) {
                ButtonType.YES -> saveFile()
                ButtonType.CANCEL -> return
            }
        }

        clear()
        isTextChanged = false
    }

    fun openFile() {
        val fileChooser = FileChooser()
        fileChooser.title = "Открыть файл"

        val file = fileChooser.showOpenDialog(primaryStage)
        if (file != null) {
            path = file.absolutePath
            textArea.text = file.readText()
            isTextChanged = false
        }
    }

    fun saveFile() {
        if (path.isEmpty()) {
            saveAsFile()
        } else {
            File(path).writeText(textArea.text)
            isTextChanged = false
        }
    }

    fun saveAsFile() {
        val fileChooser = FileChooser()
        fileChooser.title = "Сохранить файл"

        fileChooser.showSaveDialog(primaryStage)?.let {
            path = it.absolutePath
            File(path).writeText(textArea.text)
        }

        isTextChanged = false
    }

    fun textChanged() {
        isTextChanged = true
    }

    fun sample() {
        clear()
        textArea.text = """
            var
                testInteger: integer;
                bool, bool2: boolean;
                reel: real;
        """.trimIndent()
    }

    fun sampleWithErrors() {
        clear()
        textArea.text = """
            real
                testInteger: var;
                bool, bool2: boolean;
                integer: real;
        """.trimIndent()
    }

    fun clickOnError() {
        val errorIndex = errorList.selectionModel.selectedIndex
        if (errorIndex >= errorsIndex.size || errorIndex < 0)
            return

        val errorRange = errorsIndex[errorIndex]
        textArea.selectRange(errorRange.start, errorRange.end)
    }

    fun run() {
        clearErrors()

        val code = textArea.text
        val tokens = scanner(code)
        val stringBuilder = StringBuilder()

        var state = State.Start
        errorsIndex.clear()

        for (i in tokens.indices) {
            state = try {
                val correctState = analyze(state, tokens[i])
                stringBuilder.append("${tokens[i].value} ")

                correctState
            } catch (e: IllegalArgumentException) {
                addError("${e.message}")
                errorsIndex.add(ErrorRange(tokens[i].index, tokens[i].index + tokens[i].value.length))
                val predictedState = predictNextState(state, if (i + 1 !in tokens.indices) null else tokens[i + 1])

                if (predictedState != state) {
                    stringBuilder.append("${predictedState.toString().lowercase()} ")
                }
                predictedState
            }

        }

        if (state != State.Semicolon) {
            addError("Декларация переменных не завершена")
        }

        if (errorsIndex.isEmpty()) {
            addError("Ошибок не найдено. Все хорошо! :)")
        }

        if (errorsIndex.isNotEmpty()) {
            addError("Ожидалось следующее:\n$stringBuilder")
        }
    }

    fun lexerScanner() {
        val code = textArea.text
        errorList.items.clear()

        val tokens = scanner(code)
        for (token in tokens) {
            errorList.items.add(token.toString())
        }
    }
}
