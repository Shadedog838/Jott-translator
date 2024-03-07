package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.Token;
import provided.TokenType;

public class IfStmtNode implements BodyStmtNode {

    ExpressionNode expressionNode;
    BodyNode bodyNode;
    ArrayList<ElseIfNode> elseIfNodes;
    ElseNode elseNode;
    boolean isReturnable = false;
    Integer depth = 0;

    public IfStmtNode(ExpressionNode expressionNode, BodyNode bodyNode, ArrayList<ElseIfNode> elseIfNodes, ElseNode elseNode) {
        this.expressionNode = expressionNode;
        this.bodyNode = bodyNode;
        this.elseIfNodes = elseIfNodes;
        this.elseNode = elseNode;
    }

    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        result.append(ProgramNode.addTabs("", depth));
        result.append("if[");
        result.append(this.expressionNode.convertToJott());
        result.append("]{\n");
        bodyNode.depth = depth + 1;
        result.append(this.bodyNode.convertToJott());
        result.append(ProgramNode.addTabs("", depth));
        result.append("}\n");
         for (ElseIfNode elseIfNode : elseIfNodes) {
            elseIfNode.depth = depth;
            result.append(elseIfNode.convertToJott());
        }
        elseNode.depth = depth;
        result.append(this.elseNode.convertToJott());
        return result.toString();
    }

    @Override
    public String convertToJava(String className) {
        StringBuilder result = new StringBuilder();
        result.append(ProgramNode.addTabs("", depth));

        result.append("if (")
              .append(expressionNode.convertToJava(className))
              .append(") {\n");

        bodyNode.depth = depth + 1;
        result.append(bodyNode.convertToJava(className))
              .append(ProgramNode.addTabs("", depth))
              .append("}\n");

        for (ElseIfNode elseIfNode : elseIfNodes) {
            elseIfNode.depth = depth;
            result.append(elseIfNode.convertToJava(className));
        }

        elseNode.depth = depth;
        result.append(elseNode.convertToJava(className));

        return result.toString();

    }

    @Override
    public String convertToC() {
        StringBuilder result = new StringBuilder();
        result.append(ProgramNode.addTabs("", depth));

        result.append("if (")
            .append(expressionNode.convertToC())
            .append(") {\n");

        bodyNode.depth = depth + 1;
        result.append(bodyNode.convertToC())
            .append(ProgramNode.addTabs("", depth))
            .append("}\n");

        for (ElseIfNode elseIfNode : elseIfNodes) {
            elseIfNode.depth = depth;
            result.append(elseIfNode.convertToC());
        }

        elseNode.depth = depth;
        result.append(elseNode.convertToC());

        return result.toString();

    }

    @Override
    public String convertToPython() {
        StringBuilder result = new StringBuilder();
        result.append(ProgramNode.addTabs("", depth));

        result.append("if ").append(expressionNode.convertToPython())
            .append(":\n");
        
        bodyNode.depth = depth + 1;
        result.append(bodyNode.convertToPython())
            .append(ProgramNode.addTabs("", depth))
            .append("\n");
        
        for (ElseIfNode elseIfNode : elseIfNodes) {
            elseIfNode.depth = depth;
            result.append(elseIfNode.convertToPython());
        }
        
        elseNode.depth = depth;
        result.append(elseNode.convertToPython());

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
        for (ElseIfNode elseIfNode: elseIfNodes) {
            elseIfNode.validateTree();
        }
        elseNode.validateTree();
        return true;
    }

    static IfStmtNode parseIfStmtNode(ArrayList<Token> tokens) throws ErrorPrinter {
        if (!tokens.get(0).getToken().equals("if")) {
            throw new ErrorPrinter("Expected an if", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
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
            throw new ErrorPrinter("Expected ]", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);
        if (tokens.get(0).getTokenType() != TokenType.L_BRACE) {
            throw new ErrorPrinter("Expected {", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);

        BodyNode bNode = BodyNode.parseBodyNode(tokens);

        if (tokens.get(0).getTokenType() != TokenType.R_BRACE) {
            throw new ErrorPrinter("Expected }", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);

        ArrayList<ElseIfNode> elseIfNodesList = new ArrayList<>();

        while (tokens.get(0).getToken().equals("elseif")) {
            ElseIfNode elseIfNode = ElseIfNode.parseElseIfNode(tokens);
            elseIfNodesList.add(elseIfNode);
        }

        ElseNode elseNode = ElseNode.parseElseNode(tokens);

        return new IfStmtNode(eNode, bNode, elseIfNodesList, elseNode);

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
