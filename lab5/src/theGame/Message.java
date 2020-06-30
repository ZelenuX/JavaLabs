package theGame;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Message implements Serializable {
    private static final boolean xmlMode = true; //true - xml messages, false - serialisation

    private static int curId = -1;
    public int id = ++curId;
    /**
     * mesType values:
     * 0 - empty
     * 1 - text message
     * 2 - draw rectangle
     * 3 - draw oval
     * 4 - record table
     * 5 - set platform speed
     * */
    public int mesType = 0;
    public String textMessage = null;
    public double x, y;
    public double height, width;
    public int type;
    public Vector<String> colNames = null;
    public Vector<Vector<String>> table = null;
    public double score, fullTime;
    public int speed;
    public void makeEmpty(){
        id = ++curId;
        mesType = 0;
        textMessage = null;
        colNames = null;
        table = null;
    }

    public void setText(String textMessage){
        makeEmpty();
        mesType = 1;
        this.textMessage = textMessage;
    }
    public void setRectangle(double x, double y, double width, double height, int type){
        makeEmpty();
        mesType = 2;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
    }
    public void setOval(double x, double y, double width, double height, int type){
        makeEmpty();
        mesType = 3;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
    }
    public void setRecordTable(Vector<String> colNames, Vector<Vector<String>> table, double score, double fullTime){
        makeEmpty();
        mesType = 4;
        this.colNames = colNames;
        this.table = table;
        this.score = score;
        this.fullTime = fullTime;
    }
    public void setPlatformSpeed(int speed){
        makeEmpty();
        mesType = 5;
        this.speed = speed;
    }

    public static void send(Message message, Socket socket, ObjectOutputStream objectOutputStream) throws IOException {
        if (!xmlMode) {
            objectOutputStream.writeUnshared(message);
            return;
        }
        XMLMessageExchanger.send(message, socket.getOutputStream());
    }
    public static Message recv(Socket socket, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (!xmlMode) {
            return (Message) objectInputStream.readObject();
        }
        try {
            return XMLMessageExchanger.recv(socket.getInputStream());
        } catch (SAXException e) {
            e.printStackTrace();
            System.exit(10);
        }
        return null;
    }
}

class XMLMessageExchanger {
    public static void send(Message message, OutputStream outputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.setLength(0);
        stringBuilder.append("<message>\n");
        stringBuilder.append("<id>").append(message.id).append("</id>\n");
        stringBuilder.append("<mt>").append(message.mesType).append("</mt>\n");
        switch (message.mesType) {
            case 1:
                stringBuilder.append("<tm>").append(message.textMessage).append("</tm>\n");
                break;
            case 2: case 3:
                stringBuilder.append("<x>").append(message.x).append("</x>\n");
                stringBuilder.append("<y>").append(message.y).append("</y>\n");
                stringBuilder.append("<h>").append(message.height).append("</h>\n");
                stringBuilder.append("<w>").append(message.width).append("</w>\n");
                stringBuilder.append("<t>").append(message.type).append("</t>\n");
                break;
            case 4:
                stringBuilder.append("<cns>");
                for (String s : message.colNames)
                    stringBuilder.append(s).append('\t');
                if (message.colNames.size() > 0)
                    stringBuilder.setLength(stringBuilder.length() - 1);
                stringBuilder.append("</cns>\n");
                stringBuilder.append("<table>");
                for (Vector<String> vs : message.table) {
                    for (String s : vs)
                        stringBuilder.append(s.replace("&", "&amp;")).append('\t');
                }
                if (message.table.size() > 0)
                    stringBuilder.setLength(stringBuilder.length() - 1);
                stringBuilder.append("</table>\n");
                stringBuilder.append("<sc>").append(message.score).append("</sc>\n");
                stringBuilder.append("<ft>").append(message.fullTime).append("</ft>\n");
                break;
            case 5:
                stringBuilder.append("<sp>").append(message.speed).append("</sp>\n");
                break;
        }
        stringBuilder.append("</message>");
        byte[] tmpBytes = stringBuilder.toString().getBytes();
        //System.out.println("+*************************\n" + stringBuilder.toString() + "\n*************************+" + tmpBytes.length);
        outputStream.write(tmpBytes.length / 256);
        outputStream.write(tmpBytes.length % 256);
        outputStream.write(tmpBytes);
        //System.out.println("sent");
    }
    public static Message recv(InputStream inputStream) throws IOException, SAXException {
        Document document = null;
        int docLength = inputStream.read() * 256;
        docLength += inputStream.read();
        if (docLength < 0) {
            System.out.println(docLength);
            throw new EOFException();
        }
        String tmpString = new String(inputStream.readNBytes(docLength));
        //System.out.println("+-------------------------------------------\n" + tmpString + "\n-------------------------------------------+");
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(tmpString)));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Message message = new Message();
        message.makeEmpty();
        message.id = Integer.parseInt(document.getElementsByTagName("id").item(0).getTextContent());
        message.mesType = Integer.parseInt(document.getElementsByTagName("mt").item(0).getTextContent());
        switch (message.mesType) {
            case 1:
                message.textMessage = document.getElementsByTagName("tm").item(0).getTextContent();
                break;
            case 2:
            case 3:
                message.x = Double.parseDouble(document.getElementsByTagName("x").item(0).getTextContent());
                message.y = Double.parseDouble(document.getElementsByTagName("y").item(0).getTextContent());
                message.height = Double.parseDouble(document.getElementsByTagName("h").item(0).getTextContent());
                message.width = Double.parseDouble(document.getElementsByTagName("w").item(0).getTextContent());
                message.type = Integer.parseInt(document.getElementsByTagName("t").item(0).getTextContent());
                break;
            case 4:
                String[] tmp = document.getElementsByTagName("cns").item(0).getTextContent().split("\t");
                message.colNames = new Vector<String>(tmp.length);
                for (String s : tmp)
                    message.colNames.add(s);
                tmp = document.getElementsByTagName("table").item(0).getTextContent().split("\t");
                Vector<String> tmpVec = null;
                message.table = new Vector<Vector<String>>();
                for (int i = 0; i < tmp.length; i += 3) {
                    tmpVec = new Vector<String>(3);
                    tmpVec.add(tmp[i]);
                    tmpVec.add(tmp[i + 1]);
                    tmpVec.add(tmp[i + 2]);
                    message.table.add(tmpVec);
                }
                message.score = Double.parseDouble(document.getElementsByTagName("sc").item(0).getTextContent());
                message.fullTime = Double.parseDouble(document.getElementsByTagName("ft").item(0).getTextContent());
                break;
            case 5:
                message.speed = Integer.parseInt(document.getElementsByTagName("sp").item(0).getTextContent());
                break;
        }
        return message;
    }
}
