package execution;

import execution.exceptions.EmptyNumberStorage;
import execution.exceptions.UndefinedVariable;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.*;

public class StackArgsContext implements  ExecutionContext {
    private final Stack<Double> args = new Stack<Double>();
    private final Map<String, Double> defined = new HashMap<String, Double>();
    private final InputStream input;
    private final OutputStream output;
    private final Scanner inputScanner;

    public StackArgsContext(InputStream input, OutputStream output) throws InvalidParameterException {
        if (input == null || output == null)
            throw new InvalidParameterException("ERROR: null pointer passed to StackArgsContext constructor.");
        this.input = input;
        inputScanner = new Scanner(input);
        this.output = output;
    }
    public double getArg() throws EmptyNumberStorage {
        try {
            return args.peek();
        }
        catch (EmptyStackException e){
            throw new EmptyNumberStorage();
        }
    }
    public double extractArg() throws EmptyNumberStorage {
        try {
            return args.pop();
        }
        catch (EmptyStackException e){
            throw new EmptyNumberStorage();
        }
    }
    public void putArg(double value) {
        args.push(value);
    }
    public InputStream getInputStream() {
        return input;
    }
    public Scanner getInputScanner(){
        return inputScanner;
    }
    public OutputStream getOutputStream() {
        return output;
    }
    public double getDefined(String name) throws UndefinedVariable {
        Double res = defined.get(name);
        if (res == null)
            throw new UndefinedVariable(name);
        return res;
    }
    public void setDefined(String name, double value) {
        defined.put(name, value);
    }
}
