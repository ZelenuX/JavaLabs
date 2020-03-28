package com.company;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    static final InputStream systemIn = System.in;
    static final ByteArrayOutputStream output = new ByteArrayOutputStream();
    static final PrintStream systemOut = System.out;
    static final ByteArrayOutputStream error = new ByteArrayOutputStream();
    static final PrintStream systemErr = System.err;

    @BeforeAll
    static void beforeTests(){
        System.setOut(new PrintStream(output));
        System.setErr(new PrintStream(error));
    }
    @BeforeEach
    void beforeEach(){
        output.reset();
        error.reset();
    }
    @AfterAll
    static void afterTests(){
        System.setIn(systemIn);
        System.setOut(systemOut);
        System.setErr(systemErr);
    }

    static void checkFunc(String in, String out, String err){
        System.setIn(new ByteArrayInputStream(in.getBytes()));
        Main.main(new String[]{});
        assertEquals(err, error.toString());
        assertEquals(out, output.toString());
    }

    @Test
    void testSimple0(){
        checkFunc("PUSH 0 PUSH -9 / PRINT", "-Infinity\r\n", "");
    }
    @Test
    void testSimple1(){
        checkFunc("DEFINE b 100 DEFINE a 5 PUSH a DEFINE a 0,5 PUSH a PUSH 10 PUSH b PUSH 1000 - + * / PRINT PRINT", "91.0\r\n91.0\r\n", "");
    }
    @Test
    void testSimple2(){
        checkFunc("PUSH 1,44 SQRT PRINT", "1.2\r\n", "");
    }
    @Test
    void testError0(){
        checkFunc("+ PRINT PUSH 5 PUSH 7 - PRINT POP PRINT POP +", "2.0\r\n",
                "ERROR: trying to get number from empty executor storage.\r\n"
                        + "ERROR: trying to get number from empty executor storage.\r\n"
                        + "ERROR: trying to get number from empty executor storage.\r\n"
                        + "ERROR: trying to get number from empty executor storage.\r\n"
                        + "ERROR: trying to get number from empty executor storage.\r\n");
    }
    @Test
    void testError1(){
        checkFunc("DEFINE a", "", "ERROR: not enough arguments for operation.\r\n");
        beforeEach();
        checkFunc("PUSH", "", "ERROR: not enough arguments for operation.\r\n");;
    }
    @Test
    void testError2(){
        checkFunc("Unknown command .", "", "ERROR: operation \"Unknown\" does not exist.\r\n"
                + "ERROR: operation \"command\" does not exist.\r\n"
                + "ERROR: operation \".\" does not exist.\r\n");
    }
    @Test
    void testError3(){
        checkFunc("PUSH a PUSH POP", "", "ERROR: variable \"a\" is undefined.\r\n"
                + "ERROR: variable \"POP\" is undefined.\r\n");
    }
    @Test
    void testError4(){
        checkFunc("PUSH 11 DEFINE 7 5 DEFINE 9 a PRINT", "11.0\r\n",
                "ERROR: invalid argument.\r\nERROR: invalid argument.\r\n");
    }
    @Test
    void testEnd(){
        checkFunc("PUSH 5 PUSH 9 - PRINT PRINT EXIT PRINT", "4.0\r\n4.0\r\n", "");
    }
    @Test
    void testFile0(){
        Main.main(new String[]{"src/com/company/nothing"});
        assertEquals("ERROR: file \"src/com/company/nothing\" not found.\r\n", error.toString());
        assertEquals("", output.toString());
    }
    @Test
    void testFile1(){
        Main.main(new String[]{"src/com/company/testInput.txt"});
        assertEquals("", error.toString());
        assertEquals("12.0\r\n", output.toString());
    }

}