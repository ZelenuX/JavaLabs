package theGame.clientSide;

import theGame.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.Timer;

public class Client {
    public static void main(String[] args) throws IOException {
        ClientController controller = null;
        try {
            controller = new ClientController("127.0.0.1", 4567);
        }
        catch (IOException e){
            System.out.println("Can not connect to the server.");
            System.exit(3);
        }
        MyGUI userInterface = new MyGUI(20*20, 18*20, controller){
            private double drawEmptyOvalX, drawEmptyOvalY, drawEmptyOvalWidth, drawEmptyOvalHeight;
            private boolean drawEmptyOval = false;
            { recordTablePath = "C:\\Users\\il_ya\\IdeaProjects\\lab3\\src\\theGame\\remoteResults.txt"; }
            @Override
            public void drawOval(double x, double y, double width, double height, int type) {
                if (type == OvalTypes.EMPTY) {
                    drawEmptyOval = true;
                    drawEmptyOvalX = x;
                    drawEmptyOvalY = y;
                    drawEmptyOvalWidth = width;
                    drawEmptyOvalHeight = height;
                }
                else {
                    if (drawEmptyOval)
                        super.drawOval(drawEmptyOvalX, drawEmptyOvalY, drawEmptyOvalWidth, drawEmptyOvalHeight, OvalTypes.EMPTY);
                    super.drawOval(x, y, width, height, type);
                }
            }
        };
        String msg = null;
        try {
            controller.setUI(userInterface);
            controller.sendMessage(userInterface.getName());
            msg = controller.getMessage().textMessage;
        }
        catch (IOException e){
            System.out.println("Connection lost.");
            System.exit(4);
        }
        if (msg.equals("")) {
            controller.setMaxTimesWithNoInputMessages(400);//2s
        }
        else {
            System.out.println(msg);
        }
        int sleepMs = 5;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!Thread.currentThread().isInterrupted()) {
                    try {
                        userInterface.nextMoment((double) sleepMs / 1000);
                    } catch (WinException | FailException e) {

                    } catch (ConnectionLostException e) {
                        System.out.println("Connection lost.");
                        timer.cancel();
                        System.exit(3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    userInterface.update();
                }
            }
        }, 0, sleepMs);
    }
}

class ClientController extends Controller {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Message message = new Message();
    private Queue<Message> inputMessages = new LinkedList<Message>();
    private UserInterface userInterface = null;
    private Thread messageReceiver;
    private boolean listeningThreadStarted = false;
    private double score = 0;
    private double fullTime = 0;
    private int timesWithNoInputMessages = 0;
    private int maxTimesWithNoInputMessages = 4000;//20s
    public void setMaxTimesWithNoInputMessages(int value){
        maxTimesWithNoInputMessages = value;
    }

    public ClientController(String host, int port) throws IOException {
        socket = new Socket(host, port);
        input = new ObjectInputStream(socket.getInputStream());
        output = new ObjectOutputStream(socket.getOutputStream());
        messageReceiver = new Thread(new Runnable() {
            @Override
            public void run() {
                Message mes = null;
                while (true){
                    try {
                        //mes = (Message) input.readObject();
                        mes = Message.recv(socket, input);
                    } catch (SocketException | EOFException e) {
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    synchronized (inputMessages){
                        inputMessages.add(mes);
                        timesWithNoInputMessages = 0;
                    }
                }
            }
        });
        messageReceiver.setDaemon(true);
    }
    public void sendMessage(String message) throws IOException {
        this.message.setText(message);
        //output.writeObject(this.message);
        Message.send(this.message, socket, output);
    }
    public Message getMessage() throws IOException {
        try {
            //return (Message)input.readObject();
            return Message.recv(socket, input);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void setUI(UserInterface ui){
        userInterface = ui;
    }
    @Override
    public void nextMoment(double passedSeconds) throws WinException, FailException, IOException {
        if (!listeningThreadStarted){
            listeningThreadStarted = true;
            messageReceiver.start();
        }
        synchronized (inputMessages) {
            if (inputMessages.isEmpty())
                ++timesWithNoInputMessages;
            if (timesWithNoInputMessages >= maxTimesWithNoInputMessages)
                throw new ConnectionLostException();
        }
        while (true) {
            synchronized (inputMessages) {
                if (inputMessages.isEmpty())
                    break;
                message = inputMessages.poll();
                maxTimesWithNoInputMessages = 400;//2s
            }
            if (message == null)
                continue;
            switch (message.mesType) {
                case 2:
                    userInterface.drawRectangle(message.x, message.y, message.width, message.height, message.type);
                    break;
                case 3:
                    userInterface.drawOval(message.x, message.y, message.width, message.height, message.type);
                    break;
                case 4:
                    input.close();
                    output.close();
                    socket.close();
                    System.out.println("Game ended.");
                    score = message.score;
                    fullTime = message.fullTime;
                    RecordTable.rewriteFile("C:\\Users\\il_ya\\IdeaProjects\\lab3\\src\\theGame\\remoteResults.txt", message.table);
                    userInterface.end("Game ended.");
                    Thread.currentThread().interrupt();
                    break;
                default:
                    System.out.println("Unexpected message type: " + message.mesType + ".");
            }
        }

        allPassedSeconds += passedSeconds;
        curPlatformSpeed *= 1.01;
        /*int tmp = needGodHelps;
        for (int j = needGodHelps; j > 0; --j)
            for (int i = 1; i < 10; ++i)
                field.addBall(i * 0.1, (field.highestBallStartPos() - field.lowestBallStartPos()) * 0.25 + field.lowestBallStartPos(),
                        curPlatformSpeed * 0.2 * (i - 5) * field.getCols(), curPlatformSpeed * field.getCols(), 0.5);
        needGodHelps -= tmp;*/
        fullTime += passedSeconds;
    }
    @Override
    public void platformMoveRight(){
        message.setPlatformSpeed(1);
        try {
            //output.writeUnshared(message);
            Message.send(message, socket, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void platformMoveLeft(){
        message.setPlatformSpeed(-1);
        try {
            //output.writeUnshared(message);
            Message.send(message, socket, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void stopPlatform(){
        message.setPlatformSpeed(0);
        try {
            //output.writeUnshared(message);
            Message.send(message, socket, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public double getScore(){
        return 0;
    }
    @Override
    public double getFullTime(){
        return 1000000000.0;
    }
}
