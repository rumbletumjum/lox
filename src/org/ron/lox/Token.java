package org.ron.lox;

import java.util.Objects;

class Token {
  final TokenType type;
  final String lexeme;
  final Object literal;
  final int line;

  Token(TokenType type, String lexeme, Object literal, int line) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Token token = (Token) o;
    return line == token.line
        && type == token.type
        && lexeme.equals(token.lexeme)
        && Objects.equals(literal, token.literal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, lexeme, literal, line);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Token{");
    sb.append("type=").append(type);
    sb.append(", lexeme='").append(lexeme).append('\'');
    sb.append(", literal=").append(literal);
    sb.append('}');
    return sb.toString();
  }
}
