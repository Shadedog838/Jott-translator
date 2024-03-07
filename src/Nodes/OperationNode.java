package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

public class OperationNode implements JottTree {

  Token operationToken;

  public OperationNode(Token oToken) {
    this.operationToken = oToken;
  }

  @Override
  public String convertToJott() {
    StringBuilder result = new StringBuilder();
    result.append(this.operationToken.getToken());
    return result.toString();
  }

  @Override
  public String convertToJava(String className) {
    return operationToken.getToken();
  }

  @Override
  public String convertToC() {
    return operationToken.getToken();
  }

  @Override
  public String convertToPython() {
    return operationToken.getToken();
  }

  @Override
  public boolean validateTree() {
    return true;
  }

  static OperationNode parseOpNode(ArrayList<Token> tokens) throws ErrorPrinter {
    if (tokens.get(0).getTokenType() != TokenType.MATH_OP && tokens.get(0).getTokenType() != TokenType.REL_OP) {
      throw new ErrorPrinter("Not a valid operation node", tokens.get(0).getFilename(),tokens.get(0).getLineNum(), ErrorType.SYNTAX);
    }
    return new OperationNode(tokens.remove(0));
  }

}
