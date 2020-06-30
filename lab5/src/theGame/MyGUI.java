package theGame;

import theGame.Resources.ResourceLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class MyGUI implements UserInterface {
    private final Controller controller;
    private ResourceLoader resLoader = new ResourceLoader("C:\\Users\\il_ya\\IdeaProjects\\lab3\\src\\theGame\\Resources", "bmp");
    private JFrame mainFrame = new JFrame("TheGame");
    private int width, height;
    private BufferedImage fieldImage;   /**journey of rectangles and ovals: imageGraphics -> fieldImage -> fieldPanel -> mainFrame*/
    private BufferedImage background;
    private Graphics imageGraphics;
    private boolean gameEnded = false;
    String playerName;
    protected String recordTablePath = "C:\\Users\\il_ya\\IdeaProjects\\lab3\\src\\theGame\\results.txt";
    private final JPanel fieldPanel = new JPanel() {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, null);
            g.drawImage(fieldImage, 0, 0, null);
        }
    };

    private Runnable jobForUIProcess = new Runnable() {
        @Override
        public void run() {
            fieldPanel.repaint();
        }
    };
    private void askName(){
        class AkaBoolean{
            boolean value;
            public AkaBoolean(boolean initValue){
                value = initValue;
            }
            public void setValue(boolean value) {
                this.value = value;
            }
        };
        AkaBoolean nameEntered = new AkaBoolean(false);
        JFrame askFrame = new JFrame();
        askFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        askFrame.setLayout(null);
        askFrame.setBounds((width - 400) / 2, (height - 150) / 2, 400, 150);
        askFrame.add(new JLabel("/"));
        JLabel label = new JLabel("Enter player name:");
        label.setBounds(0, 0, 120, 25);
        askFrame.add(label);
        JTextField input = new JTextField();
        input.setBounds(120, 0, 250, 25);
        askFrame.add(input);
        JButton confirm = new JButton("confirm");
        confirm.setBounds(0, 25, 385, 86);
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                nameEntered.setValue(true);
            }
        });
        askFrame.add(confirm);
        askFrame.setVisible(true);
        while (!nameEntered.value){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        playerName = input.getText();
        mainFrame.setTitle("Great " + playerName + " plays");
        askFrame.dispose();
    }
    public MyGUI(int width, int height, Controller controller) throws IOException {
        if (controller == null)
            throw new InvalidParameterException();
        this.controller = controller;
        if (width < 100 || height < 100 || width > 1280 || height > 1024)
            throw new InvalidParameterException();
        this.width = width;
        this.height = height;

        askName();

        fieldImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        imageGraphics = fieldImage.getGraphics();
        drawRectangle(0, 0, 1, 1, RectangleTypes.EMPTY);

        background = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        background.getGraphics().drawImage(ImageIO.read(new File("C:\\Users\\il_ya\\IdeaProjects\\lab3\\src\\theGame\\Resources\\background.bmp")), 0, 0, width, height, null);

        fieldPanel.setBounds(0, 0, width, height);

        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {}
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                switch(keyEvent.getKeyCode()){
                    case KeyEvent.VK_RIGHT: case KeyEvent.VK_D:
                        controller.platformMoveRight();
                        break;
                    case KeyEvent.VK_LEFT: case KeyEvent.VK_A:
                        controller.platformMoveLeft();
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                switch(keyEvent.getKeyCode()){
                    case KeyEvent.VK_RIGHT: case KeyEvent.VK_D:
                    case KeyEvent.VK_LEFT: case KeyEvent.VK_A:
                        controller.stopPlatform();
                        break;
                }
            }
        };

        JButton helpButton = new JButton("GOD HELP");
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                controller.godHelp();
            }
        });
        helpButton.setBounds(0, height, width / 2, 50);
        helpButton.addKeyListener(keyListener);


        JButton restartButton = new JButton("Restart");
        restartButton.setBounds(width / 2, height, width / 2, 50);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                restart();
            }
        });
        restartButton.addKeyListener(keyListener);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(fieldPanel);
        mainFrame.add(helpButton);
        mainFrame.add(restartButton);
        mainFrame.setSize(width + 14, height + 85);
        mainFrame.setMaximumSize(mainFrame.getSize());
        mainFrame.setMinimumSize(mainFrame.getSize());
        mainFrame.setLayout(null);
        mainFrame.setIconImage(resLoader.loadRectangle(1));
        mainFrame.setVisible(true);
    }

    private static Composite transparentComposite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
    private static Composite nonTransparentComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
    @Override
    public void drawRectangle(double x, double y, double width, double height, int type) throws InvalidParameterException {
        BufferedImage rectImage;
        try {
            switch (type) {
                case RectangleTypes.EMPTY:
                    ((Graphics2D)imageGraphics).setComposite(transparentComposite);
                    imageGraphics.fillRect((int)(x * this.width), (int)(y * this.height),
                            (int)((x + width) * this.width + 0.00001) - (int)(x * this.width), (int)((y + height) * this.height + 0.00001) - (int)(y * this.height));
                    ((Graphics2D)imageGraphics).setComposite(nonTransparentComposite);
                    return;
                case RectangleTypes.ONE_POINT:
                    rectImage = resLoader.loadRectangle(1);
                    break;
                case RectangleTypes.TWO_POINTS:
                    rectImage = resLoader.loadRectangle(2);
                    break;
                case RectangleTypes.THREE_POINTS:
                    rectImage = resLoader.loadRectangle(3);
                    break;
                case RectangleTypes.FOUR_POINTS:
                    rectImage = resLoader.loadRectangle(4);
                    break;
                case RectangleTypes.FIVE_POINTS:
                    rectImage = resLoader.loadRectangle(5);
                    break;
                case RectangleTypes.HIGH_POINTS:
                    rectImage = resLoader.loadRectangle(6);
                    break;
                case RectangleTypes.BASE:
                    rectImage = resLoader.loadRectangle(7);
                    break;
                default:
                    throw new InvalidParameterException();
            }
        }
        catch (IOException e){
            System.err.println("ERROR: Can not read image file.");
            throw new RuntimeException();
        }
        imageGraphics.drawImage(rectImage, (int)(x * this.width), (int)(y * this.height),
                (int)((x + width) * this.width + 0.00001) - (int)(x * this.width), (int)((y + height) * this.height + 0.00001) - (int)(y * this.height), null);
    }
    @Override
    public void drawOval(double x, double y, double width, double height, int type) throws InvalidParameterException {
        switch (type){
            case OvalTypes.EMPTY:
                ((Graphics2D)imageGraphics).setComposite(transparentComposite);
                break;
            case OvalTypes.REGULAR:
                imageGraphics.setColor(Color.BLUE);
                break;
            default:
                throw new InvalidParameterException();
        }
        x -= width / 2;
        y -= height / 2;
        imageGraphics.fillOval((int)(x * this.width), (int)(y * this.height),
                (int)((x + width) * this.width + 0.00001) - (int)(x * this.width), (int)((y + height) * this.height + 0.00001) - (int)(y * this.height));
        if (type == OvalTypes.EMPTY)
            ((Graphics2D)imageGraphics).setComposite(nonTransparentComposite);
    }
    @Override
    public void nextMoment(double passedSeconds) throws FailException, WinException, IOException {
        if (gameEnded)
            return;
        try {
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
        SwingUtilities.invokeLater(jobForUIProcess);
    }
    @Override
    public void end(String message) throws IOException {
        RecordTable recordTable = new RecordTable(recordTablePath);
        String[] timeAndResult = recordTable.getTimeAndResult(playerName);
        if (timeAndResult == null || (double)(int)(controller.getScore() * 100000) / 1000 > Double.valueOf(timeAndResult[1].split("%")[0]))
            recordTable.set(playerName,
                    (double)(int)(controller.getFullTime() * 100) / 100 + "s", (double)(int)(controller.getScore() * 100000) / 1000 + "%");
        gameEnded = true;
        JFrame endFrame = new JFrame();
        endFrame.setLayout(null);
        endFrame.setTitle(message);
        JButton restartButton = new JButton("again?");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                endFrame.dispose();
                restart();
            }
        });
        restartButton.setBounds(0, 0, 255, 60);
        Vector<String> colNames = new Vector<>();
        colNames.add("player name");
        colNames.add("time");
        colNames.add("result");
        JTable resTable = new JTable(recordTable.getStrs(), colNames){
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };
        recordTable.leaveBest(5);
        recordTable.updateFile();
        resTable.setBounds(0, 65, 255, 101);
        endFrame.add(resTable);
        endFrame.add(restartButton);
        endFrame.setBounds((width + 14 - 270) / 2, (height - 395) / 2, 270, 200);
        endFrame.setIconImage(resLoader.loadRectangle(1));
        endFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        endFrame.setVisible(true);
    }
    @Override
    public String getName() {
        return playerName;
    }

    private void restart(){
        controller.restart();
        gameEnded = false;
    }
}
