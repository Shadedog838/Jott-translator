package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

public interface ExpressionNode extends JottTree {

    static ExpressionNode parseExpressionNode(ArrayList<Token> tokens) throws ErrorPrinter {
        ExpressionNode left = null;
        OperationNode oNode;

        if (tokens.get(0).getTokenType() == TokenType.ID_KEYWORD) {
            if (tokens.get(0).getToken().equals("True") || tokens.get(0).getToken().equals("False")) {
                return BooleanNode.parseBooleanNode(tokens);
            } else {
                left = IdNode.parseIdNode(tokens);
            }
        }
        if (tokens.get(0).getTokenType() == TokenType.NUMBER) {
            left = NumberNode.parseNumberNode(tokens);
        }
        else if (tokens.get(0).getTokenType() == TokenType.FC_HEADER) {
            left = FuncCallNode.parseFunctionCallNode(tokens);
        }
        else if (tokens.get(0).getTokenType() == TokenType.STRING) {
            return StringLiteralNode.parseStringLiteralNode(tokens);
        }
        if (tokens.get(0).getTokenType() == TokenType.MATH_OP || tokens.get(0).getTokenType() == TokenType.REL_OP) {
            oNode = OperationNode.parseOpNode(tokens);
            ExpressionNode right = ExpressionNode.parseExpressionNode(tokens);
            return new BinaryOpExpressionNode(left, oNode, right);
        }

        return left;
    }

    public boolean isInteger() throws ErrorPrinter;

    public boolean isString() throws ErrorPrinter;

    public boolean isBoolean() throws ErrorPrinter;

    public boolean isDouble() throws ErrorPrinter;

    public String getType() throws ErrorPrinter;
}
