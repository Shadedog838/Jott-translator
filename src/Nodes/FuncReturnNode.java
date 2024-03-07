package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import provided.JottTree;
import provided.Token;

public class FuncReturnNode implements JottTree {
    TypeNode typeNode;

    public FuncReturnNode(TypeNode typeNode) {
        this.typeNode = typeNode;
    }


    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        if (this.typeNode == null)
        {
            result.append("Void");
        }
        else
        {
            result.append(this.typeNode.convertToJott());
        }
        return result.toString();
    }


    @Override
    public String convertToJava(String className) {
        if (typeNode == null) {
            return "void";
        } else {
            return typeNode.convertToJava(className);
        }
    }


    @Override
    public String convertToC() {
        if (typeNode == null) {
            return "void";
        } else {
            return typeNode.convertToC();
        }

    }


    @Override
    public String convertToPython() {
        return "";
    }


    @Override
    public boolean validateTree() {
        // Guarantees that the return type will be the last element added to the the table
        if (this.typeNode != null) {
            this.typeNode.validateTree();
            SymTable.funcTable.get(SymTable.functionName).add(typeNode.typeNode.getToken());
        } else {
            SymTable.funcTable.get(SymTable.functionName).add("Void");
        }
        return true;
    }

    static FuncReturnNode parseFuncReturn(ArrayList<Token> tokens) throws ErrorPrinter {
        if (tokens.get(0).getToken().equals("Void")) {
            tokens.remove(0);
            return new FuncReturnNode(null);
        }

        TypeNode tNode = TypeNode.parseTypeNode(tokens);

        return new FuncReturnNode(tNode);

    }

}
