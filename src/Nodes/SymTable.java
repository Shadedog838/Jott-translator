package Nodes;
import java.util.*;

public class SymTable {

    public static String functionName = null;

    public static HashMap<String, ArrayList<String>> funcTable = new HashMap<>();

    public static HashMap<String, HashMap<String, String>> varTable = new HashMap<>();

    public static List<String> unitVars = new ArrayList<>();

    public static void intializeSymTables() {
        SymTable.funcTable.put("print", new ArrayList<String>(Arrays.asList("Any", "Void")));
        SymTable.funcTable.put("concat", new ArrayList<String>(Arrays.asList("String", "String", "String")));
        SymTable.funcTable.put("length", new ArrayList<String>(Arrays.asList( "String", "Integer")));
    }
}
