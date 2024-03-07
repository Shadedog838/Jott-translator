package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

public interface BodyStmtNode extends JottTree {
  void setIsBodyStmt();
  void setDepth(Integer depth);

  static BodyStmtNode parseBodyStmtNode(ArrayList<Token> tokens)
    throws ErrorPrinter {
    BodyStmtNode bStmtNode = null;
    if (tokens.get(0).getToken().equals("if")) {
      bStmtNode = IfStmtNode.parseIfStmtNode(tokens);
    } else if (tokens.get(0).getToken().equals("while")) {
      bStmtNode = WhileLoopNode.parseWhileLoopNode(tokens);
    } else if (
      tokens.get(2).getTokenType() == TokenType.ASSIGN ||
      tokens.get(1).getTokenType() == TokenType.ASSIGN
    ) {
      bStmtNode = AssignmentNode.parseAssignmentNode(tokens);
    } else if (tokens.get(2).getTokenType() == TokenType.SEMICOLON) {
      bStmtNode = VarDecNode.parseVarDecNode(tokens);
    } else if (tokens.get(0).getTokenType() == TokenType.FC_HEADER) {
      bStmtNode = FuncCallNode.parseFunctionCallNode(tokens);

      if (tokens.get(0).getTokenType() != TokenType.SEMICOLON) {
        throw new ErrorPrinter(
          "Expected ;",
          tokens.get(0).getFilename(),
          tokens.get(0).getLineNum(), ErrorType.SYNTAX
        );
      }
      tokens.remove(0);
      bStmtNode.setIsBodyStmt();
    }
    return bStmtNode;
  }
}
