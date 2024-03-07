package Nodes;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import java.util.ArrayList;
import provided.Token;
import provided.TokenType;

public class FuncCallNode implements ExpressionNode, BodyStmtNode {

  IdNode idNode;
  ParamsNode pNode;
  boolean isBodyStmt = false;
  boolean isReturnable = false;
  Integer depth = 0;

  public FuncCallNode(IdNode id, ParamsNode pNode) {
    this.idNode = id;
    this.pNode = pNode;
  }

  @Override
  public String convertToJott() {
    StringBuilder result = new StringBuilder();
    result.append(ProgramNode.addTabs("", depth));
    result.append("::");
    result.append(this.idNode.convertToJott());
    result.append("[");
    result.append(this.pNode.convertToJott());
    result.append("]");
    if (isBodyStmt) {
      result.append(";\n");
    }

    return result.toString();
  }

  @Override
  public String convertToJava(String className) {
    StringBuilder result = new StringBuilder();
    result.append(ProgramNode.addTabs("", depth));

    String idStr = idNode.idToken.getToken();
    if ("print".equals(idStr)) {
      idStr = "System.out.println";
    } else if ("concat".equals(idStr)) {
      result
        .append(pNode.eNode.convertToJava(className))
        .append(" + ")
        .append(pNode.pTNodes.get(0).eNode.convertToJava(className));
      return result.toString();
    } else if ("length".equals(idStr)) {
      result.append(pNode.eNode.convertToJava(className)).append(".length()");
      return result.toString();
    }

    String paramsStr = pNode.convertToJava(className);
    result.append(idStr).append("(").append(paramsStr).append(")");

    return result.toString();
  }

  @Override
  public String convertToC() {
    StringBuilder result = new StringBuilder();
    result.append(ProgramNode.addTabs("", depth));

        try {
            String idStr = idNode.idToken.getToken();
            if ("print".equals(idStr)) {
                String paramType = pNode.eNode.getType();
                switch (paramType) {
                    case "Integer":
                        idStr = "printInt";
                        break;
                    case "String":
                        idStr = "printStr";
                        break;
                    case "Double":
                        idStr = "printDouble";
                        break;
                    case "Boolean":
                        idStr = "printBoolean";
                        break;
                }
            }
            String paramsStr = pNode.convertToC();
            result.append(idStr).append("(").append(paramsStr).append(")");
            return result.toString();
        } catch (ErrorPrinter e) {
            e.getMessage();
        }

    return "";
  }

  @Override
  public String convertToPython() {
    String idstr = idNode.idToken.getToken();
    if ("length".equals(idstr)) {
      idstr = "len";
    } else if ("concat".equals(idstr)) {
      StringBuilder inside = new StringBuilder(
        pNode.eNode.convertToPython()
      );
      inside.append(ProgramNode.addTabs("", depth));
      inside.append(" + ").append(pNode.pTNodes.get(0).convertToPython().replace(",", ""));
      return inside.toString();
    }
    StringBuilder result = new StringBuilder();
    result.append(ProgramNode.addTabs("", depth));
    String parastr = pNode.convertToPython();
    result.append(idstr).append("(").append(parastr).append(")");
   
    return result.toString();
  }

  @Override
  public boolean validateTree() throws ErrorPrinter {
    if (!SymTable.funcTable.containsKey(idNode.idToken.getToken())) {
      throw new ErrorPrinter(
        idNode.idToken.getToken() + " is not defined",
        idNode.idToken.getFilename(),
        idNode.idToken.getLineNum(),
        ErrorType.SEMANTIC
      );
    }
    if (pNode.eNode == null && pNode.pTNodes == null) {
      if (SymTable.funcTable.get(idNode.idToken.getToken()).size() != 1) { // make sure that funcTable params are empty too
        throw new ErrorPrinter(
          "Did expect params for " + idNode.idToken.getToken(),
          idNode.idToken.getFilename(),
          idNode.idToken.getLineNum(),
          ErrorType.SEMANTIC
        ); 
      }
    } else {
      if (
        SymTable.funcTable.get(idNode.idToken.getToken()).size() !=
        pNode.pTNodes.size() +
        2
      ) { // make sure that funcTable params are empty too
        throw new ErrorPrinter(
          "Number of params for " +
          idNode.idToken.getToken() +
          " is not correct",
          idNode.idToken.getFilename(),
          idNode.idToken.getLineNum(),
          ErrorType.SEMANTIC
        );
      }
    }

    if (pNode.eNode != null && pNode.pTNodes != null) {
      // Check if eNode is matched type in the funcTable
      if (
        SymTable.funcTable
          .get(idNode.idToken.getToken())
          .get(0)
          .equals("Integer")
      ) {
        pNode.eNode.isInteger();
      } else if (
        SymTable.funcTable
          .get(idNode.idToken.getToken())
          .get(0)
          .equals("String")
      ) {
        pNode.eNode.isString();
      } else if (
        SymTable.funcTable
          .get(idNode.idToken.getToken())
          .get(0)
          .equals("Double")
      ) {
        pNode.eNode.isDouble();
      } else if (
        SymTable.funcTable
          .get(idNode.idToken.getToken())
          .get(0)
          .equals("Boolean")
      ) {
        pNode.eNode.isBoolean();
      } else if (
        SymTable.funcTable.get(idNode.idToken.getToken()).get(0).equals("Any")
      ) {
        if (pNode.eNode.getType().equals("Void")) {
          FuncCallNode funcCallNode = (FuncCallNode) pNode.eNode;
          throw new ErrorPrinter(
            "Print statement cannot take type Void",
            funcCallNode.idNode.idToken.getFilename(),
            funcCallNode.idNode.idToken.getLineNum(),
            ErrorType.SEMANTIC
          );
        }
      }
      int index = 1;
      for (ParamsTNode paramsTNode : pNode.pTNodes) { // validate every single param
        if (
          SymTable.funcTable
            .get(idNode.idToken.getToken())
            .get(index)
            .equals("Integer")
        ) {
          paramsTNode.eNode.isInteger();
        } else if (
          SymTable.funcTable
            .get(idNode.idToken.getToken())
            .get(index)
            .equals("String")
        ) {
          paramsTNode.eNode.isString();
        } else if (
          SymTable.funcTable
            .get(idNode.idToken.getToken())
            .get(index)
            .equals("Double")
        ) {
          paramsTNode.eNode.isDouble();
        } else if (
          SymTable.funcTable
            .get(idNode.idToken.getToken())
            .get(index)
            .equals("Boolean")
        ) {
          paramsTNode.eNode.isBoolean();
        }
        index += 1;
      }
    }
    idNode.validateTree();
    pNode.validateTree();
    return true;
  }

