package Nodes;

import provided.JottTree;
import provided.Token;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;


public class TypeNode implements JottTree {

    Token typeNode;

    public TypeNode(Token token) {
        this.typeNode = token;

    }

    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        result.append(this.typeNode.getToken());
        return result.toString();
    }

    @Override
    public String convertToJava(String className) {
       String type = typeNode.getToken();
        if (type == "Integer") {
            return "int";
        }
        else if (type == "Double") {
            return "float";
        }
        else if (type == "Boolean") {
            return "boolean";
        }
        else {
            return type;
        }
    }

    @Override
    public String convertToC() {
        return typeNode.getToken();
    }

    @Override
    public String convertToPython() {
        String type = typeNode.getToken();
        if (type == "Integer") {
            return "int";
        }
        else if (type == "Double") {
            return "float";
        }
        else if (type == "Boolean") {
            return "bool";
        }
        else if (type == "String") {
            return "str";
        }
        return type;
    }

    @Override
    public boolean validateTree() {
        return true;
    }

    static TypeNode parseTypeNode(ArrayList<Token> tokens) throws ErrorPrinter {
        if (!tokens.get(0).getToken().equals("Double") && !tokens.get(0).getToken().equals("Integer")
        && !tokens.get(0).getToken().equals("String") && !tokens.get(0).getToken().equals("Boolean")) {
            throw new ErrorPrinter("Not a valid type node", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        return new TypeNode(tokens.remove(0));
    }

}
