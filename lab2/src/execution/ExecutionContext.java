package execution;

import execution.exceptions.EmptyNumberStorage;
import execution.exceptions.UndefinedVariable;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public interface ExecutionContext {
    double getArg() throws EmptyNumberStorage;
    double extractArg() throws EmptyNumberStorage;
    void putArg(double value);
    InputStream getInputStream();
    Scanner getInputScanner();
    OutputStream getOutputStream();
    double getDefined(String name) throws UndefinedVariable;
    void setDefined(String name, double value);
}
