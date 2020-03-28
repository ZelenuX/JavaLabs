package execution;

import execution.exceptions.ExitCommandEntered;

import java.io.InputStream;

public interface CommandFlowExecutor {
    void executeAll() throws ExitCommandEntered;
}
