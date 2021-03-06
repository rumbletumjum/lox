package lox;

import static lox.TokenType.BANG;
import static lox.TokenType.BANG_EQUAL;
import static lox.TokenType.EOF;
import static lox.TokenType.EQUAL_EQUAL;
import static lox.TokenType.FALSE;
import static lox.TokenType.GREATER;
import static lox.TokenType.GREATER_EQUAL;
import static lox.TokenType.LEFT_PAREN;
import static lox.TokenType.LESS;
import static lox.TokenType.LESS_EQUAL;
import static lox.TokenType.MINUS;
import static lox.TokenType.NIL;
import static lox.TokenType.NUMBER;
import static lox.TokenType.PLUS;
import static lox.TokenType.PRINT;
import static lox.TokenType.RIGHT_PAREN;
import static lox.TokenType.SEMICOLON;
import static lox.TokenType.SLASH;
import static lox.TokenType.STAR;
import static lox.TokenType.STRING;
import static lox.TokenType.TRUE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(statement());
        }

        return statements;
    }

    private Stmt statement() {
        if (match(PRINT)) return printStatement();

        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        return parseBinary(this::comparison, BANG_EQUAL, EQUAL_EQUAL);
    }

    private Expr comparison() {
        return parseBinary(this::addition, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
    }

    private Expr addition() {
        return parseBinary(this::multiplication, MINUS, PLUS);
    }

    private Expr multiplication() {
        return parseBinary(this::unary, SLASH, STAR);
    }

    private Expr parseBinary(Supplier<Expr> operand, TokenType... types) {
        Expr expr = operand.get();

        while (match(types)) {
            Token operator = previous();
            Expr right = operand.get();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() {
        if (match(FALSE)) {
            return new Expr.Literal(false);
        }
        if (match(TRUE)) {
            return new Expr.Literal(true);
        }
        if (match(NIL)) {
            return new Expr.Literal(null);
        }

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }

        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) {
                return;
            }

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }
}
