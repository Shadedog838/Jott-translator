package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

public class ReturnStmtNode implements JottTree{

    ExpressionNode rNode;
    Integer depth = 0;

    public ReturnStmtNode(ExpressionNode rNode) {
        this.rNode = rNode;
    }

    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        if (this.rNode != null)
        {
            result.append(ProgramNode.addTabs("", depth));
            result.append("return ");
            result.append(this.rNode.convertToJott());
            result.append(";\n");
        }
        return result.toString();
    }

    @Override
    public String convertToJava(String className) {
        StringBuilder result = new StringBuilder();
        if (rNode != null) {
            result.append(ProgramNode.addTabs("", depth));
            result.append("return ").append(rNode.convertToJava(className)).append(";\n");
        }

        return result.toString();

    }

    @Override
    public String convertToC() {
        StringBuilder result = new StringBuilder();

        if (rNode != null) {
            result.append(ProgramNode.addTabs("", depth));
            result.append("return ").append(rNode.convertToC()).append(";\n");
        }

        return result.toString();

    }

    @Override
    public String convertToPython() {
        StringBuilder result = new StringBuilder();

        if (rNode != null) {
            result.append(ProgramNode.addTabs("", depth));
            result.append("return ").append(rNode.convertToPython()).append("\n");
        }
        return result.toString();
    }

    @Override
    public boolean validateTree() throws ErrorPrinter {
        if (rNode == null) {
            return true;
        }
        rNode.validateTree();
        String returnType = SymTable.funcTable.get(SymTable.functionName).get(SymTable.funcTable.get(SymTable.functionName).size() - 1);
        if (returnType.equals("Integer")) {
            rNode.isInteger();
        }
        else if (returnType.equals("String")) {
            rNode.isString();
        }
        else if (returnType.equals("Boolean")) {
            rNode.isBoolean();
        }
        else if (returnType.equals("Double")) {
            rNode.isDouble();
        }
        return true;
    }

    static ReturnStmtNode parseReturnStmtNode(ArrayList<Token> tokens) throws ErrorPrinter {
        if (tokens.get(0).getTokenType() == TokenType.R_BRACE) {
            return new ReturnStmtNode(null);
        }

        if (!tokens.get(0).getToken().equals("return")) {
            throw new ErrorPrinter("Expected return", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);
        ExpressionNode eNode = ExpressionNode.parseExpressionNode(tokens);

        if (eNode == null) {
            throw new ErrorPrinter("Not a valid expression node", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }

        if (tokens.get(0).getTokenType() != TokenType.SEMICOLON) {
            throw new ErrorPrinter("Expected ;", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);
        return new ReturnStmtNode(eNode);
    }
}
