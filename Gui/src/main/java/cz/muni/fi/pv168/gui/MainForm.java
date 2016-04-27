package cz.muni.fi.pv168.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by cechy on 26.04.2016.
 */
public class MainForm {
    private JPanel panel1;
    private JButton sinnersButton;
    private JButton cauldronsButton;
    private JButton exitButton;

    public MainForm() {
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
        sinnersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Sinners dialog = new Sinners();
                dialog.pack();
                dialog.setVisible(true);
            }
        });
        cauldronsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Cauldrons dialog = new Cauldrons();
                dialog.pack();
                dialog.setVisible(true);
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Hell Manager");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setContentPane(new MainForm().panel1);
                frame.setPreferredSize(new Dimension(800,600));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
