package Nodes;

import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

public class ParamsTNode implements JottTree {
    ExpressionNode eNode;

    public ParamsTNode(ExpressionNode eNode) {
        this.eNode = eNode;
    }

    @Override
    public String convertToJott() {
        StringBuilder result = new StringBuilder();
        result.append(", ");
        result.append(eNode.convertToJott());
        return result.toString();
    }

    @Override
    public String convertToJava(String className) {
        return ", " + eNode.convertToJava(className);
    }

    @Override
    public String convertToC() {
        return ", " + eNode.convertToC();
    }

    @Override
    public String convertToPython() {
        return ", " + eNode.convertToPython();
    }

    @Override
    public boolean validateTree() throws ErrorPrinter {
        eNode.validateTree();
        return true;
    }

    static ParamsTNode parseParamsTNode(ArrayList<Token> tokens) throws ErrorPrinter {
        if (tokens.get(0).getTokenType() != TokenType.COMMA) {
            throw new ErrorPrinter("Expected a ,", tokens.get(0).getFilename(), tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }
        tokens.remove(0);
        ExpressionNode eNode = ExpressionNode.parseExpressionNode(tokens);

        if (eNode == null) {
            throw new ErrorPrinter("Not a valid expression node", tokens.get(0).getFilename(),
                    tokens.get(0).getLineNum(), ErrorType.SYNTAX);
        }

        return new ParamsTNode(eNode);

    }

}
