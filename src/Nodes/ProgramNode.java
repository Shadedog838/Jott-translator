package Nodes;

import provided.Token;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.JottTree;

public class ProgramNode implements JottTree {

    ArrayList<FuncDefNode> functionDefNodes;
    static Token lastToken = null;


    public ProgramNode(ArrayList<FuncDefNode> functionDefNodes) {
        this.functionDefNodes = functionDefNodes;
    }

    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        while (functionDefNodes.size() > 0) {
            result.append(this.functionDefNodes.remove(0).convertToJott());
        }
        return result.toString();
    }

    @Override
    public String convertToJava(String className) {
        StringBuilder result = new StringBuilder("public class ").append(className).append(" {\n");
        for (FuncDefNode node : functionDefNodes) {
            node.depth = 1;
            result.append(node.convertToJava(className));
        }
        result.append("}\n");
        return result.toString();
    }

    @Override
    public String convertToC() {
        createCHeaderFiles();
        StringBuilder result = new StringBuilder("#include \"convertToC.h\"\n");
        for (FuncDefNode node : functionDefNodes) {
            result.append(node.convertToC());
        }
        return result.toString();
    }

    @Override
    public String convertToPython() {
        StringBuilder result = new StringBuilder();
        for (FuncDefNode node : functionDefNodes) {
            result.append(node.convertToPython());
        }
        result.append("\nif __name__ == \"__main__\":\n\tmain()");

        return result.toString();
    }

    @Override
    public boolean validateTree() throws ErrorPrinter {
        SymTable.intializeSymTables();
        boolean isMainDefined = false;

        for (FuncDefNode funcDefNode: functionDefNodes) {
            if (funcDefNode.idNode.idToken.getToken().equals("main")) {
                funcDefNode.isMainDefinedCorrectly();
                isMainDefined = true;
            }
            funcDefNode.validateTree();
        }
        if (isMainDefined == false) {
            throw new ErrorPrinter("main not defined", lastToken.getFilename(), lastToken.getLineNum(), ErrorType.SEMANTIC);
        }
        return true;
    }

    public static void createCHeaderFiles() {
        try {
            String convertToCFile = "convertToC.h";
            FileWriter fw = new FileWriter(convertToCFile);
            BufferedWriter bw = new BufferedWriter(fw);
            String concatFuncStr = "String concat(String s1, String s2){size_t len1 = strlen(s1);size_t len2 = strlen(s2); size_t totalLen = len1 + len2; String result = (String)malloc(totalLen + 1); strcpy(result, s1); strcat(result, s2); return result;}";
            String lenFuncStr = "Integer length(String s1){size_t result = strlen(s1); return result;}";
            String printFuncStr = "void printStr(String x){printf(\"%s\\n\", x);}";
            String printFuncInt = "void printInt(Integer x){printf(\"%d\\n\", x);}";
            String printFuncDouble = "void printDouble(Double x){printf(\"%f\\n\", x);}";
            String printFuncBoolean = "void printBoolean(Boolean x){printf(\"%s\\n\", x ? \"true\" : \"false\");}";
            ArrayList<String> headers = new ArrayList<>(
                Arrays.asList(
                "#include <stdio.h>",
                "#include <string.h>",
                "#include <stdlib.h>",
                "#include <stdbool.h>",
                "#define Integer int",
                "#define String char*",
                "#define Boolean bool",
                "#define Double double",
                printFuncStr,
                printFuncInt,
                printFuncDouble,
                printFuncBoolean,
                concatFuncStr,
                lenFuncStr
            )
            );
            for (String header : headers) {
                bw.write(header + "\n");
            }
            bw.close();

        } catch(Exception ex) {

        }
    }

    public static ProgramNode parseProgramNode(ArrayList<Token> tokens) {
        ArrayList<FuncDefNode> functionDefNodes = new ArrayList<>();
        lastToken = tokens.get(tokens.size()-1);
        try {
            while (tokens.size() > 0) {
                FuncDefNode functionDefNode = FuncDefNode.parseFunctionDefNode(tokens);
                functionDefNodes.add(functionDefNode);
            }

            return new ProgramNode(functionDefNodes);
        } catch (ErrorPrinter errorPrinter) {
            errorPrinter.printErrorMessage();
            return null;
        }
        catch (IndexOutOfBoundsException e){
            System.err.println("Syntax Error: unexpected EOF detected");
            return null;
        }
    }

    public static String addTabs(String string, int depth) {
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int i = 0; i < depth; ++i) {
            stringBuilder.append("\t");
        }
        return stringBuilder.toString();
    }
}
