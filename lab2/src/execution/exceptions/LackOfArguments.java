package execution.exceptions;

public class LackOfArguments extends ExecutionException {
    public String getMessage(){
        return "ERROR: not enough arguments for operation.";
    }
}
