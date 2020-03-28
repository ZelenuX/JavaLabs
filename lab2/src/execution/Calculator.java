package execution;

import execution.exceptions.ExecutionException;
import execution.exceptions.ExitCommandEntered;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Calculator implements CommandFlowExecutor{
    private final ExecutionContext context;
    private final Scanner input;
    private final ExecutorsBuilder executorsBuilder;

    public Calculator(InputStream executorInput, OutputStream executorOutput, InputStream config) throws IOException {
        this.context = new StackArgsContext(executorInput, executorOutput);
        input = context.getInputScanner();
        executorsBuilder = new CalculatorExecutorsBuilder(config);
    }
    public void executeAll() throws ExitCommandEntered {
        SingleExecutor cur;
        String command;
        while (input.hasNext()){
            command = input.next();
            cur = executorsBuilder.getExecutor(command);
            if (cur == null) {
                System.err.println("ERROR: operation \"" + command + "\" does not exist.");
            }
            else{
                try {
                    cur.execute(context);
                }
                catch (ExitCommandEntered e){
                    throw e;
                }
                catch (ExecutionException e){
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
