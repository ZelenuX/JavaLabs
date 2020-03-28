package execution;

public interface ExecutorsBuilder {
    SingleExecutor getExecutor(String commandName);
}
