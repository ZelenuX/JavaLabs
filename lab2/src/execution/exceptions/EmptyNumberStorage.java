package execution.exceptions;

public class EmptyNumberStorage extends ExecutionException {
    public String getMessage(){
        return "ERROR: trying to get number from empty executor storage.";
    }
}
