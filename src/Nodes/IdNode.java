package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.Token;
import provided.TokenType;

public class IdNode implements ExpressionNode {

  Token idToken;

  public IdNode(Token iToken) {
    this.idToken = iToken;
  }

  @Override
  public String convertToJott() {
    StringBuilder result = new StringBuilder();
    result.append(this.idToken.getToken());
    return result.toString();
  }

  @Override
  public String convertToJava(String className) {
    return idToken.getToken();
  }

  @Override
  public String convertToC() {
    return idToken.getToken();
  }

  @Override
  public String convertToPython() {
    return idToken.getToken();
  }

  @Override
  public boolean validateTree() {
    return true;
  }

  static IdNode parseIdNode(ArrayList<Token> tokens) throws ErrorPrinter {
    if (tokens.get(0).getTokenType() != TokenType.ID_KEYWORD) {
      throw new ErrorPrinter("Not valid operation id node", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
    }
    return new IdNode(tokens.remove(0));
  }

  @Override
  public boolean isInteger() throws ErrorPrinter {
    if (!SymTable.varTable.get(SymTable.functionName).get(idToken.getToken()).equals("Integer")) {
       throw new ErrorPrinter("Id type is not a Integer", idToken.getFilename(), idToken.getLineNum(), ErrorType.SEMANTIC);
    }
    return true;
  }

  @Override
  public boolean isString() throws ErrorPrinter {
    if (!SymTable.varTable.get(SymTable.functionName).get(idToken.getToken()).equals("String")) {
      throw new ErrorPrinter("Id type is not a String", idToken.getFilename(), idToken.getLineNum(), ErrorType.SEMANTIC);
    }
    return true;
  }

  @Override
  public boolean isBoolean() throws ErrorPrinter {
    if (!SymTable.varTable.get(SymTable.functionName).get(idToken.getToken()).equals("Boolean")) {
      throw new ErrorPrinter("Id type is not a Boolean", idToken.getFilename(), idToken.getLineNum(), ErrorType.SEMANTIC);
    }
    return true;
  }

  @Override
  public boolean isDouble() throws ErrorPrinter {
    if (!SymTable.varTable.get(SymTable.functionName).get(idToken.getToken()).equals("Double")) {
      throw new ErrorPrinter("Id type is not a Double", idToken.getFilename(), idToken.getLineNum(), ErrorType.SEMANTIC);
    }
    return true;
  }

  @Override
  public String getType() throws ErrorPrinter {
    if (SymTable.unitVars.contains(idToken.getToken())) {
      throw new ErrorPrinter(idToken.getToken() + " is uninitialized", idToken.getFilename(), idToken.getLineNum(), ErrorType.SEMANTIC);
    }
    return SymTable.varTable.get(SymTable.functionName).get(idToken.getToken());
  }

}
