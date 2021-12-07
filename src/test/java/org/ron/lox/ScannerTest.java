package org.ron.lox;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class ScannerTest {

  @Test
  public void scanTokens() {
    var source = "({";

    Scanner s = new Scanner(source);

    List<Token> tokens = s.scanTokens();

    List<Token> expectedTokens =
        Arrays.asList(
            new Token(TokenType.LEFT_PAREN, "(", null, 1),
            new Token(TokenType.LEFT_BRACE, "{", null, 1),
            new Token(TokenType.EOF, "", null, 1));

    assertThat(tokens, equalTo(expectedTokens));
  }
}
