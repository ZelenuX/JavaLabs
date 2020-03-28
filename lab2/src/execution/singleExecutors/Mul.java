package execution.singleExecutors;

import execution.ExecutionContext;
import execution.SingleExecutor;
import execution.exceptions.EmptyNumberStorage;

public class Mul implements SingleExecutor {
    public void execute(ExecutionContext context) throws EmptyNumberStorage {
        context.putArg(context.extractArg() * context.extractArg());
    }
}
