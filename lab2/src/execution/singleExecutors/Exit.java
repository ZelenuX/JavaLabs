package execution.singleExecutors;

import execution.ExecutionContext;
import execution.SingleExecutor;
import execution.exceptions.ExitCommandEntered;

public class Exit implements SingleExecutor {
    public void execute(ExecutionContext context) throws ExitCommandEntered{
        throw new ExitCommandEntered();
    }
}
