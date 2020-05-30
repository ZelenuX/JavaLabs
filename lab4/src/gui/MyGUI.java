package gui;

import factory.Factory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.InvalidParameterException;

public class MyGUI {
    private JProgressBar casingsFillness;
    private JProgressBar motorsFillness;
    private JProgressBar accessoriesFillness;
    private JProgressBar autosFillness;
    private JTextField casingsTextField;
    private JTextField motorsTextField;
    private JTextField accessoriesTextField;
    private JTextField autosTextField;
    private JSlider casingSupplierSpeed;
    private JSlider motorSupplierSpeed;
    private JSlider accessorySuppliersSpeed;
    private JSlider dealersSpeed;
    private JTextField casingSupplierTextField;
    private JTextField motorSupplierTextField;
    private JTextField accessorySuppliersTextField;
    private JTextField dealersTextField;
    private JTextField producedAutos;
    private JTextField producedAutosTextField;
    private JPanel mainPanel;
    private JFrame mainFrame;
    private final int maxBarsValue = 100;

    private Factory factory;
    private Timer updateTimer;
    int initialSupplierAndDealerInterval;

    public MyGUI(Factory factory, int initialSupplierAndDealerInterval) throws InvalidParameterException {
        if (factory == null || initialSupplierAndDealerInterval <= 0)
            throw new InvalidParameterException();
        this.factory = factory;
        mainFrame = new JFrame("Factory");
        mainFrame.setContentPane(mainPanel);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                updateTimer.stop();
                factory.stop();
            }
        });
        updateTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                update();
            }
        });
        this.initialSupplierAndDealerInterval = initialSupplierAndDealerInterval;
        updateTimer.start();
    }
    public void setVisible(boolean visible){
        mainFrame.setVisible(visible);
    }
    private void update(){
        casingsFillness.setValue((int)(factory.getCasingsFillness() * maxBarsValue));
        motorsFillness.setValue((int)(factory.getMotorsFillness() * maxBarsValue));
        accessoriesFillness.setValue((int)(factory.getAccessoriesFillness() * maxBarsValue));
        autosFillness.setValue((int)(factory.getAutosFillness() * maxBarsValue));
        producedAutos.setText("" + factory.getNumberOfAutos());
        factory.setCasingSupplierInterval(getMs(casingSupplierSpeed));
        factory.setMotorSupplierInterval(getMs(motorSupplierSpeed));
        factory.setAccessorySuppliersInterval(getMs(accessorySuppliersSpeed));
        factory.setDealersInterval(getMs(dealersSpeed));
    }
    private int getMs(JSlider slider){
        return (int)(initialSupplierAndDealerInterval * 0.5 / (0.0 + (double)(slider.getValue() - slider.getMinimum()) / (slider.getMaximum() - slider.getMinimum())));
    }
}
