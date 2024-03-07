package Nodes;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import java.util.ArrayList;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

public class ParamsNode implements JottTree {

  ExpressionNode eNode;
  ArrayList<ParamsTNode> pTNodes;

  public ParamsNode(ExpressionNode eNode, ArrayList<ParamsTNode> pTNodes) {
    this.eNode = eNode;
    this.pTNodes = pTNodes;
  }

  @Override
  public String convertToJott() {
    StringBuilder result = new StringBuilder();
    if (this.eNode == null && this.pTNodes == null) {
      result.append("");
    } else {
      result.append(eNode.convertToJott());
      while (pTNodes.size() > 0) {
        result.append(pTNodes.get(0).convertToJott());
        pTNodes.remove(0);
      }
    }
    return result.toString();
  }

  @Override
  public String convertToJava(String className) {
    if (eNode != null && pTNodes != null) {
      StringBuilder result = new StringBuilder();
      result.append(eNode.convertToJava(className));

      for (ParamsTNode paramsTNode : pTNodes) {
        result.append(paramsTNode.convertToJava(className));
      }

      return result.toString();
    }

    return "";
  }

  @Override
  public String convertToC() {
    if (eNode != null && pTNodes != null) {
      StringBuilder result = new StringBuilder();
      result.append(eNode.convertToC());

      for (ParamsTNode pNode : pTNodes) {
        result.append(pNode.convertToC());
      }

      return result.toString();
    }

    return "";
  }

  @Override
  public String convertToPython() {
    if (eNode != null) {
      StringBuilder result = new StringBuilder();
      result.append(eNode.convertToPython());

      for (ParamsTNode paramsTNode : pTNodes) {
        result.append(paramsTNode.convertToPython());
      }
      
      return result.toString();
    }
    return "";
  }

  @Override
  public boolean validateTree() throws ErrorPrinter {
    if (eNode != null && pTNodes != null) {
      eNode.validateTree();
      for (ParamsTNode paramsTNode : pTNodes) { // validate every single param
        paramsTNode.validateTree();
      }
    }
    return true;
  }

  static ParamsNode parseParamsNode(ArrayList<Token> tokens)
    throws ErrorPrinter {
    if (tokens.get(0).getTokenType() == TokenType.R_BRACKET) {
      return new ParamsNode(null, null);
    }

    ExpressionNode eNode = ExpressionNode.parseExpressionNode(tokens);

    if (eNode == null) {
      throw new ErrorPrinter(
        "Not a valid expression node",
        tokens.get(0).getFilename(),
        tokens.get(0).getLineNum(),
        ErrorType.SYNTAX
      );
    }

    ArrayList<ParamsTNode> pTNodes = new ArrayList<>();
    while (tokens.get(0).getTokenType() == TokenType.COMMA) {
      ParamsTNode paramsTNode = ParamsTNode.parseParamsTNode(tokens);
      pTNodes.add(paramsTNode);
    }

    return new ParamsNode(eNode, pTNodes);
  }
}