  static FuncCallNode parseFunctionCallNode(ArrayList<Token> tokens)
    throws ErrorPrinter {
    if (tokens.get(0).getTokenType() != TokenType.FC_HEADER) {
      throw new ErrorPrinter(
        "Expected ::",
        tokens.get(0).getFilename(),
        tokens.get(0).getLineNum(),
        ErrorType.SYNTAX
      );
    }
    tokens.remove(0);
    IdNode funcName = IdNode.parseIdNode(tokens);

    if (tokens.get(0).getTokenType() != TokenType.L_BRACKET) {
      throw new ErrorPrinter(
        "Expected [",
        tokens.get(0).getFilename(),
        tokens.get(0).getLineNum(),
        ErrorType.SYNTAX
      );
    }
    tokens.remove(0);
    ParamsNode pNode = ParamsNode.parseParamsNode(tokens);

    if (tokens.get(0).getTokenType() != TokenType.R_BRACKET) {
      throw new ErrorPrinter(
        "Expected ]",
        tokens.get(0).getFilename(),
        tokens.get(0).getLineNum(),
        ErrorType.SYNTAX
      );
    }
    tokens.remove(0);
    return new FuncCallNode(funcName, pNode);
  }

  @Override
  public void setIsBodyStmt() {
    isBodyStmt = true;
  }

  @Override
  public boolean isInteger() throws ErrorPrinter {
    if (
      !SymTable.funcTable
        .get(idNode.idToken.getToken())
        .get(SymTable.funcTable.get(idNode.idToken.getToken()).size() - 1)
        .equals("Integer")
    ) {
      throw new ErrorPrinter(
        "Return type doesn't match",
        idNode.idToken.getFilename(),
        idNode.idToken.getLineNum(),
        ErrorType.SEMANTIC
      );
    }
    return true;
  }

  @Override
  public boolean isString() throws ErrorPrinter {
    if (
      !SymTable.funcTable
        .get(idNode.idToken.getToken())
        .get(SymTable.funcTable.get(idNode.idToken.getToken()).size() - 1)
        .equals("String")
    ) {
      throw new ErrorPrinter(
        "Return type doesn't match",
        idNode.idToken.getFilename(),
        idNode.idToken.getLineNum(),
        ErrorType.SEMANTIC
      );
    }
    return true;
  }

  @Override
  public boolean isBoolean() throws ErrorPrinter {
    if (
      !SymTable.funcTable
        .get(idNode.idToken.getToken())
        .get(SymTable.funcTable.get(idNode.idToken.getToken()).size() - 1)
        .equals("Boolean")
    ) {
      throw new ErrorPrinter(
        "Return type doesn't match",
        idNode.idToken.getFilename(),
        idNode.idToken.getLineNum(),
        ErrorType.SEMANTIC
      );
    }
    return true;
  }

  @Override
  public boolean isDouble() throws ErrorPrinter {
    if (
      !SymTable.funcTable
        .get(idNode.idToken.getToken())
        .get(SymTable.funcTable.get(idNode.idToken.getToken()).size() - 1)
        .equals("Double")
    ) {
      throw new ErrorPrinter(
        "Return type doesn't match",
        idNode.idToken.getFilename(),
        idNode.idToken.getLineNum(),
        ErrorType.SEMANTIC
      );
    }
    return true;
  }

  @Override
  public String getType() {
    return SymTable.funcTable
      .get(idNode.idToken.getToken())
      .get(SymTable.funcTable.get(idNode.idToken.getToken()).size() - 1);
  }

  @Override
  public void setDepth(Integer depth) {
    this.depth = depth;
  }
}
