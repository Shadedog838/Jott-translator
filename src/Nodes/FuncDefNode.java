package Nodes;

import java.util.ArrayList;
import java.util.HashMap;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

public class FuncDefNode implements JottTree{

    IdNode idNode;
    FuncDefParamsNode functionDefParamsNode;
    FuncReturnNode funcReturnNode;
    Integer depth = 0;

    public FuncDefNode(IdNode idNode, FuncDefParamsNode functionDefParamsNode, FuncReturnNode funcReturnNode,
            BodyNode bodyNode) {
        this.idNode = idNode;
        this.functionDefParamsNode = functionDefParamsNode;
        this.funcReturnNode = funcReturnNode;
        this.bodyNode = bodyNode;
    }

    BodyNode bodyNode;

    boolean isMainDefinedCorrectly() throws ErrorPrinter {
        if (functionDefParamsNode.idNode != null) {
            throw new ErrorPrinter("main must have no parameters", idNode.idToken.getFilename(), idNode.idToken.getLineNum(), ErrorType.SEMANTIC);
        }
        else if (funcReturnNode.typeNode != null) {
            throw new ErrorPrinter("main must return void", idNode.idToken.getFilename(), idNode.idToken.getLineNum(), ErrorType.SEMANTIC);
        }
        return true;
    }

    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        result.append("def ");
        result.append(this.idNode.convertToJott());
        result.append("[");
        result.append(this.functionDefParamsNode.convertToJott());
        result.append("]");
        result.append(":");
        result.append(this.funcReturnNode.convertToJott());
        result.append("{\n");
        bodyNode.depth = depth + 1;
        result.append(this.bodyNode.convertToJott());
        result.append(ProgramNode.addTabs("", depth));
        result.append("}\n");
        return result.toString();
    }

    @Override
    public String convertToJava(String className) {
        StringBuilder result = new StringBuilder();
        result.append(ProgramNode.addTabs("", depth))
                .append("public static ")
                .append(funcReturnNode.convertToJava(className))
                .append(" ")
                .append(idNode.convertToJava(className))
                .append("(");

        if (idNode.idToken.getToken().equals("main")) {
            result.append("String args[]");
        } else {
            result.append(functionDefParamsNode.convertToJava(className));
        }
        result.append(") {\n");
        bodyNode.depth = depth + 1;
        result.append(bodyNode.convertToJava(className));

        result.append(ProgramNode.addTabs("", depth))
                .append("}\n");

        return result.toString();
    }

    @Override
    public String convertToC() {
        StringBuilder result = new StringBuilder();
        SymTable.functionName = idNode.idToken.getToken();
        result.append(idNode.idToken.getToken().equals("main") ? "int" : funcReturnNode.convertToC())
            .append(" ")
            .append(idNode.convertToC())
            .append("(")
            .append(functionDefParamsNode.convertToC())
            .append(") ")
            .append("{\n");

        bodyNode.returnStmtNode.rNode = idNode.idToken.getToken().equals("main") ? new NumberNode(new Token("0", null, 0, TokenType.NUMBER)) : bodyNode.returnStmtNode.rNode;
        bodyNode.depth = depth + 1;
        result.append(bodyNode.convertToC());
        result.append("}\n");

        return result.toString();
    }

    @Override
    public String convertToPython() {
        StringBuilder result = new StringBuilder();
        result.append("def ")
            .append(idNode.convertToPython()).append("(");
        result.append(functionDefParamsNode.convertToPython())
            .append("):\n");
        
        bodyNode.depth = depth + 1;
        result.append(bodyNode.convertToPython());

        result.append(ProgramNode.addTabs("", depth))
            .append("\n");
        
        return result.toString();
    }

    @Override
    public boolean validateTree() throws ErrorPrinter {
        if (SymTable.funcTable.containsKey(idNode.idToken.getToken())) {
            throw new ErrorPrinter(idNode.idToken.getToken() + " already exists", idNode.idToken.getFilename(), idNode.idToken.getLineNum(), ErrorType.SEMANTIC);
        }
        idNode.validateTree();
        SymTable.functionName = idNode.idToken.getToken();
        SymTable.funcTable.put(SymTable.functionName, new ArrayList<String>());
        SymTable.varTable.put(SymTable.functionName, new HashMap<>());

        functionDefParamsNode.validateTree();
        funcReturnNode.validateTree();
        boolean isReturnable = SymTable.funcTable.get(SymTable.functionName).get(SymTable.funcTable.get(SymTable.functionName).size() - 1).equals("Void") ? false : true; // checks the return type of a function and checks if it is void
        // if it is void then the function should return nothing. Else it is a returnable function. In the func table the return type is the last element in the hash table.
        if (bodyNode.isReturnable() != isReturnable) {
            if (isReturnable) {
                throw new ErrorPrinter("Function " + idNode.idToken.getToken() + " expected a return", idNode.idToken.getFilename(), idNode.idToken.getLineNum(), ErrorType.SEMANTIC);
            } else {
                throw new ErrorPrinter("Function " + idNode.idToken.getToken() + " doesn't expect return", idNode.idToken.getFilename(), idNode.idToken.getLineNum(), ErrorType.SEMANTIC);
            }
        }

        bodyNode.validateTree();
        return true;
    }

    static FuncDefNode parseFunctionDefNode(ArrayList<Token> tokens) throws ErrorPrinter {
        if (!tokens.get(0).getToken().equals("def")) {
            throw new ErrorPrinter("Expected def", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);
        IdNode idNode = IdNode.parseIdNode(tokens);

        if (tokens.get(0).getTokenType() != TokenType.L_BRACKET) {
            throw new ErrorPrinter("Expected [", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);

        FuncDefParamsNode functionDefParamsNode = FuncDefParamsNode.parseFunctionDefParamsNode(tokens);

        if (tokens.get(0).getTokenType() != TokenType.R_BRACKET) {
            throw new ErrorPrinter("Expected ]", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);

        if (tokens.get(0).getTokenType() != TokenType.COLON) {
            throw new ErrorPrinter("Expected :", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);

        FuncReturnNode funcReturnNode = FuncReturnNode.parseFuncReturn(tokens);

        if (tokens.get(0).getTokenType() != TokenType.L_BRACE) {
            throw new ErrorPrinter("Expected {", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);

        BodyNode bodyNode = BodyNode.parseBodyNode(tokens);

        if (tokens.get(0).getTokenType() != TokenType.R_BRACE) {
            throw new ErrorPrinter("Expected } but got " + tokens.get(0).getToken(),
                    tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);

        return new FuncDefNode(idNode, functionDefParamsNode, funcReturnNode, bodyNode);
    }

}
