package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

public class ElseNode implements JottTree {

    BodyNode bodyNode;
    Integer depth = 0;

    public ElseNode(BodyNode bodyNode) {
        this.bodyNode = bodyNode;
    }

    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        if (bodyNode != null) {
            result.append(ProgramNode.addTabs("", depth));
            result.append("else {\n");
             bodyNode.depth = depth + 1;
            result.append(this.bodyNode.convertToJott())
                .append(ProgramNode.addTabs("", depth));
            result.append("}\n");
            return result.toString();
        }
        return result.toString();
    }

    @Override
    public String convertToJava(String className) {
        if (bodyNode != null) {
            StringBuilder result = new StringBuilder();
            result.append(ProgramNode.addTabs("", depth));

            result.append("else {\n");

            bodyNode.depth = depth + 1;
            result.append(bodyNode.convertToJava(className))
                  .append(ProgramNode.addTabs("", depth))
                  .append("}\n");

            return result.toString();
        }

        return "";

    }

    @Override
    public String convertToC() {
        if (bodyNode != null) {
            StringBuilder result = new StringBuilder();
            result.append(ProgramNode.addTabs("", depth));

            result.append("else {\n");

            bodyNode.depth = depth + 1;
            result.append(bodyNode.convertToC())
                  .append(ProgramNode.addTabs("", depth))
                  .append("}\n");

            return result.toString();
        }

        return "";
    }

    @Override
    public String convertToPython() {
        if (bodyNode != null) {
            StringBuilder result = new StringBuilder();
            result.append(ProgramNode.addTabs("", depth));

            result.append("else:\n");
            bodyNode.depth = depth + 1;

            result.append(bodyNode.convertToPython())
                .append(ProgramNode.addTabs("", depth))
                .append("\n");
            
            return result.toString();
        }
        return "";
    }

    @Override
    public boolean validateTree() throws ErrorPrinter {
        if (bodyNode != null) {
             bodyNode.validateTree();
            for (BodyStmtNode b : bodyNode.bStmtNodes) {
                if (b instanceof VarDecNode) {
                    throw new ErrorPrinter("Cannot declare new variable inside while loop", ((VarDecNode) b).idNode.idToken.getFilename(), ((VarDecNode) b).idNode.idToken.getLineNum(), ErrorType.SEMANTIC);
                }
            }
        }
        return true;
    }

    static ElseNode parseElseNode(ArrayList<Token> tokens) throws ErrorPrinter {
        if (tokens.get(0).getToken().equals("return") || tokens.get(0).getTokenType() == TokenType.R_BRACE ||
        tokens.get(0).getToken().equals("if") || tokens.get(0).getToken().equals("::") || tokens.get(0).getToken().equals("while") ||
        tokens.get(0).getTokenType() == TokenType.ID_KEYWORD) {
            if (!tokens.get(0).getToken().equals("else")) {
                return new ElseNode(null);
            }
        }

        if (!tokens.get(0).getToken().equals("else")) {
            throw new ErrorPrinter("Expected else", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
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

        return new ElseNode(bodyNode);

    }

}
