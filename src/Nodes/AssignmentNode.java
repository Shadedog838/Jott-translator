package Nodes;

import java.util.ArrayList;
import java.util.Arrays;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.Token;
import provided.TokenType;

public class AssignmentNode implements BodyStmtNode {

  TypeNode typeNode;
  IdNode idNode;
  ExpressionNode exprNode;
  boolean isReturnable = false;
  Integer depth = 0;

  public AssignmentNode(
      TypeNode typeNode,
      IdNode idNode,
      ExpressionNode expressionNode) {
    this.typeNode = typeNode;
    this.idNode = idNode;
    this.exprNode = expressionNode;
  }

  @Override
  public String convertToJott() {
    StringBuilder result = new StringBuilder();
    result.append(ProgramNode.addTabs("", depth));
    if(this.typeNode != null)
    {
      result.append(this.typeNode.convertToJott());
      result.append(" ");
    }
    result.append(this.idNode.convertToJott());
    result.append(" ");
    result.append("=");
    result.append(" ");
    result.append(this.exprNode.convertToJott());
    result.append(";\n");
    return result.toString();
  }

  @Override
  public String convertToJava(String className) {
    StringBuilder result = new StringBuilder();
    result.append(ProgramNode.addTabs("", depth));

    if (typeNode != null) {
        result.append(typeNode.convertToJava(className))
              .append(" ");
    }

    result.append(idNode.convertToJava(className))
          .append(" = ")
          .append(exprNode.convertToJava(className))
          .append(";\n");

    return result.toString();

  }

  @Override
  public String convertToC() {
    StringBuilder result = new StringBuilder();
    result.append(ProgramNode.addTabs("", depth));

    if (typeNode != null) {
        result.append(typeNode.convertToC())
              .append(" ");
    }

    result.append(idNode.convertToC())
          .append(" = ")
          .append(exprNode.convertToC())
          .append(";\n");

    return result.toString();

  }

  @Override
  public String convertToPython() {
    StringBuilder result = new StringBuilder();
    result.append(ProgramNode.addTabs("", depth));
    String idstr = idNode.convertToPython();
    String expstr = exprNode.convertToPython();
    result.append(idstr)
          .append(" = ")
          .append(expstr)
          .append("\n");
    

    return result.toString();

  }

  @Override
  public boolean validateTree() throws ErrorPrinter {
    if(typeNode == null) // if there is no type
    {
      if(!SymTable.varTable.get(SymTable.functionName).containsKey(idNode.idToken.getToken())) { // if the id does not exists in the symtable
        throw new ErrorPrinter(idNode.idToken.getToken() + " doesn't exist", idNode.idToken.getFilename(), idNode.idToken.getLineNum(), ErrorType.SEMANTIC);
      }
      idNode.validateTree();
      exprNode.validateTree();
      SymTable.unitVars.remove(idNode.idToken.getToken());
    }
    else {
      if(SymTable.varTable.get(SymTable.functionName).containsKey(idNode.idToken.getToken())) { // if the id already exists in the symtable
        throw new ErrorPrinter(idNode.idToken.getToken() + " already exists", idNode.idToken.getFilename(), idNode.idToken.getLineNum(), ErrorType.SEMANTIC);
      }
      if (exprNode instanceof FuncCallNode) {

        if (!SymTable.funcTable.containsKey(((FuncCallNode) exprNode).idNode.idToken.getToken())) {
          throw new ErrorPrinter(((FuncCallNode) exprNode).idNode.idToken.getToken() + " is not defined", ((FuncCallNode) exprNode).idNode.idToken.getFilename(), ((FuncCallNode) exprNode).idNode.idToken.getLineNum(), ErrorType.SEMANTIC);
        }
      }
      if (!typeNode.typeNode.getToken().equals(exprNode.getType())) {
        throw new ErrorPrinter(idNode.idToken.getToken() + " type does not match", idNode.idToken.getFilename(), idNode.idToken.getLineNum(), ErrorType.SEMANTIC);
      }
      typeNode.validateTree();
      idNode.validateTree();
      ArrayList<String> invalid_ids = new ArrayList<>(Arrays.asList("null", "while", "if", "else", "elseif", "main", "void", "true", "false", "def", "return"));
      if(invalid_ids.contains(idNode.idToken.getToken())) {
          throw new ErrorPrinter(idNode.idToken.getToken() + " is a keyword", idNode.idToken.getFilename(), idNode.idToken.getLineNum(),  ErrorType.SEMANTIC);
      }
      exprNode.validateTree();
      SymTable.varTable.get(SymTable.functionName).put(idNode.idToken.getToken(), typeNode.typeNode.getToken());
    }
    return true;

  }

  static AssignmentNode parseAssignmentNode(ArrayList<Token> tokens) throws ErrorPrinter {
    IdNode idNode = null;
    TypeNode tNode = null;
    if (tokens.get(1).getTokenType() == TokenType.ASSIGN) {
      idNode = IdNode.parseIdNode(tokens);
    }
    else {
      tNode = TypeNode.parseTypeNode(tokens);
      idNode = IdNode.parseIdNode(tokens);
    }
    if (tokens.get(0).getTokenType() != TokenType.ASSIGN) {
      throw new ErrorPrinter(
          "Expected =",
          tokens.get(0).getFilename(),
          tokens.get(0).getLineNum(), ErrorType.SYNTAX);
    }
    tokens.remove(0);
    ExpressionNode expressionNode = ExpressionNode.parseExpressionNode(tokens);

    if (expressionNode == null) {
      throw new ErrorPrinter("Not a valid expression node", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
    }
    if (tokens.get(0).getTokenType() != TokenType.SEMICOLON) {
      throw new ErrorPrinter(
          "Expected ;",
          tokens.get(0).getFilename(),
          tokens.get(0).getLineNum(), ErrorType.SYNTAX);
    }
    tokens.remove(0);
    return new AssignmentNode(tNode, idNode, expressionNode);
  }

  @Override
  public void setIsBodyStmt() {
    throw new UnsupportedOperationException("Unimplemented method 'setIsBodyStmt'");
  }

  @Override
  public void setDepth(Integer depth) {
    this.depth = depth;
  }

}
