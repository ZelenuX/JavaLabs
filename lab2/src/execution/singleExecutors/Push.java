package execution.singleExecutors;

import execution.ExecutionContext;
import execution.SingleExecutor;
import execution.exceptions.LackOfArguments;
import execution.exceptions.UndefinedVariable;

import java.io.*;
import java.util.Scanner;

public class Push implements SingleExecutor {
    public void execute(ExecutionContext context) throws LackOfArguments, UndefinedVariable {
        Scanner input = context.getInputScanner();
        if (!input.hasNextDouble()) {
            if (!input.hasNext())
                throw new LackOfArguments();
            context.putArg(context.getDefined(input.next()));
            return;
        }
        context.putArg(input.nextDouble());
    }
}
