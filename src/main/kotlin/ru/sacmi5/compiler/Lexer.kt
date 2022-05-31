package ru.sacmi5.compiler

enum class TokenType {
    Keyword, Type, Identifier, Delimiter, Colon, Semicolon, Digit, Unknown
}

enum class State {
    Start, Var, Identifier, Type, Delimiter, Colon, Semicolon
}

data class Token(val type: TokenType, val value: String, val index: Int)

fun isKeyword(word: String) = when (word) {
    "var" -> true
    else -> false
}

fun isType(word: String) = when (word) {
    "integer", "real", "boolean", "char" -> true
    else -> false
}

fun predictNextState(state: State, nextToken: Token?) = when (state) {
    State.Start -> State.Var
    State.Var -> State.Identifier
    State.Identifier -> if (nextToken != null) when (nextToken.type) {
        TokenType.Identifier -> State.Delimiter
        TokenType.Type -> State.Colon
        else -> state
    } else state
    State.Type -> State.Semicolon
    State.Delimiter -> State.Identifier
    State.Colon -> State.Type
    State.Semicolon -> State.Identifier
}

fun scanner(str: String): List<Token> {
    val tokens = mutableListOf<Token>()
    var toSkip = 0

    for ((index, char) in str.withIndex()) {
        if (toSkip > 0) {
            toSkip--
            continue
        }

        when (char) {
            ' ', '\t', '\n', '\r' -> {}
            ':' -> tokens.add(Token(TokenType.Colon, char.toString(), index))
            ',' -> tokens.add(Token(TokenType.Delimiter, char.toString(), index))
            ';' -> tokens.add(Token(TokenType.Semicolon, char.toString(), index))
            else -> {
                when {
                    char.isLetter() -> {
                        str.substring(index).takeWhile { it.isLetterOrDigit() }.let {
                            toSkip += it.length - 1

                            val tokenType = when {
                                isType(it) -> TokenType.Type
                                isKeyword(it) -> TokenType.Keyword
                                else -> TokenType.Identifier
                            }

                            tokens.add(
                                Token(
                                    tokenType, it, index
                                )
                            )
                        }
                    }
                    char.isDigit() -> {
                        tokens.add(Token(TokenType.Digit, char.toString(), index))
                    }
                    else -> {
                        tokens.add(Token(TokenType.Unknown, char.toString(), index))
                    }
                }
            }

        }
    }

    return tokens
}

fun getErrorMessage(expected: String, got: TokenType, position: Int): Nothing {
    throw IllegalArgumentException("Ожидал: $expected, получил: <$got> на позиции в строке ${position + 1}")
}


fun analyze(state: State, token: Token): State {
    when (state) {
        State.Start -> {
            if (token.value == "var") return State.Var
            else getErrorMessage("ключевое слово 'var'", token.type, token.index)
        }
        State.Var, State.Delimiter -> {
            if (token.type == TokenType.Identifier) return State.Identifier
            else getErrorMessage("идентификатор", token.type, token.index)
        }
        State.Identifier -> {
            return when (token.type) {
                TokenType.Colon -> State.Colon
                TokenType.Delimiter -> State.Delimiter
                else -> getErrorMessage("двоеточие или точка с запятой", token.type, token.index)
            }
        }
        State.Colon -> return when (token.type) {
            TokenType.Type -> State.Type
            else -> getErrorMessage("тип переменной", token.type, token.index)
        }
        State.Type -> return when (token.type) {
            TokenType.Semicolon -> State.Semicolon
            else -> getErrorMessage("точка с запятой", token.type, token.index)
        }
        State.Semicolon -> return when (token.type) {
            TokenType.Identifier -> State.Identifier
            else -> getErrorMessage("идентификатор", token.type, token.index)
        }
    }
}

