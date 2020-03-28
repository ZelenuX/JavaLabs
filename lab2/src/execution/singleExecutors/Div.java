package execution.singleExecutors;

import execution.ExecutionContext;
import execution.SingleExecutor;
import execution.exceptions.EmptyNumberStorage;

public class Div implements SingleExecutor {
    public void execute(ExecutionContext context) throws EmptyNumberStorage {
        context.putArg(context.extractArg() / context.extractArg());
    }
}
