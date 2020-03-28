package execution.singleExecutors;

import execution.ExecutionContext;
import execution.SingleExecutor;
import execution.exceptions.InvalidArgument;
import execution.exceptions.LackOfArguments;
import execution.exceptions.UndefinedVariable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class Define implements SingleExecutor{
    public void execute(ExecutionContext context) throws UndefinedVariable, LackOfArguments, InvalidArgument {
        Scanner input = context.getInputScanner();
        if (input.hasNextDouble()){
            input.next();
            input.next();
            throw new InvalidArgument();
        }
        if (!input.hasNext())
            throw new LackOfArguments();
        String name = input.next();
        if (input.hasNextDouble()){
            context.setDefined(name, input.nextDouble());
        }
        else{
            if (!input.hasNext())
                throw new LackOfArguments();
            context.setDefined(name, context.getDefined(input.next()));
        }
    }
}
