package Nodes;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.TokenType;

public class BinaryOpExpressionNode implements ExpressionNode {

    ExpressionNode left;
    OperationNode oNode;
    ExpressionNode right;

    public BinaryOpExpressionNode(ExpressionNode left, OperationNode oNode, ExpressionNode right) {
        this.left = left;
        this.oNode = oNode;
        this.right = right;
    }

    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        result.append(this.left.convertToJott());
        result.append(this.oNode.convertToJott());
        result.append(this.right.convertToJott());
        return result.toString();
    }

    @Override
    public String convertToJava(String className) {
        StringBuilder result = new StringBuilder();
        result.append(left.convertToJava(className))
            .append(" ")
            .append(oNode.convertToJava(className))
            .append(" ")
            .append(right.convertToJava(className));

        return result.toString();

    }

    @Override
    public String convertToC() {
        StringBuilder result = new StringBuilder();
        result.append(left.convertToC())
            .append(" ")
            .append(oNode.convertToC())
            .append(" ")
            .append(right.convertToC());

        return result.toString();

    }

    @Override
    public String convertToPython() {
        StringBuilder result = new StringBuilder();
        result.append(left.convertToPython())
            .append(" ")
            .append(oNode.convertToPython())
            .append(" ")
            .append(right.convertToPython());

        return result.toString();

    }

    @Override
    public boolean validateTree() throws ErrorPrinter {
        String nodeType = getType();
        if (nodeType.equals("Invalid")){
            throw new ErrorPrinter("Operands are not the same type", oNode.operationToken.getFilename(), oNode.operationToken.getLineNum(), ErrorType.SEMANTIC);
        }
        if (!nodeType.equals("Double") && !nodeType.equals("Integer") && !nodeType.equals("Boolean")){
            throw new ErrorPrinter("Operands must be Integers or Doubles or Booleans", oNode.operationToken.getFilename(), oNode.operationToken.getLineNum(), ErrorType.SEMANTIC);
        }
        if (oNode.operationToken.getToken().equals("/") && right instanceof NumberNode) {
            NumberNode rightNode = (NumberNode) right;
            double rightDouble = Double.parseDouble(rightNode.numNode.getToken());
            if (rightDouble == 0) {
                throw new ErrorPrinter("Divide by zero", rightNode.numNode.getFilename(), rightNode.numNode.getLineNum(), ErrorPrinter.ErrorType.SEMANTIC);
            }
        }
        left.validateTree();
        oNode.validateTree();
        right.validateTree();
        return true;
    }

    @Override
    public boolean isInteger() throws ErrorPrinter {
        if (oNode.operationToken.getTokenType() != TokenType.MATH_OP) {
            return false;
        }
        return left.isInteger() && right.isInteger();
    }

    @Override
    public boolean isString() { // assuming binary operations cannot contain string what so ever
        return false;
    }

    @Override
    public boolean isBoolean() throws ErrorPrinter {
        if (oNode.operationToken.getTokenType() != TokenType.REL_OP) {
            return false;
        }
        return (left.isInteger() && right.isInteger()) || (left.isDouble() && right.isDouble());
    }

    @Override
    public boolean isDouble() throws ErrorPrinter {
        if (oNode.operationToken.getTokenType() != TokenType.MATH_OP) {
            return false;
        }
        return left.isDouble() && right.isDouble();
    }

    @Override
    public String getType() throws ErrorPrinter {
        String leftType = left.getType();
        String rightType = right.getType();
        if (!leftType.equals(rightType)) {
            return "Invalid";
        }

        if (oNode.operationToken.getTokenType() == TokenType.REL_OP) {
            return "Boolean";
        }
        return leftType;
    }

}
