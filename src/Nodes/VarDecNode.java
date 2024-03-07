package Nodes;

import java.util.ArrayList;
import java.util.Arrays;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.Token;
import provided.TokenType;

public class VarDecNode implements BodyStmtNode {

    TypeNode tNode;
    IdNode idNode;
    boolean isReturnable = false;
    Integer depth = 0;

    public VarDecNode(TypeNode tNode, IdNode idNode) {
        this.tNode = tNode;
        this.idNode = idNode;
    }

    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        result.append(tNode.convertToJott() + " ");
        result.append(this.idNode.convertToJott());
        result.append(";");
        return result.toString();
    }

    @Override
    public String convertToJava(String className) {
        StringBuilder result = new StringBuilder();
        result.append(ProgramNode.addTabs("", depth));

        String typeStr = tNode.convertToJava(className);
        String idStr = idNode.convertToJava(className);
        result.append(typeStr).append(" ").append(idStr).append(";\n");

        return result.toString();

    }

    @Override
    public String convertToC() {
        StringBuilder result = new StringBuilder();
        result.append(ProgramNode.addTabs("", depth));

        String typeStr = tNode.convertToC();
        String idStr = idNode.convertToC();
        result.append(typeStr).append(" ").append(idStr).append(";\n");

        return result.toString();

    }

    @Override
    public String convertToPython() {
        return "";
    }

    @Override
    public boolean validateTree() throws ErrorPrinter{
        if(SymTable.varTable.get(SymTable.functionName).containsKey(idNode.idToken.getToken())) { // if the id already exists in the symtable
            throw new ErrorPrinter(idNode.idToken.getToken() + " already exists", idNode.idToken.getFilename(), idNode.idToken.getLineNum(),  ErrorType.SEMANTIC);
        }
        tNode.validateTree();
        idNode.validateTree();
        ArrayList<String> invalid_ids = new ArrayList<>(Arrays.asList("null", "while", "if", "else", "main", "void", "true", "false", "def", "return"));
        if(invalid_ids.contains(idNode.idToken.getToken())) {
            throw new ErrorPrinter(idNode.idToken.getToken() + " is a keyword", idNode.idToken.getFilename(), idNode.idToken.getLineNum(),  ErrorType.SEMANTIC);
        }
        if(!Character.isLowerCase(idNode.idToken.getToken().charAt(0))) {
            throw new ErrorPrinter("Variable cannot be uppercase", idNode.idToken.getFilename(), idNode.idToken.getLineNum(),  ErrorType.SEMANTIC);
        }
        SymTable.varTable.get(SymTable.functionName).put(idNode.idToken.getToken(), tNode.typeNode.getToken());
        SymTable.unitVars.add(idNode.idToken.getToken());
        return true;
    }

    static VarDecNode parseVarDecNode(ArrayList<Token> tokens) throws ErrorPrinter {
        TypeNode typeNode = null;
        IdNode idNode = null;

        typeNode = TypeNode.parseTypeNode(tokens);
        idNode = IdNode.parseIdNode(tokens);

        if (tokens.get(0).getTokenType() != TokenType.SEMICOLON) {
            throw new ErrorPrinter("Expected ;", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);

        return new VarDecNode(typeNode, idNode);
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
