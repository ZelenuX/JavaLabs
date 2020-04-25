package com.company;
import theGame.*;

import java.util.Timer;
import java.io.IOException;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) {
        final int sleepMs = 5;
        int k = 1;
        BallsField field = new SquaresAndBallsField(40 * k, 6 * k, 8 * k, 22 * k);
        Controller controller = new Controller(field);
        UserInterface userInterface;
        try {
            userInterface = new MyGUI(20*40, 18*40, controller);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        field.setUI(userInterface);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    userInterface.nextMoment((double) sleepMs / 1000);
                } catch (WinException | FailException e) {}
                userInterface.update();
            }
        }, 0, sleepMs);
    }
}
