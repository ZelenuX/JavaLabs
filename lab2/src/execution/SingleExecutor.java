package execution;

import execution.exceptions.*;

public interface SingleExecutor {
    void execute(ExecutionContext context) throws EmptyNumberStorage, LackOfArguments,
            UndefinedVariable, ExitCommandEntered, InvalidArgument;
}
