package provided;

import java.io.BufferedReader;
import java.io.FileReader;
/**
 * This class is responsible for tokenizing Jott code.
 *
 * @author Paul Curcio (phc6515) Shandon Mith (sam8031) Hana Ho (hth2539) Claudia Snyder (cws8272) Daniel Rigoglioso (dsr5964)
 **/
import java.io.IOException;
import java.util.ArrayList;

import Error.ErrorPrinter;
import Error.ErrorPrinter.ErrorType;

public class JottTokenizer {

  private static TokenType determineTokenType(String tokenValue) {
    if (tokenValue.matches("^,$")) {
      return TokenType.COMMA;
    } else if (tokenValue.matches("^\\]$")) {
      return TokenType.R_BRACKET;
    } else if (tokenValue.matches("^\\[$")) {
      return TokenType.L_BRACKET;
    } else if (tokenValue.matches("^\\}$")) {
      return TokenType.R_BRACE;
    } else if (tokenValue.matches("^\\{$")) {
      return TokenType.L_BRACE;
    } else if (tokenValue.matches("^=$")) {
      return TokenType.ASSIGN;
    } else if (tokenValue.matches("^==$") || tokenValue.matches("^>$") || tokenValue.matches("^<$")
        || tokenValue.matches("^>=$") || tokenValue.matches("^<=$") || tokenValue.matches("^!=$")) {
      return TokenType.REL_OP;
    } else if (tokenValue.matches("\\/") || tokenValue.matches("\\+") ||
        tokenValue.matches("\\-") || tokenValue.matches("\\*")) {
      return TokenType.MATH_OP;
    } else if (tokenValue.matches("^;$")) {
      return TokenType.SEMICOLON;
    } else if (tokenValue.matches("\\.\\d+") || tokenValue.matches("^\\d+$") || tokenValue.matches("^\\d+\\.\\d*$")) {
      return TokenType.NUMBER;
    } else if (tokenValue.matches("^[a-zA-Z]+[a-zA-Z\\d]*$")) {
      return TokenType.ID_KEYWORD;
    } else if (tokenValue.matches("^:$")) {
      return TokenType.COLON;
    } else if (tokenValue.matches("^::$")) {
      return TokenType.FC_HEADER;
    } else if (tokenValue.matches("^\"[^\"]*\"$")) {
      return TokenType.STRING;
    }

    return null;
  }

  private static void addToken(ArrayList<Token> result, String value, String filename, int lineNum) {
    result.add(new Token(value, filename, lineNum, determineTokenType(value)));
  }

