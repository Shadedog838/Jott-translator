package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.Token;
import provided.TokenType;

public class StringLiteralNode implements ExpressionNode {

  Token stringToken;

  public StringLiteralNode(Token strToken) {
    this.stringToken = strToken;
  }

  @Override
  public String convertToJott() {
    StringBuilder result = new StringBuilder();
    result.append(stringToken.getToken());
    return result.toString();
  }

  @Override
  public String convertToJava(String className) {
    return stringToken.getToken();
  }

  @Override
  public String convertToC() {
    return stringToken.getToken();
  }

  @Override
  public String convertToPython() {
    return stringToken.getToken();
  }

  @Override
  public boolean validateTree() {
    return true;
  }

  static StringLiteralNode parseStringLiteralNode(ArrayList<Token> tokens)
      throws ErrorPrinter {
    if (tokens.get(0).getTokenType() != TokenType.STRING) {
      throw new ErrorPrinter("Not a valid string literal node", tokens.get(0).getFilename(),
          tokens.get(0).getLineNum(), ErrorType.SYNTAX);
    }
    return new StringLiteralNode(tokens.remove(0));
  }

  @Override
  public boolean isInteger() throws ErrorPrinter {
    throw new ErrorPrinter("String is not a Integer", stringToken.getFilename(), stringToken.getLineNum(), ErrorType.SEMANTIC);
  }

  @Override
  public boolean isString() {
    return true;
  }

  @Override
  public boolean isBoolean() throws ErrorPrinter {
    throw new ErrorPrinter("String is not a Boolean", stringToken.getFilename(), stringToken.getLineNum(), ErrorType.SEMANTIC);
  }

  @Override
  public boolean isDouble() throws ErrorPrinter {
    throw new ErrorPrinter("String is not a Double", stringToken.getFilename(), stringToken.getLineNum(), ErrorType.SEMANTIC);
  }

  @Override
  public String getType() {
    return "String";
  }
}
