package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.Token;
import provided.TokenType;

public class WhileLoopNode implements BodyStmtNode {
    ExpressionNode expressionNode;
    BodyNode bodyNode;
    boolean isReturnable = false;
    Integer depth = 0;

    public WhileLoopNode(ExpressionNode expressionNode, BodyNode bodyNode) {
        this.expressionNode = expressionNode;
        this.bodyNode = bodyNode;
    }

    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        result.append(ProgramNode.addTabs("", depth));
        result.append("while [");
        result.append(this.expressionNode.convertToJott());
        result.append("] {\n");
        bodyNode.depth = depth + 1;
        result.append(this.bodyNode.convertToJott());
        result.append(ProgramNode.addTabs("", depth));
        result.append("}\n");
        return result.toString();
    }

    @Override
    public String convertToJava(String className) {
        StringBuilder result = new StringBuilder();
        result.append(ProgramNode.addTabs("", depth));

        result.append("while (")
              .append(expressionNode.convertToJava(className))
              .append(") {\n");

        bodyNode.depth = depth + 1;
        result.append(bodyNode.convertToJava(className))
              .append(ProgramNode.addTabs("", depth))
              .append("}\n");

        return result.toString();

    }

    @Override
    public String convertToC() {
        StringBuilder result = new StringBuilder();
        result.append(ProgramNode.addTabs("", depth));

        result.append("while (")
            .append(expressionNode.convertToC())
            .append(") {\n");

        bodyNode.depth = depth + 1;
        result.append(bodyNode.convertToC())
            .append(ProgramNode.addTabs("", depth))
            .append("}\n");

        return result.toString();

    }

    @Override
    public String convertToPython() {
        StringBuilder result = new StringBuilder();
        String exprstr = expressionNode.convertToPython();
        

        result.append(ProgramNode.addTabs("", depth))
            .append("while ")
            .append(exprstr)
            .append(":\n");
        bodyNode.depth = depth + 1;
        result.append(bodyNode.convertToPython()).append(ProgramNode.addTabs("", depth)).append("\n");

        return result.toString();
    }

    @Override
    public boolean validateTree() throws ErrorPrinter {
        expressionNode.validateTree();
        bodyNode.validateTree();
        for (BodyStmtNode b : bodyNode.bStmtNodes) {
            if (b instanceof VarDecNode) {
                throw new ErrorPrinter("Cannot declare new variable inside while loop", ((VarDecNode) b).idNode.idToken.getFilename(), ((VarDecNode) b).idNode.idToken.getLineNum(), ErrorType.SEMANTIC);
            }
        }
        return true;
    }


    static WhileLoopNode parseWhileLoopNode(ArrayList<Token> tokens) throws ErrorPrinter {
        if (!tokens.get(0).getToken().equals("while")) {
            throw new ErrorPrinter("Expected a while", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);

        if (tokens.get(0).getTokenType() != TokenType.L_BRACKET) {
            throw new ErrorPrinter("Expected [", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);
        ExpressionNode eNode = ExpressionNode.parseExpressionNode(tokens);

        if (eNode == null) {
            throw new ErrorPrinter("Not a valid expression node", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }

        if (tokens.get(0).getTokenType() != TokenType.R_BRACKET) {
            throw new ErrorPrinter("Expexted ]", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);

        if (tokens.get(0).getTokenType() != TokenType.L_BRACE) {
            throw new ErrorPrinter("Expected {", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);
        BodyNode bodyNode = BodyNode.parseBodyNode(tokens);

        if (tokens.get(0).getTokenType() != TokenType.R_BRACE) {
            throw new ErrorPrinter("Expected }", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);

        return new WhileLoopNode(eNode, bodyNode);

    }

    @Override
    public void setIsBodyStmt() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setIsBodyStmt'");
    }

    @Override
    public void setDepth(Integer depth) {
        this.depth = depth;
    }

}
