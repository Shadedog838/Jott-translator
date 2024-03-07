package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

public class FuncDefParamsNode implements JottTree{

    IdNode idNode;
    TypeNode typeNode;
    ArrayList<FuncDefParamsTNode> functionDefParamsTNodes;

    public FuncDefParamsNode(IdNode iNode, TypeNode typeNode,
            ArrayList<FuncDefParamsTNode> functionDefParamsTNodes) {
        this.idNode = iNode;
        this.typeNode = typeNode;
        this.functionDefParamsTNodes = functionDefParamsTNodes;
    }

    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        if (this.idNode == null && this.typeNode == null
        && this.functionDefParamsTNodes == null) {
            return "";
        }
        result.append(this.idNode.convertToJott());
        result.append(":");
        result.append(this.typeNode.convertToJott());
        while (functionDefParamsTNodes.size() > 0) {
            result.append(this.functionDefParamsTNodes.remove(0).convertToJott());
        }
        return result.toString();
    }

    @Override
    public String convertToJava(String className) {
        StringBuilder result = new StringBuilder();
        if (typeNode != null && idNode != null && functionDefParamsTNodes != null) {
            result.append(typeNode.convertToJava(className))
                .append(" ")
                .append(idNode.convertToJava(className));

            for (FuncDefParamsTNode node : functionDefParamsTNodes) {
                result.append(node.convertToJava(className));
            }
        }
        return result.toString();
    }

    @Override
    public String convertToC() {
        StringBuilder result = new StringBuilder();
        if (typeNode != null && idNode != null && functionDefParamsTNodes != null) {
            result.append(typeNode.convertToC())
                .append(" ")
                .append(idNode.convertToC());

            for (FuncDefParamsTNode node : functionDefParamsTNodes) {
                result.append(node.convertToC());
            }
        }

        return result.toString();
    }

    @Override
    public String convertToPython() {
        StringBuilder result = new StringBuilder();
        if (idNode != null) {
            result.append(idNode.idToken.getToken());

            for (FuncDefParamsTNode node : functionDefParamsTNodes) {
                result.append(node.convertToPython());
            }
        }

        return result.toString();
    }

    @Override
    public boolean validateTree() {
        if (idNode != null && typeNode != null && functionDefParamsTNodes != null) {
            idNode.validateTree();
            typeNode.validateTree();
            SymTable.funcTable.get(SymTable.functionName).add(typeNode.typeNode.getToken());
            SymTable.varTable.get(SymTable.functionName).put(idNode.idToken.getToken(), typeNode.typeNode.getToken());
            for (FuncDefParamsTNode funcDefParamsTNode : functionDefParamsTNodes) {
                funcDefParamsTNode.validateTree();
            }
        }
        return true;
    }

    static FuncDefParamsNode parseFunctionDefParamsNode(ArrayList<Token> tokens) throws ErrorPrinter {
        if (tokens.get(0).getTokenType() == TokenType.R_BRACKET) {
            return new FuncDefParamsNode(null, null, null);
        }

        IdNode idNode = IdNode.parseIdNode(tokens);

        if (tokens.get(0).getToken() != ":") {
            throw new ErrorPrinter("Expected :", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);

        TypeNode tNode = TypeNode.parseTypeNode(tokens);

        ArrayList<FuncDefParamsTNode> functionDefParamsTNodes = new ArrayList<>();

        while (tokens.get(0).getToken().equals(",")) {
            FuncDefParamsTNode pDefParamsTNode = FuncDefParamsTNode.parseFunctionDefParamsTNode(tokens);
            functionDefParamsTNodes.add(pDefParamsTNode);
        }

        return new FuncDefParamsNode(idNode, tNode, functionDefParamsTNodes);
    }

}
