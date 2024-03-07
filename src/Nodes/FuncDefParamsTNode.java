package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

public class FuncDefParamsTNode implements JottTree{

    IdNode idNode;
    TypeNode typeNode;

    public FuncDefParamsTNode(IdNode idNode, TypeNode typeNode) {
        this.idNode = idNode;
        this.typeNode = typeNode;
    }

    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        result.append(",");
        result.append(this.idNode.convertToJott());
        result.append(":");
        result.append(this.typeNode.convertToJott());
        return result.toString();
    }

    @Override
    public String convertToJava(String className) {
        StringBuilder result = new StringBuilder(", ");
        if (typeNode != null && idNode != null) {
            result.append(typeNode.convertToJava(className))
                .append(" ")
                .append(idNode.convertToJava(className));
        }
        return result.toString();
    }

    @Override
    public String convertToC() {
        StringBuilder result = new StringBuilder(", ");
        if (typeNode != null && idNode != null) {
            result.append(typeNode.convertToC())
                .append(" ")
                .append(idNode.convertToC());
        }

        return result.toString();
    }

    @Override
    public String convertToPython() {
        StringBuilder result = new StringBuilder(", ");
        if (idNode != null) {
            result.append(idNode.convertToC());
        }

        return result.toString();
    }

    @Override
    public boolean validateTree() {
        idNode.validateTree();
        typeNode.validateTree();
        SymTable.funcTable.get(SymTable.functionName).add(typeNode.typeNode.getToken()); // adds the return type of the function
        SymTable.varTable.get(SymTable.functionName).put(idNode.idToken.getToken(), typeNode.typeNode.getToken());
        return true;
    }

    static FuncDefParamsTNode parseFunctionDefParamsTNode(ArrayList<Token> tokens) throws ErrorPrinter {
        if (tokens.get(0).getTokenType() != TokenType.COMMA) {
            throw new ErrorPrinter("Expected ,", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);

        IdNode idNode = IdNode.parseIdNode(tokens);

         if (tokens.get(0).getTokenType() != TokenType.COLON) {
            throw new ErrorPrinter("Expected :", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);

        TypeNode tNode = TypeNode.parseTypeNode(tokens);

        return new FuncDefParamsTNode(idNode, tNode);
    }

}
