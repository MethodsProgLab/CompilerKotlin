package ru.sacmi5.compiler

enum class TokenType {
    Keyword, Type, Identifier, Delimiter, Colon, Semicolon, Unknown
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
                if (char.isLetter()) {
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
                } else {
                    tokens.add(Token(TokenType.Unknown, char.toString(), index))
                }
            }

        }
    }

    return tokens
}


fun analyze(state: State, token: Token): State {
    when (state) {
        State.Start -> {
            if (token.value == "var") return State.Var
            else throw IllegalArgumentException("Expected: keyword 'var', got '${token.value}' at position ${token.index}")
        }
        State.Var, State.Delimiter -> {
            if (token.type == TokenType.Identifier) return State.Identifier
            else throw IllegalArgumentException("Expected: type <Identifier>, got <${token.type}> as position ${token.index}")
        }
        State.Identifier -> {
            return when (token.type) {
                TokenType.Colon -> State.Colon
                TokenType.Delimiter -> State.Delimiter
                else -> throw IllegalArgumentException("Expected: colon or semicolon, got <${token.type}> as position ${token.index}")
            }
        }
        State.Colon -> return when (token.type) {
            TokenType.Type -> State.Type
            else -> throw IllegalArgumentException("Expected: type of variable, got <${token.type}> as position ${token.index}")
        }
        State.Type -> return when (token.type) {
            TokenType.Semicolon -> State.Semicolon
            else -> throw IllegalArgumentException("Expected: semicolon, got <${token.type}> as position ${token.index}")
        }
        State.Semicolon -> return when (token.type) {
            TokenType.Identifier -> State.Identifier
            else -> throw IllegalArgumentException("Expected: type <Identifier>, got <${token.type}> as position ${token.index}")
        }
    }
}