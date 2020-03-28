package execution.singleExecutors;

import execution.ExecutionContext;
import execution.SingleExecutor;
import execution.exceptions.EmptyNumberStorage;

import java.io.OutputStream;
import java.io.PrintStream;

public class Print implements SingleExecutor {
    private PrintStream output = null;
    private OutputStream curOutputStream = null;

    public void execute(ExecutionContext context) throws EmptyNumberStorage {
        if (context.getOutputStream() != curOutputStream){
            curOutputStream = context.getOutputStream();
            output = new PrintStream(curOutputStream);
        }
        output.println(context.getArg());
    }
}