  /**
   * Takes in a filename and tokenizes that file into Tokens
   * based on the rules of the Jott Language
   *
   * @param filename the name of the file to tokenize; can be relative or absolute
   *                 path
   * @return an ArrayList of Jott Tokens
   */
  public static ArrayList<Token> tokenize(String filename) {
    ArrayList<Token> result = new ArrayList<>();
    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      int lineNum = 0;
      String lineStr = "";

      while ((lineStr = br.readLine()) != null) {
        char[] lineChars = lineStr.toCharArray();
        lineNum += 1;

        StringBuilder tokenBuilder = new StringBuilder();
        for (int i = 0; i < lineChars.length; i++) {
          char lineChar = lineChars[i];
          // Handles the white space. If it is white space continue
          if (Character.isWhitespace(lineChar)) {
            continue;
          }
          // Handles comments
          else if (lineChar == '#') {
            break;
          }
          // Handles commas,brackets,braces,mathOps,semicolon
          else if (lineChar == ',' || lineChar == ']' || lineChar == '[' || lineChar == '}' ||
              lineChar == '{' || lineChar == '+' || lineChar == '-' || lineChar == '*' ||
              lineChar == '/' || lineChar == ';') {
            addToken(result, String.valueOf(lineChar), filename, lineNum);
          }
          // Handles = / ==
          else if (lineChar == '=') {
            int nextCharIndex = i + 1;
            if (nextCharIndex < lineChars.length && lineChars[nextCharIndex] == '=') {
              addToken(result, "==", filename, lineNum);
              i = nextCharIndex;
            } else {
              addToken(result, "=", filename, lineNum);
            }
          }
          // Handles <> / <= / >=
          else if (lineChar == '<' || lineChar == '>') {
            int nextCharIndex = i + 1;
            if (nextCharIndex < lineChars.length && lineChars[nextCharIndex] == '=') {
              addToken(result, lineChar + "=", filename, lineNum);
              i = nextCharIndex;
            } else {
              addToken(result, String.valueOf(lineChar), filename, lineNum);
            }
          }
          // Handles digits starting with a period
          else if (lineChar == '.') {
            int nextCharIndex = i + 1;
            tokenBuilder.append(lineChar);
            if (nextCharIndex < lineChars.length && Character.isDigit(lineChars[nextCharIndex])) {
              while (nextCharIndex < lineChars.length && (Character.isDigit(lineChars[nextCharIndex]))) {
                tokenBuilder.append(lineChars[nextCharIndex]);
                nextCharIndex += 1;
              }
              i = nextCharIndex - 1;
              addToken(result, tokenBuilder.toString(), filename, lineNum);
              tokenBuilder.setLength(0);
            } else {
              new ErrorPrinter("Expected digit to follow '.'", filename, lineNum, ErrorType.SYNTAX).printErrorMessage();
              br.close();
              return null;
            }
          }
          // Handles digit and doubles
          else if (Character.isDigit(lineChars[i])) {
            int j = i; // this is a copy of i to modify
            while (j < lineChars.length && Character.isDigit(lineChars[j])) {
              tokenBuilder.append(lineChars[j]);
              j += 1;
            }
            if (j < lineChars.length && lineChars[j] == '.') {
              tokenBuilder.append(lineChars[j]);
              j += 1;
              while (j < lineChars.length && Character.isDigit(lineChars[j])) {
                tokenBuilder.append(lineChars[j]);
                j += 1;
              }
            }
            i = j - 1;
            addToken(result, tokenBuilder.toString(), filename, lineNum);
            tokenBuilder.setLength(0);
          }
          // Handles id / keywords
          else if (Character.isLetter(lineChar)) {
            int j = i; // this is a copy of i to modify
            while (j < lineChars.length && (Character.isLetterOrDigit(lineChars[j]))) {
              tokenBuilder.append(lineChars[j]);
              j += 1;
            }
            i = j - 1;
            addToken(result, tokenBuilder.toString(), filename, lineNum);
            tokenBuilder.setLength(0);
          }
          // Handles :
          else if (lineChar == ':') {
            int nextCharIndex = i + 1;
            if (nextCharIndex < lineChars.length && lineChars[nextCharIndex] == ':') {
              addToken(result, "::", filename, lineNum);
              i = nextCharIndex;
            } else {
              addToken(result, ":", filename, lineNum);
            }
          }
          // Handles !=
          else if (lineChar == '!') {
            int nextCharIndex = i + 1;
            if (nextCharIndex < lineChars.length && lineChars[nextCharIndex] == '=') {
              addToken(result, "!=", filename, lineNum);
              i = nextCharIndex;
            } else {
              new ErrorPrinter("Expected '=' after '!'", filename, lineNum, ErrorType.SYNTAX).printErrorMessage();
              br.close();
              return null;
            }
          }
          // Handles strings
          else if (lineChar == '"') {
            int endIndex = lineStr.indexOf('"', i + 1);
            if (endIndex == -1) {
              new ErrorPrinter("Expected closing '\"'", filename, lineNum, ErrorType.SYNTAX).printErrorMessage();
              br.close();
              return null;
            }
            String string = lineStr.substring(i, endIndex + 1);
            if (string.matches(".*[.,;?!].*")) {
              new ErrorPrinter("String caintains punctuation", filename, lineNum, ErrorType.SYNTAX).printErrorMessage();
              br.close();
              return null;
            }
            addToken(result, lineStr.substring(i, endIndex + 1), filename, lineNum);
            i = endIndex;
          } else {
            new ErrorPrinter("Unexpected character: " + lineChar, filename, lineNum, ErrorType.SYNTAX).printErrorMessage();
            br.close();
            return null;
          }
        }
      }
      br.close();
      return result;
    } catch (IOException e) {
      return result;
    }
  }
}
