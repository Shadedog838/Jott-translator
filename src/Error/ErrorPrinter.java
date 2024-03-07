package Error;

public class ErrorPrinter extends Throwable {
    public enum ErrorType {
        SEMANTIC("Semantic Error"), SYNTAX("Syntax Error");

        private final String displayValue;

        ErrorType(String displayValue) {
            this.displayValue = displayValue;
        }

        @Override
        public String toString() {
            return displayValue;
        }
    }

    private final String message;
    private final String fileName;
    private final int lineNum;
    private final ErrorType type;

    public ErrorPrinter(String message, String fileName, int lineNum, ErrorType type) {
        this.message = message;
        this.fileName = fileName;
        this.lineNum = lineNum;
        this.type = type;
    }

    public void printErrorMessage() {
        System.err.println(type + ":\n" +
                message + "\n" +
                fileName + ":" + lineNum);
    }
}

