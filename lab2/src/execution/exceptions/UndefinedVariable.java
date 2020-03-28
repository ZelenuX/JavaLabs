package execution.exceptions;

public class UndefinedVariable extends ExecutionException {
    private String varName = "";
    private UndefinedVariable(){}
    public UndefinedVariable(String varName){
        this.varName = varName;
    }
    public String getMessage(){
        return "ERROR: variable \"" + varName + "\" is undefined.";
    }
}
