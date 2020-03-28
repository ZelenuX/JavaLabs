package com.company;

import execution.Calculator;
import execution.CommandFlowExecutor;
import execution.exceptions.ExitCommandEntered;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        InputStream config = CommandFlowExecutor.class.getResourceAsStream("config.conf");
        if (config == null) {
            System.err.println("can not find conf file");
            return;
        }
        InputStream executorInput = null;
        if (args.length >= 1){
            try {
                executorInput = new FileInputStream(args[0]);
            } catch (FileNotFoundException e) {
                System.err.println("ERROR: file \"" + args[0] + "\" not found.");
                return;
            }
        }
        else{
            executorInput = System.in;
        }
        executorInput = new BufferedInputStream(executorInput);
        CommandFlowExecutor executor = null;
        try {
            executor = new Calculator(executorInput, System.out, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            executor.executeAll();
        }
        catch (ExitCommandEntered e){
            //System.out.println("Application finished.");
        }
    }
}
