package com.company;
import theGame.*;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.io.IOException;
import java.util.TimerTask;

public class Main {

    private static class ServerSocketSingleton {
        private static volatile ServerSocket ss = null;
        private ServerSocketSingleton(){}
        public static ServerSocket getServerSocket() throws IOException {
            if (ss == null)
                synchronized (ServerSocketSingleton.class){
                    if (ss == null)
                        ss = new ServerSocket(4567);
                }
            return ss;
        }
    }

    private static volatile boolean isFirstProcess = true;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (isFirstProcess){
            isFirstProcess = false;
            Thread stopperListeningThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ServerSocket stopListener = null;
                    try {
                        stopListener = new ServerSocket(4568);
                        Socket s = null;
                        ObjectInputStream ois = null;
                        while (true) {
                            if (s != null)
                                s.close();
                            s = stopListener.accept();
                            if (ois != null)
                                ois.close();
                            ois = new ObjectInputStream(s.getInputStream());
                            //if (((Message) ois.readObject()).textMessage.equals("Die immediately!"))
                            if (Message.recv(s, ois).textMessage.equals("Die immediately!"))
                                System.exit(0);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(6);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        System.exit(7);
                    }
                }
            });
            stopperListeningThread.start();
        }
        final int sleepMs = 5;
        int k = 1;
        BallsField field = new SquaresAndBallsField(40 * k, 6 * k, 8 * k, 22 * k);
        Controller controller = new Controller(field);
        UserInterface userInterface = null;
        boolean corruptedConnection = false;
        try {
            userInterface = new TwoPlayersNetInterface(controller, ServerSocketSingleton.getServerSocket());
            field.setUI(userInterface);
        } catch (IOException e) {
            System.out.println("Connection corrupted.");
            corruptedConnection = true;
            e.printStackTrace();
        }

        if (!corruptedConnection) {
            Timer timer = new Timer();
            UserInterface finalUserInterface = userInterface;
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        finalUserInterface.nextMoment((double) sleepMs / 1000);
                    } catch (WinException | FailException e) {

                    } catch (SocketException | EOFException e) {
                        timer.cancel();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finalUserInterface.update();
                }
            }, 0, sleepMs);
        }

        Timer newThreadDelay = new Timer();
        newThreadDelay.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Main.main(args);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, 250);
    }
}
