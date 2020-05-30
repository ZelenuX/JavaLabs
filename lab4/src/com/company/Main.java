package com.company;

import factory.Factory;
import gui.MyGUI;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        Properties fabricProperties = new Properties();
        fabricProperties.load(new FileInputStream("C:\\Users\\il_ya\\IdeaProjects\\lab4\\src\\com\\company\\config.txt"));
        PrintStream logStream = null;
        if (fabricProperties.getProperty("LogSale").equals("true")) {
            logStream = System.out;
        }
        else{
            if (!fabricProperties.getProperty("LogSale").equals("false"))
                throw new IOException();
        }
        Factory factory = new Factory(Integer.parseInt(fabricProperties.getProperty("StorageBodySize")),
                Integer.parseInt(fabricProperties.getProperty("StorageMotorSize")),
                Integer.parseInt(fabricProperties.getProperty("StorageAccessorySize")),
                Integer.parseInt(fabricProperties.getProperty("StorageAutoSize")),
                Integer.parseInt(fabricProperties.getProperty("AccessorySuppliers")),
                Integer.parseInt(fabricProperties.getProperty("Workers")),
                Integer.parseInt(fabricProperties.getProperty("Dealers")),
                logStream);
        MyGUI gui = new MyGUI(factory, 250);
        factory.start();
        gui.setVisible(true);
    }
}
