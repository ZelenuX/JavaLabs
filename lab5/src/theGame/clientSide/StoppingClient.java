package theGame.clientSide;

import theGame.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class StoppingClient {
    public static void main(String[] args) {
        Socket s = null;
        ObjectOutputStream os = null;
        try {
            s = new Socket("127.0.0.1", 4568);
            Message m = new Message();
            m.setText("Die immediately!");
            os = new ObjectOutputStream(s.getOutputStream());
            //os.writeUnshared(m);
            Message.send(m, s, os);
        } catch (IOException e) {
            System.out.println("Couldn't stop server.");
            return;
        }
        try {
            os.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
