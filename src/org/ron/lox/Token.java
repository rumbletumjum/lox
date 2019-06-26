package org.ron.lox;

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
  public String toString() {
    final StringBuilder sb = new StringBuilder("Token{");
    sb.append("type=").append(type);
    sb.append(", lexeme='").append(lexeme).append('\'');
    sb.append(", literal=").append(literal);
    sb.append('}');
    return sb.toString();
  }
}
