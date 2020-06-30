package theGame;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class TwoPlayersNetInterface implements UserInterface {
    private ServerSocket connectionListener;
    private Socket leftPlayer, rightPlayer;
    private String leftName, rightName, playerName;
    private ObjectOutputStream toLeft, toRight;
    private ObjectInputStream fromLeft, fromRight;
    private Message message = new Message();
    private Queue<Message> inputLeftMessages = new LinkedList<Message>();
    private Queue<Message> inputRightMessages = new LinkedList<Message>();
    private Controller controller;
    private boolean gameEnded = false;
    private Thread leftReceiver, rightReceiver;

    public TwoPlayersNetInterface(Controller gameController, ServerSocket serverSocket) throws IOException {
        connectionListener = serverSocket;
        leftPlayer = connectionListener.accept();
        toLeft = new ObjectOutputStream(leftPlayer.getOutputStream());
        fromLeft = new ObjectInputStream(leftPlayer.getInputStream());
        try {
            message = Message.recv(leftPlayer, fromLeft);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        leftName = message.textMessage;
        message.setText("Waiting second player...");
        //toLeft.writeUnshared(message);
        Message.send(message, leftPlayer, toLeft);
        rightPlayer = connectionListener.accept();
        toRight = new ObjectOutputStream(rightPlayer.getOutputStream());
        fromRight = new ObjectInputStream(rightPlayer.getInputStream());
        try {
            //message = (Message) fromRight.readObject();
            message = Message.recv(rightPlayer, fromRight);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        rightName = message.textMessage;
        playerName = leftName + '&' + rightName;
        message.setText("");
        Message.send(message, rightPlayer, toRight);
        //toRight.writeUnshared(message);
        this.controller = gameController;
        leftReceiver = new Thread(new Runnable() {
            @Override
            public void run() {
                Message mes = null;
                while(true){
                    try {
                        //mes = (Message) fromLeft.readObject();
                        mes = Message.recv(leftPlayer, fromLeft);
                    } catch (SocketException | EOFException e) {
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (mes.mesType == 5 && mes.speed > 0)
                        mes.speed *= -1;
                    synchronized (inputLeftMessages){
                        inputLeftMessages.add(mes);
                    }
                }
            }
        });
        leftReceiver.setDaemon(true);
        leftReceiver.start();
        rightReceiver = new Thread(new Runnable() {
            @Override
            public void run() {
                Message mes = null;
                while(true){
                    try {
                        //mes = (Message) fromRight.readObject();
                        mes = Message.recv(rightPlayer, fromRight);
                    } catch (SocketException | EOFException e) {
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (mes.mesType == 5 && mes.speed < 0)
                        mes.speed *= -1;
                    synchronized (inputRightMessages){
                        //todo remove
                        if (mes == null)
                            System.err.println("WHAAAAAAAT?");
                        inputRightMessages.add(mes);
                    }
                }
            }
        });
        rightReceiver.setDaemon(true);
        rightReceiver.start();
    }
    @Override
    public void drawRectangle(double x, double y, double width, double height, int type) throws IOException {
        message.setRectangle(x, y, width, height, type);
        Message.send(message, leftPlayer, toLeft);
        Message.send(message, rightPlayer, toRight);
        //toLeft.writeUnshared(message);
        //toRight.writeUnshared(message);
    }
    @Override
    public void drawOval(double x, double y, double width, double height, int type) throws IOException {
        message.setOval(x, y, width, height, type);
        Message.send(message, leftPlayer, toLeft);
        Message.send(message, rightPlayer, toRight);
        //toLeft.writeUnshared(message);
        //toRight.writeUnshared(message);
    }
    @Override
    public void nextMoment(double passedSeconds) throws FailException, WinException, IOException {
        if (gameEnded)
            return;
        try {
            synchronized (inputLeftMessages) {
                while (!inputLeftMessages.isEmpty()) {
                    message = inputLeftMessages.poll();
                    switch (message.mesType) {
                        case 5:
                            if (message.speed == 0) controller.stopPlatform();
                            if (message.speed < 0) controller.platformMoveLeft();
                            if (message.speed > 0) controller.platformMoveRight();
                            break;
                    }
                }
            }
            synchronized (inputRightMessages) {
                while (!inputRightMessages.isEmpty()) {
                    message = inputRightMessages.poll();
                    switch (message.mesType) {
                        case 5:
                            if (message.speed == 0) controller.stopPlatform();
                            if (message.speed < 0) controller.platformMoveLeft();
                            if (message.speed > 0) controller.platformMoveRight();
                            break;
                    }
                }
            }
            controller.nextMoment(passedSeconds);
        } catch (WinException e){
            try {
                end("WIN");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            throw e;
        } catch (FailException e){
            try {
                end("FAIL(" + (double) (int) (controller.getScore() * 100 * 1000) / 1000 + "%)");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            throw e;
        }
    }
    @Override
    public void update() {

    }
    @Override
    public void end(String message) throws IOException {
        RecordTable recordTable = new RecordTable("C:\\Users\\il_ya\\IdeaProjects\\lab3\\src\\theGame\\results.txt");
        String[] timeAndResult = recordTable.getTimeAndResult(playerName);
        if (timeAndResult == null || (double)(int)(controller.getScore() * 100000) / 1000 > Double.valueOf(timeAndResult[1].split("%")[0]))
            recordTable.set(playerName,
                    (double)(int)(controller.getFullTime() * 100) / 100 + "s", (double)(int)(controller.getScore() * 100000) / 1000 + "%");
        gameEnded = true;
        Vector<String> colNames = new Vector<>();
        colNames.add("player name");
        colNames.add("time");
        colNames.add("result");
        this.message.setRecordTable(colNames, recordTable.getStrs(), controller.getScore(), controller.getFullTime());
        Message.send(this.message, leftPlayer, toLeft);
        Message.send(this.message, rightPlayer, toRight);
        //toLeft.writeUnshared(this.message);
        //toRight.writeUnshared(this.message);
        recordTable.leaveBest(5);
        recordTable.updateFile();

        toLeft.close();
        toRight.close();
        fromLeft.close();
        fromRight.close();
        leftPlayer.close();
        rightPlayer.close();
    }
    @Override
    public String getName() {
        return null;
    }
}
