package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

public class BodyNode implements JottTree {

    ArrayList<BodyStmtNode> bStmtNodes;
    ReturnStmtNode returnStmtNode;
    Integer depth = 0;

    public BodyNode(ArrayList<BodyStmtNode> bodyStmtNodes, ReturnStmtNode returnStmtNode) {
        this.bStmtNodes = bodyStmtNodes;
        this.returnStmtNode = returnStmtNode;
    }


    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        for (BodyStmtNode bodyStmtNode : bStmtNodes) {
            bodyStmtNode.setDepth(depth);
            result.append(bodyStmtNode.convertToJott());
        }

        returnStmtNode.depth = depth;
        result.append(returnStmtNode.convertToJott());
        return result.toString();
    }

    @Override
    public String convertToJava(String className) {
        StringBuilder result = new StringBuilder();
        for (BodyStmtNode bodyStmtNode : bStmtNodes) {
            bodyStmtNode.setDepth(depth);
            result.append(bodyStmtNode instanceof FuncCallNode ? bodyStmtNode.convertToJava(className) + ";\n" : bodyStmtNode.convertToJava(className));
        }

        returnStmtNode.depth = depth;
        result.append(returnStmtNode.convertToJava(className));

        return result.toString();
    }

    @Override
    public String convertToC() {
        StringBuilder result = new StringBuilder();
        for (BodyStmtNode bodyStmtNode : bStmtNodes) {
            bodyStmtNode.setDepth(depth);
            result.append(bodyStmtNode instanceof FuncCallNode ? bodyStmtNode.convertToC() + ";\n" : bodyStmtNode.convertToC());
        }

        returnStmtNode.depth = depth;
        result.append(returnStmtNode.convertToC());

        return result.toString();
    }

    @Override
    public String convertToPython() {
        StringBuilder result = new StringBuilder();
        for (BodyStmtNode bodyStmtNode : bStmtNodes) {
            bodyStmtNode.setDepth(depth);
            result.append(bodyStmtNode instanceof FuncCallNode ? bodyStmtNode.convertToPython() + "\n" : bodyStmtNode.convertToPython());
        }

        returnStmtNode.depth = depth;
        result.append(returnStmtNode.convertToPython());
        return result.toString();
    }

    @Override
    public boolean validateTree() throws ErrorPrinter {
        for (BodyStmtNode bodyStmtNode: bStmtNodes) {
            bodyStmtNode.validateTree();
        }
        returnStmtNode.validateTree();
        return true;
    }

    static BodyNode parseBodyNode(ArrayList<Token> tokens) throws ErrorPrinter {
        ArrayList<BodyStmtNode> bodyStmtNodes = new ArrayList<>();
        while (tokens.get(0).getTokenType() != TokenType.R_BRACE && !tokens.get(0).getToken().equals("return")) {
            BodyStmtNode bStmtNode = BodyStmtNode.parseBodyStmtNode(tokens);
            if (bStmtNode == null) {
                throw new ErrorPrinter("Not a valid body statement node", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
            }
            bodyStmtNodes.add(bStmtNode);
        }

        ReturnStmtNode returnStmtNode = ReturnStmtNode.parseReturnStmtNode(tokens);

        return new BodyNode(bodyStmtNodes, returnStmtNode);

    }

    public boolean isReturnable() {
        Boolean isReturnable = false;
        for (BodyStmtNode bodyStmtNode: bStmtNodes) {
            if (bodyStmtNode instanceof IfStmtNode) {
                Boolean ifStmtReturn = ((IfStmtNode)bodyStmtNode).bodyNode.isReturnable();
                Boolean elseIfStmtReturn = true;
                for (ElseIfNode elseIfNode : ((IfStmtNode)bodyStmtNode).elseIfNodes) {
                    elseIfStmtReturn = elseIfNode.bodyNode.isReturnable();
                }

                Boolean elseStmtReturn = ((IfStmtNode)bodyStmtNode).elseNode.bodyNode != null ? ((IfStmtNode)bodyStmtNode).elseNode.bodyNode.isReturnable() : false;
                isReturnable = ifStmtReturn && elseIfStmtReturn && elseStmtReturn;
            }
        }

        return returnStmtNode.rNode != null ? true : isReturnable;
    }

}
