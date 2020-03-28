package execution.exceptions;

public class InvalidArgument extends ExecutionException {
    public String getMessage(){
        return "ERROR: invalid argument.";
    }
}
