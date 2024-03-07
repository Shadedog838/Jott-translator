package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.Token;

public class BooleanNode implements ExpressionNode {

    Token bool;

    public BooleanNode(Token bool) {
        this.bool = bool;
    }

    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        result.append(bool.getToken());
        return result.toString();
    }

    @Override
    public String convertToJava(String className) {
        return bool.getToken().toLowerCase();
    }

    @Override
    public String convertToC() {
        return bool.getToken().toLowerCase();
    }

    @Override
    public String convertToPython() {
        return bool.getToken();
    }

    @Override
    public boolean validateTree() {
        return true;
    }

    static BooleanNode parseBooleanNode(ArrayList<Token> tokens) throws ErrorPrinter {
        if (!tokens.get(0).getToken().equals("True") && !tokens.get(0).getToken().equals("False")) {
            throw new ErrorPrinter("Not a valid boolean node", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        return new BooleanNode(tokens.remove(0));
    }

    @Override
    public boolean isInteger() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Override
    public String getType() {
        return "Boolean";
    }

}
