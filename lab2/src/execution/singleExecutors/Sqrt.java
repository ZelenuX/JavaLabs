package execution.singleExecutors;

import execution.ExecutionContext;
import execution.SingleExecutor;
import execution.exceptions.EmptyNumberStorage;

public class Sqrt implements SingleExecutor {
    public void execute(ExecutionContext context) throws EmptyNumberStorage {
        context.putArg(Math.sqrt(context.extractArg()));
    }
}
