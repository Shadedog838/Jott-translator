package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.Token;
import provided.TokenType;

public class NumberNode implements ExpressionNode {

  Token numNode;

  public NumberNode(Token nNode) {
    this.numNode = nNode;
  }

  @Override
  public String convertToJott() {
    StringBuilder result = new StringBuilder();
    result.append(this.numNode.getToken());
    return result.toString();
  }

  @Override
  public String convertToJava(String className) {
    return numNode.getToken();
  }

  @Override
  public String convertToC() {
    return numNode.getToken();
  }

  @Override
  public String convertToPython() {
    return numNode.getToken();
  }

  @Override
  public boolean validateTree() {
    return true;
  }

  static NumberNode parseNumberNode(ArrayList<Token> tokens) throws ErrorPrinter {
    if (tokens.get(0).getTokenType() != TokenType.NUMBER) {
      throw new ErrorPrinter("Not a valid number node", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
    }
    return new NumberNode(tokens.remove(0));
  }

  @Override
  public boolean isInteger() throws ErrorPrinter {
    if (numNode.getToken().contains(".")) {
      throw new ErrorPrinter("Number is not an integer", numNode.getFilename(), numNode.getLineNum(), ErrorType.SEMANTIC);
    }
    return true;
  }

  @Override
  public boolean isString() throws ErrorPrinter {
    throw new ErrorPrinter("Number is not an integer", numNode.getFilename(), numNode.getLineNum(), ErrorType.SEMANTIC);
  }

  @Override
  public boolean isBoolean() throws ErrorPrinter {
    throw new ErrorPrinter("Number is not an integer", numNode.getFilename(), numNode.getLineNum(), ErrorType.SEMANTIC);
  }

  @Override
  public boolean isDouble() throws ErrorPrinter {
    if (!numNode.getToken().contains(".")) {
      throw new ErrorPrinter("Number is not a double", numNode.getFilename(), numNode.getLineNum(), ErrorType.SEMANTIC);
    }
    return true;
  }

  @Override
  public String getType() {
    return numNode.getToken().contains(".") ? "Double" : "Integer";
  }

}
