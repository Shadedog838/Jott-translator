import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import Error.ErrorPrinter;
import provided.JottParser;
import provided.JottTokenizer;
import provided.JottTree;
import provided.Token;

public class Jott {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Error: Must be three arguments");
            System.out.println("Usage: <Jott file> <output file> <language>");
        } else {
            String fileName = args[0];
            String outputFileName = args[1];
            String outputLanguage = args[2];
            ArrayList<Token> tokens = JottTokenizer.tokenize(fileName);
            if (tokens != null && !tokens.isEmpty()) {
                JottTree jottTree = JottParser.parse(tokens);
                if (jottTree != null) {
                    String outputCode = "";
                    try {
                        jottTree.validateTree();
                        String fileExtension = outputFileName.substring(outputFileName.lastIndexOf("."), outputFileName.length());
                        switch (outputLanguage) {
                            case "Jott":
                                if (!fileExtension.equals(".jott")) {
                                    System.err.println("The output file must be a .jott file if converting to Jott");
                                    System.exit(-1);
                                }
                                else {
                                    outputCode = jottTree.convertToJott();
                                }
                                break;
                            case "C":
                                if (!fileExtension.equals(".c")) {
                                    System.err.println("The output file must be a .c file if converting to C");
                                    System.exit(-1);
                                }
                                else {
                                    outputCode = jottTree.convertToC();
                                }
                                break;
                            case "Python":
                                if (!fileExtension.equals(".py")) {
                                    System.err.println("The output file must be a .py file if converting to Python");
                                    System.exit(-1);
                                }
                                else {
                                    outputCode = jottTree.convertToPython();
                                }
                                break;
                            case "Java":
                                if (!fileExtension.equals(".java")) {
                                    System.err.println("The output file must be a .java file if converting to Java");
                                    System.exit(-1);
                                }
                                else {
                                    String className = outputFileName.substring(0, outputFileName.lastIndexOf("."));
                                    outputCode = jottTree.convertToJava(className);
                                }
                                break;
                            default:
                                System.err.println("Sorry we are unable to convert to " + outputLanguage);
                                System.exit(-1);
                        }
                        FileWriter fileWriter = new FileWriter(outputFileName);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write(outputCode);
                        bufferedWriter.close();
                    } catch (ErrorPrinter errorPrinter) {
                        errorPrinter.printErrorMessage();
                    } catch (IOException ioException) {
                        System.err.println("Failed to create output file");
                    }
                }
            } else {
                System.err.println("Unable to find file or the file is empty or file failed to tokenize");
            }

        }
    }
}
